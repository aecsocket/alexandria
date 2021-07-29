package com.gitlab.aecsocket.minecommons.paper.display;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Stores information on how to spawn particles to a client.
 * @param particle The particle to display.
 * @param count The amount of particles, or {@code 0} for a single finely-controllable particle.
 * @param size The size.
 * @param speed The speed.
 * @param data The data.
 */
public record Particles(
        Particle particle,
        int count,
        Vector3 size,
        double speed,
        @Nullable Object data
) {
    /**
     * Creates particle data.
     * @param particle The particle to display.
     * @param count The amount of particles, or {@code 0} for a single finely-controllable particle.
     * @param size The size.
     * @param speed The speed {@code >= 0}.
     * @param data The data.
     * @return The particle data.
     */
    public static Particles particles(Particle particle, int count, Vector3 size, double speed, @Nullable Object data) {
        Validation.notNull("particle", particle);
        Validation.greaterThanEquals("count", count, 0);
        Validation.notNull("size", size);
        Validation.greaterThanEquals("speed", speed, 0);
        return new Particles(particle, count, size, speed, data);
    }

    /**
     * Creates particle data, with no data.
     * @param particle The particle to display.
     * @param count The amount of particles, or {@code 0} for a single finely-controllable particle.
     * @param size The size.
     * @param speed The speed.
     * @return The particle data.
     */
    public static Particles particles(Particle particle, int count, Vector3 size, double speed) {
        return particles(particle, count, size, speed, null);
    }

    private Object data(@Nullable Object data) {
        return data == null || !particle.getDataType().isAssignableFrom(data.getClass())
                ? null : data;
    }

    /**
     * Spawns the particles to a specified player at a location, with data provided.
     * @param player The player to spawn to.
     * @param origin The location of the particles.
     * @param data The particle data.
     */
    public void spawn(Player player, Location origin, @Nullable Object data) {
        player.spawnParticle(particle, origin, count, size.x(), size.y(), size.z(), data(data));
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
    public void spawn(Location origin, @Nullable Object data) {
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
