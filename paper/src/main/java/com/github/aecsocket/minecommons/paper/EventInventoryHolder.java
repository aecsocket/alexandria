package com.github.aecsocket.minecommons.paper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

/**
 * Allows running code on an inventory event, through an inventory holder.
 */
public interface EventInventoryHolder extends InventoryHolder {
    /**
     * The listener for the holder.
     */
    final class EventListener implements Listener {
        private final Plugin plugin;

        /**
         * Creates an instance.
         * @param plugin The plugin.
         */
        public EventListener(Plugin plugin) {
            this.plugin = plugin;
        }

        /**
         * Gets the plugin.
         * @return The plugin.
         */
        public Plugin plugin() { return plugin; }

        private void handle(InventoryEvent event) {
            if (
                event.getInventory().getHolder() instanceof EventInventoryHolder holder
                && holder.plugin().equals(plugin)
            ) {
                holder.event(event);
            }
        }

        @EventHandler private void onEvent(InventoryClickEvent event) { handle(event); }
        @EventHandler private void onEvent(InventoryDragEvent event) { handle(event); }
        @EventHandler private void onEvent(InventoryCloseEvent event) { handle(event); }
    }

    /**
     * Gets the plugin used to register event listeners.
     * @return The plugin.
     */
    Plugin plugin();

    /**
     * Runs when an event is called.
     * @param event The event.
     */
    void event(InventoryEvent event);
}
