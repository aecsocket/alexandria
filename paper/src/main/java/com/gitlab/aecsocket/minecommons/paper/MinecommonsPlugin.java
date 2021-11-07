package com.gitlab.aecsocket.minecommons.paper;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Minecommons plugin class.
 */
public final class MinecommonsPlugin extends JavaPlugin {
    private static MinecommonsPlugin instance;

    /**
     * Gets the global instance of this plugin.
     * @return The instance.
     */
    public static MinecommonsPlugin instance() { return instance; }

    private final TrackedChunks trackedChunks = new TrackedChunks(this);

    @Override
    public void onEnable() {
        instance = this;
        trackedChunks.enable();
    }

    /**
     * Gets the tracked chunks, allowing reading of what chunks are tracked by a player.
     * @return The tracked chunks.
     */
    public TrackedChunks trackedChunks() { return trackedChunks; }
}
