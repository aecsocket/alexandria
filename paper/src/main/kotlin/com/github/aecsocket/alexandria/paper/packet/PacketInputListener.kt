package com.github.aecsocket.alexandria.paper.packet

import com.github.aecsocket.alexandria.core.Input
import com.github.aecsocket.alexandria.core.Input.*
import com.github.aecsocket.alexandria.core.Input.Companion.MENU_ADVANCEMENTS
import com.github.aecsocket.alexandria.core.Input.Companion.MENU_HORSE
import com.github.aecsocket.alexandria.core.Input.Companion.MOUSE_DOWN
import com.github.aecsocket.alexandria.core.Input.Companion.MOUSE_LEFT
import com.github.aecsocket.alexandria.core.Input.Companion.MOUSE_RIGHT
import com.github.aecsocket.alexandria.core.Input.Companion.MOUSE_UNDEFINED
import com.github.aecsocket.alexandria.core.Input.Companion.MOUSE_UP
import com.github.aecsocket.alexandria.core.Input.Companion.SCROLL_DOWN
import com.github.aecsocket.alexandria.core.Input.Companion.SCROLL_UP
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client
import com.github.retrooper.packetevents.protocol.player.DiggingAction
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAdvancementTab
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem
import net.minecraft.world.item.UseAnim
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import java.util.UUID

class PacketInputListener(
    val callback: (Event) -> Unit
) : PacketListener {
    interface Event {
        val input: Input
        val player: Player

        fun cancel()
    }

    private val lastDropped = HashMap<UUID, Int>()

    private fun dropping(player: Player) = lastDropped[player.uniqueId] == Bukkit.getCurrentTick()

    private fun dropped(player: Player) {
        lastDropped[player.uniqueId] = Bukkit.getCurrentTick()
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.player
        if (player !is Player || !player.isValid)
            return

        data class EventImpl(
            override val input: Input,
            val onCancel: () -> Unit
        ) : Event {
            override val player: Player
                get() = player

            override fun cancel() {
                event.isCancelled = true
                onCancel()
            }
        }

        fun call(input: Input, onCancel: () -> Unit = {}) = callback(EventImpl(input, onCancel))

        when (event.packetType) {
            Client.ANIMATION -> {
                val packet = WrapperPlayClientAnimation(event)
                player.sendMessage(" > hand = ${packet.hand} | dropping = ${dropping(player)}")
                if (packet.hand == InteractionHand.MAIN_HAND && !dropping(player)) {
                    player.sendMessage(" > called")
                    call(Mouse(MOUSE_LEFT, MOUSE_UNDEFINED))
                }
            }
            Client.USE_ITEM -> {
                val packet = WrapperPlayClientUseItem(event)
                if (packet.hand == InteractionHand.MAIN_HAND) {
                    // if the item has an animation (e.g. eating, using bow)
                    // client will later send a PLAYER_BLOCK_PLACEMENT to cancel this action
                    call(Mouse(MOUSE_RIGHT, when ((player.inventory.itemInMainHand as CraftItemStack).handle.useAnimation) {
                        UseAnim.NONE -> MOUSE_UNDEFINED
                        else -> MOUSE_DOWN
                    }))
                }
            }
            Client.PLAYER_BLOCK_PLACEMENT -> {
                val packet = WrapperPlayClientPlayerBlockPlacement(event)
                if (packet.hand == InteractionHand.MAIN_HAND) {
                    call(Mouse(MOUSE_RIGHT, MOUSE_UNDEFINED))
                }
            }
            Client.PLAYER_DIGGING -> {
                val packet = WrapperPlayClientPlayerDigging(event)
                when (packet.action) {
                    DiggingAction.SWAP_ITEM_WITH_OFFHAND -> call(SwapHands)

                    DiggingAction.DROP_ITEM,
                    DiggingAction.DROP_ITEM_STACK -> {
                        call(Drop)
                        dropped(player)
                    }

                    DiggingAction.START_DIGGING -> call(Mouse(MOUSE_LEFT, MOUSE_DOWN))
                    DiggingAction.CANCELLED_DIGGING,
                    DiggingAction.FINISHED_DIGGING -> call(Mouse(MOUSE_LEFT, MOUSE_UP))

                    DiggingAction.RELEASE_USE_ITEM -> call(Mouse(MOUSE_RIGHT, MOUSE_UP))
                    else -> {}
                }
            }
            Client.HELD_ITEM_CHANGE -> {
                val packet = WrapperPlayClientHeldItemChange(event)
                val next = packet.slot
                val last = player.inventory.heldItemSlot
                call(HeldItem(scrollDirection(next, last))) {
                    player.inventory.heldItemSlot = last
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
                    WrapperPlayClientEntityAction.Action.OPEN_HORSE_INVENTORY -> Menu(MENU_HORSE, true)
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
                    WrapperPlayClientAdvancementTab.Action.OPENED_TAB -> Menu(MENU_ADVANCEMENTS, true)
                    WrapperPlayClientAdvancementTab.Action.CLOSED_SCREEN -> Menu(MENU_ADVANCEMENTS, false)
                    else -> null
                }?.let { call(it) }
            }
        }
    }
}

fun scrollDirection(next: Int, last: Int): Int {
    return if (next < last && next != 0 && last != 8) SCROLL_DOWN
        else if (next > last && next != 8 && last != 0) SCROLL_UP
        else 0
}
