package com.gitlab.aecsocket.alexandria.paper.input

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client
import com.github.retrooper.packetevents.protocol.player.DiggingAction
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.client.*
import com.gitlab.aecsocket.alexandria.core.input.Input
import com.gitlab.aecsocket.alexandria.core.input.Input.*
import com.gitlab.aecsocket.alexandria.core.input.Input.MenuType.ADVANCEMENTS
import com.gitlab.aecsocket.alexandria.core.input.Input.MenuType.HORSE
import com.gitlab.aecsocket.alexandria.core.input.Input.MouseButton.LEFT
import com.gitlab.aecsocket.alexandria.core.input.Input.MouseButton.RIGHT
import com.gitlab.aecsocket.alexandria.paper.alexandria
import com.gitlab.aecsocket.alexandria.paper.extension.bukkitCurrentTick
import net.minecraft.server.level.ServerPlayerGameMode
import net.minecraft.world.item.UseAnim
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandles

class PacketInputListener(
    val callback: (InputEvent) -> Unit,
) : PacketListener {
    private val hIsDestroying = MethodHandles
        .privateLookupIn(ServerPlayerGameMode::class.java, MethodHandles.lookup())
        .findVarHandle(
            ServerPlayerGameMode::class.java,
            "f", // isDestroyingBlock
            Boolean::class.javaPrimitiveType
        )

    private fun isSwinging(player: Player) = player.alexandria.lastSwing == bukkitCurrentTick

    private fun markSwinging(player: Player) {
        player.alexandria.lastSwing = bukkitCurrentTick
    }

    private fun isClicking(player: Player) = player.alexandria.lastClick == bukkitCurrentTick

    private fun markClicking(player: Player) {
        player.alexandria.lastClick = bukkitCurrentTick
    }

    private fun isDigging(player: Player) = player.alexandria.digging

    private fun setDigging(player: Player, value: Boolean) {
        player.alexandria.digging = value
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.player as? Player ?: return
        if (!player.isValid) return

        fun call(input: Input, cancel: () -> Unit = {}) {
            callback(InputEvent(player, input) {
                event.isCancelled = true
                cancel()
            })
        }

        when (event.packetType) {
            Client.ANIMATION -> {
                val packet = WrapperPlayClientAnimation(event)
                if (
                    packet.hand == InteractionHand.MAIN_HAND
                    && !isSwinging(player)
                    && !isClicking(player)
                    && !isDigging(player)
                ) {
                    call(Mouse(LEFT, MouseState.UNDEFINED))
                }
            }
            Client.USE_ITEM -> {
                val packet = WrapperPlayClientUseItem(event)
                if (packet.hand == InteractionHand.MAIN_HAND && !isClicking(player)) {
                    // if the item has an animation (e.g. eating, using bow)
                    // client will later send a PLAYER_BLOCK_PLACEMENT to cancel this action
                    call(Mouse(RIGHT, when ((player.inventory.itemInMainHand as CraftItemStack).handle?.useAnimation) {
                        UseAnim.NONE -> MouseState.UNDEFINED
                        else -> MouseState.DOWN
                    }))
                }
            }
            // wiki.vg: Use Item On
            Client.PLAYER_BLOCK_PLACEMENT -> {
                val packet = WrapperPlayClientPlayerBlockPlacement(event)
                markClicking(player)
                if (packet.hand == InteractionHand.MAIN_HAND) {
                    call(Mouse(RIGHT, MouseState.UNDEFINED))
                }
            }
            // wiki.vg: Player Action
            Client.PLAYER_DIGGING -> {
                val packet = WrapperPlayClientPlayerDigging(event)
                when (packet.action) {
                    DiggingAction.SWAP_ITEM_WITH_OFFHAND -> call(SwapHands)

                    DiggingAction.DROP_ITEM,
                    DiggingAction.DROP_ITEM_STACK -> {
                        markSwinging(player)
                        call(Drop)
                    }

                    DiggingAction.START_DIGGING -> {
                        setDigging(player, true)
                        call(Mouse(LEFT, MouseState.DOWN))
                    }
                    DiggingAction.CANCELLED_DIGGING,
                    DiggingAction.FINISHED_DIGGING -> {
                        setDigging(player, false)
                        call(Mouse(LEFT, MouseState.UP))
                    }

                    DiggingAction.RELEASE_USE_ITEM -> call(Mouse(RIGHT, MouseState.UP))
                    else -> {}
                }
            }
            Client.HELD_ITEM_CHANGE -> {
                val packet = WrapperPlayClientHeldItemChange(event)
                val next = packet.slot
                val last = player.inventory.heldItemSlot
                scrollDirection(next, last)?.let { direction ->
                    call(HeldItem(direction)) {
                        player.inventory.heldItemSlot = last
                    }
                }
            }
            Client.ENTITY_ACTION -> {
                val packet = WrapperPlayClientEntityAction(event)
                when (packet.action) {
                    WrapperPlayClientEntityAction.Action.START_SNEAKING -> Sneak(true)
                    WrapperPlayClientEntityAction.Action.STOP_SNEAKING -> Sneak(false)
                    WrapperPlayClientEntityAction.Action.START_SPRINTING -> Sprint(true)
                    WrapperPlayClientEntityAction.Action.STOP_SPRINTING -> Sprint(false)
                    WrapperPlayClientEntityAction.Action.LEAVE_BED -> LeaveBed
                    WrapperPlayClientEntityAction.Action.OPEN_HORSE_INVENTORY -> Menu(HORSE, true)
                    WrapperPlayClientEntityAction.Action.START_JUMPING_WITH_HORSE -> HorseJump(true)
                    WrapperPlayClientEntityAction.Action.STOP_JUMPING_WITH_HORSE -> HorseJump(false)
                    WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA -> ElytraFlight
                    else -> null
                }?.let { call(it) }
            }
            Client.PLAYER_ABILITIES -> {
                val packet = WrapperPlayClientPlayerAbilities(event)
                call(Flight(packet.isFlying))
            }
            Client.ADVANCEMENT_TAB -> {
                val packet = WrapperPlayClientAdvancementTab(event)
                when (packet.action) {
                    WrapperPlayClientAdvancementTab.Action.OPENED_TAB -> Menu(ADVANCEMENTS, true)
                    WrapperPlayClientAdvancementTab.Action.CLOSED_SCREEN -> Menu(ADVANCEMENTS, false)
                    else -> null
                }?.let { call(it) }
            }
        }
    }
}
