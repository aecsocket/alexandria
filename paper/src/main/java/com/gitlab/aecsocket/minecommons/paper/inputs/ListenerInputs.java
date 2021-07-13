package com.gitlab.aecsocket.minecommons.paper.inputs;

import com.gitlab.aecsocket.minecommons.core.InputType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Handles player inputs through an event listener.
 * <p>
 * Uses:
 * <ul>
 *     <li>{@link PlayerAnimationEvent}: {@link InputType#MOUSE_LEFT}</li>
 *     <li>{@link PlayerInteractEvent}: {@link InputType#MOUSE_RIGHT}</li>
 *     <li>{@link PlayerSwapHandItemsEvent}: {@link InputType#OFFHAND}</li>
 *     <li>{@link PlayerDropItemEvent}: {@link InputType#DROP}</li>
 * </ul>
 */
public class ListenerInputs extends AbstractInputs implements Listener {
    @EventHandler
    private void onEvent(PlayerAnimationEvent event) {
        handle(event.getPlayer(), InputType.MOUSE_LEFT, () -> event.setCancelled(true));
    }

    @EventHandler
    private void onEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND &&
                (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            handle(event.getPlayer(), InputType.MOUSE_RIGHT, () -> event.setCancelled(true));
        }
    }

    @EventHandler
    private void onEvent(PlayerSwapHandItemsEvent event) {
        handle(event.getPlayer(), InputType.OFFHAND, () -> event.setCancelled(true));
    }

    @EventHandler
    private void onEvent(PlayerDropItemEvent event) {
        handle(event.getPlayer(), InputType.DROP, () -> event.setCancelled(true));
    }
}
