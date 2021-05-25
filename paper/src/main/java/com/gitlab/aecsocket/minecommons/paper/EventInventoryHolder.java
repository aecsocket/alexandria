package com.gitlab.aecsocket.minecommons.paper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

public interface EventInventoryHolder extends InventoryHolder {
    final class EventListener implements Listener {
        private final Plugin plugin;

        public EventListener(Plugin plugin) {
            this.plugin = plugin;
        }

        public Plugin plugin() { return plugin; }

        private void handle(InventoryEvent event) {
            if (
                    event.getInventory().getHolder() instanceof EventInventoryHolder holder
                    && holder.plugin().equals(plugin)
            ) {
                holder.event(event);
            }
        }

        @EventHandler public void onEvent(InventoryClickEvent event) { handle(event); }
        @EventHandler public void onEvent(InventoryDragEvent event) { handle(event); }
        @EventHandler public void onEvent(InventoryCloseEvent event) { handle(event); }
    }

    Plugin plugin();
    void event(InventoryEvent event);
}
