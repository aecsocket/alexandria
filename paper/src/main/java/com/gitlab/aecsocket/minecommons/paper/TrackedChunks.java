package com.gitlab.aecsocket.minecommons.paper;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds information on what chunks are visible by what players.
 */
public final class TrackedChunks {
    private final class EventHandle implements Listener {
        @EventHandler
        private void onEvent(PlayerQuitEvent event) {
            tracked.remove(event.getPlayer());
        }

        @EventHandler
        private void onEvent(PlayerChunkLoadEvent event) {
            tracked.computeIfAbsent(event.getPlayer(), p -> new LongOpenHashSet()).add(event.getChunk().getChunkKey());
        }

        @EventHandler
        private void onEvent(PlayerChunkUnloadEvent event) {
            tracked.computeIfAbsent(event.getPlayer(), p -> new LongOpenHashSet()).remove(event.getChunk().getChunkKey());
        }
    }

    private final MinecommonsPlugin plugin;
    private final Map<Player, LongSet> tracked = new HashMap<>();

    TrackedChunks(MinecommonsPlugin plugin) {
        this.plugin = plugin;
    }

    void enable() {
        Bukkit.getPluginManager().registerEvents(new EventHandle(), plugin);
    }

    /**
     * Gets the set of chunk keys tracked by a player currently.
     * @param player The player.
     * @return The set of chunk keys.
     */
    public LongSet tracked(Player player) { return tracked.getOrDefault(player, new LongOpenHashSet()); }
}
