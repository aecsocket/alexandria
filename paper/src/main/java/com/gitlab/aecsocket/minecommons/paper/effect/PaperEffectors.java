package com.gitlab.aecsocket.minecommons.paper.effect;

import com.gitlab.aecsocket.minecommons.core.effect.Effector;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Allows creation of Paper implementations of effectors.
 */
public class PaperEffectors {
    private final Plugin plugin;

    public PaperEffectors(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the plugin used by this manager.
     * @return The plugin.
     */
    public Plugin plugin() { return plugin; }

    /**
     * Gets an effector for a player.
     * @param player The player.
     * @return The effector.
     */
    public Effector ofPlayer(Player player) {
        return new PlayerEffector(this, player);
    }

    /**
     * Gets an effector for a world, forwarding to all players in the world.
     * @param world The world.
     * @return The effector.
     */
    public Effector ofWorld(World world) {
        return new WorldEffector(this, world);
    }
}
