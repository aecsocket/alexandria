package com.gitlab.aecsocket.minecommons.paper.display;

import com.gitlab.aecsocket.minecommons.core.Numbers;
import com.gitlab.aecsocket.minecommons.core.Ticks;
import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.gitlab.aecsocket.minecommons.paper.PaperUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A sound whose volume over distance can be accurately controlled, and simulates speed-of-sound.
 * @param name The namespaced key of the sound, sent to clients.
 * @param source The source.
 * @param volume The volume.
 * @param pitch The pitch.
 * @param dropoff The distance at which volume starts to decrease.
 * @param dropoffSqr The square dropoff.
 * @param range The distance at which the sound is inaudible.
 * @param rangeSqr The square range.
 * @param speed The speed, in m/t, of the sound.
 */
public record PreciseSound(
        Key name,
        Sound.Source source,
        float volume,
        float pitch,
        double dropoff,
        double dropoffSqr,
        double range,
        double rangeSqr,
        double speed
) {
    /** The default speed of sound, in m/s. */
    public static final double SPEED_MS = 340.29;
    /** The default speed of sound, in m/t. */
    public static final double SPEED_MT = SPEED_MS / Ticks.TPS;
    /** The distance at which sounds will be played away from a player. */
    public static final double OFFSET_DISTANCE = 10;

    /**
     * Creates a sound.
     * @param name The namespaced key of the sound, sent to clients.
     * @param source The source.
     * @param volume The volume.
     * @param pitch The pitch.
     * @param dropoff The distance at which volume starts to decrease.
     * @param range The distance at which the sound is inaudible.
     * @param speed The speed, in m/t, of the sound.
     * @return The sound.
     */
    public static PreciseSound sound(Key name, Sound.Source source, float volume, float pitch, double dropoff, double range, double speed) {
        Validation.notNull("name", name);
        Validation.notNull("source", source);
        Validation.greaterThanEquals("volume", volume, 0);
        Validation.greaterThanEquals("pitch", pitch, 0);
        Validation.greaterThanEquals("dropoff", dropoff, 0);
        Validation.greaterThanEquals("rangeSqr", range, 0);
        Validation.greaterThan("speed", speed, 0);
        return new PreciseSound(name, source, volume, pitch, dropoff, dropoff*dropoff, range, range*range, speed);
    }

    /**
     * Creates a sound, using the default speed {@link #SPEED_MT}.
     * @param name The namespaced key of the sound, sent to clients.
     * @param source The source.
     * @param volume The volume.
     * @param pitch The pitch.
     * @param dropoff The distance at which volume starts to decrease.
     * @param range The distance at which the sound is inaudible.
     * @return The sound.
     */
    public static PreciseSound sound(Key name, Sound.Source source, float volume, float pitch, double dropoff, double range) {
        return sound(name, source, volume, pitch, dropoff, range, SPEED_MT);
    }


    /**
     * Plays the sound to an audience.
     * @param audience The audience.
     * @param x The X component of the origin location.
     * @param y The Y component of the origin location.
     * @param z The Z component of the origin location.
     */
    public void play(Audience audience, double x, double y, double z) {
        audience.playSound(Sound.sound(name, source, volume, pitch), x, y, z);
    }

    /**
     * Plays a sound internally.
     * @param player The player.
     * @param deltaSqr The distance from source, squared.
     * @param origin The origin of the sound.
     */
    private void play(Player player, double deltaSqr, Location origin) {
        Vector3 location = PaperUtils.toCommons(player.getLocation());
        Vector3 delta = new Vector3(
                origin.getX() - location.x(),
                origin.getY() - location.y(),
                origin.getZ() - location.z()
        );
        Vector3 position = (Double.compare(delta.manhattanLength(), 0) == 0
                ? delta
                : delta.normalize().multiply(OFFSET_DISTANCE)
        ).add(location);
        player.playSound(Sound.sound(name, source,
                volume * (float) (1 - Numbers.clamp01((deltaSqr-dropoffSqr) / (rangeSqr -dropoffSqr))),
                pitch
        ), position.x(), position.y(), position.z());
    }

    /**
     * Plays the sound to a player, calculating volume over distance.
     * @param player The player.
     * @param origin The location to play the sound at.
     */
    public void play(Player player, Location origin) {
        double deltaSqr = player.getLocation().distanceSquared(origin);
        if (deltaSqr > rangeSqr)
            return;
        play(player, deltaSqr, origin);
    }

    /**
     * Plays the sound to all players in a world from a location, calculating volume over distance.
     * @param origin The location to play the sound at.
     */
    public void play(Location origin) {
        for (Player player : origin.getWorld().getPlayers()) {
            play(player, origin);
        }
    }

    /**
     * Plays the sound to all players in a world from a location, calculating volume over distance
     * and simulating speed-of-sound.
     * @param plugin The plugin to schedule the sound task for.
     * @param origin The location to play the sound at.
     */
    public void play(Plugin plugin, Location origin) {
        for (Player player : origin.getWorld().getPlayers()) {
            double deltaSqr = player.getLocation().distanceSquared(origin);
            if (deltaSqr > rangeSqr)
                return;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> play(player, deltaSqr, origin),
                    (long) (Math.sqrt(deltaSqr) / speed));
        }
    }
}
