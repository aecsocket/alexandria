package com.gitlab.aecsocket.minecommons.inputs;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Handles player inputs through an event listener.
 * <p>
 * Uses:
 * <ul>
 * <li>{@link PlayerAnimationEvent}: {@link Inputs.Input#LEFT}</li>
 * <li>{@link PlayerInteractEvent}: {@link Inputs.Input#RIGHT}</li>
 * </ul>
 */
public class ListenerInputs extends AbstractInputs implements Listener {
    @EventHandler
    public void onEvent(PlayerAnimationEvent event) {
        handle(event.getPlayer(), Input.LEFT, () -> event.setCancelled(true));
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND &&
                (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            handle(event.getPlayer(), Input.RIGHT, () -> event.setCancelled(true));
        }
    }
}
