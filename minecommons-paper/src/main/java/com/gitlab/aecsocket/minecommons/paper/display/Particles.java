package com.gitlab.aecsocket.minecommons.paper.display;

import com.gitlab.aecsocket.minecommons.core.Validation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Stores information on how to spawn particles to a client.
 */
public record Particles(
        Particle particle,
        int count,
        double dx,
        double dy,
        double dz,
        double speed,
        Object data
) {
    public Particles {
        Validation.notNull(particle, "particle");
        Validation.greaterThanEquals("count", count, 0);
        Validation.greaterThanEquals("speed", speed, 0);
    }

    public Particles(Particle particle, int count, double dx, double dy, double dz, double speed) {
        this(particle, count, dx, dy, dz, speed, null);
    }

    public Particles(Particle particle, int count, double dx, double dy, double dz) {
        this(particle, count, dx, dy, dz, 0, null);
    }

    public Particles(Particle particle, int count) {
        this(particle, count, 0, 0, 0, 0, null);
    }

    public Particles(Particle particle) {
        this(particle, 0, 0, 0, 0, 0, null);
    }

    /**
     * Spawns the particles to a specified player at a location, with data provided.
     * @param player The player to spawn to.
     * @param origin The location of the particles.
     * @param data The particle data.
     */
    public void spawn(Player player, Location origin, Object data) {
        player.spawnParticle(particle, origin, count, dx, dy, dz, speed, data);
    }

    /**
     * Spawns the particles to a specified player at a location, with this instance's data used.
     * @param player The player to spawn to.
     * @param origin The location of the particles.
     */
    public void spawn(Player player, Location origin) {
        spawn(player, origin, data);
    }

    /**
     * Spawns the particles to all players in a world at a location, with data provided.
     * @param origin The location of the particles.
     * @param data The particle data.
     */
    public void spawn(Location origin, Object data) {
        for (Player player : origin.getWorld().getPlayers()) {
            spawn(player, origin, data);
        }
    }

    /**
     * Spawns the particles to all players in a world at a location, with this instance's data used.
     * @param origin The location of the particles.
     */
    public void spawn(Location origin) {
        for (Player player : origin.getWorld().getPlayers()) {
            spawn(player, origin);
        }
    }
}
