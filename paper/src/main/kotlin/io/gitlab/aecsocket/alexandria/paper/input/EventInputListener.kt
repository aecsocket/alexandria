package io.gitlab.aecsocket.alexandria.paper.input

import io.gitlab.aecsocket.alexandria.core.input.Input
import io.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin

class EventInputListener(
    private val callback: (InputEvent) -> Unit
) {
    fun enable(plugin: Plugin) {
        plugin.registerEvents(object : Listener {
            fun call(player: Player, input: Input, cancel: () -> Unit) {
                callback(InputEvent(player, input, cancel))
            }

            @EventHandler
            fun on(event: PlayerInteractEvent) {
                if (event.hand != EquipmentSlot.HAND) return
                call(event.player, Input.Mouse(
                    if (event.action.isLeftClick) Input.MouseButton.LEFT else Input.MouseButton.RIGHT,
                    Input.MouseState.UNDEFINED
                )) { event.isCancelled = true }
            }

            @EventHandler
            fun on(event: PlayerSwapHandItemsEvent) {
                call(event.player, Input.SwapHands) { event.isCancelled = true }
            }

            @EventHandler
            fun on(event: PlayerDropItemEvent) {
                call(event.player, Input.Drop) { event.isCancelled = true }
            }

            @EventHandler
            fun on(event: PlayerItemHeldEvent) {
                val direction = scrollDirection(event.newSlot, event.previousSlot) ?: return
                call(event.player, Input.HeldItem(direction)) { event.isCancelled = true }
            }

            @EventHandler
            fun on(event: PlayerToggleSneakEvent) {
                call(event.player, Input.Sneak(event.isSneaking)) {}
            }

            @EventHandler
            fun on(event: PlayerToggleSprintEvent) {
                call(event.player, Input.Sneak(event.isSprinting)) { event.isCancelled = true }
            }

            @EventHandler
            fun on(event: PlayerToggleFlightEvent) {
                call(event.player, Input.Sneak(event.isFlying)) { event.isCancelled = true }
            }

            @EventHandler
            fun on(event: PlayerBedLeaveEvent) {
                call(event.player, Input.LeaveBed) { event.isCancelled = true }
            }
        })
    }
}
