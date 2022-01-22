package com.github.aecsocket.minecommons.core.effect;

import net.kyori.adventure.sound.Sound;

/**
 * A sound effect which has an exact dropoff and range value, and a m/s speed.
 */
public interface SoundEffect {
    /** The default speed value, in m/s. */
    double SPEED = 340.29;

    /**
     * Gets the sound to play.
     * @return The sound.
     */
    Sound sound();

    /**
     * Gets the distance at which the volume starts to drop.
     * @return The distance in m.
     */
    double dropoff();

    /**
     * Gets {@code dropoff * dropoff}.
     * @return The squared dropoff.
     */
    double sqrDropoff();

    /**
     * Gets the distance at which the sound is not audible.
     * @return The distance in m.
     */
    double range();

    /**
     * Gets {@code range * range}.
     * @return The squared range.
     */
    double sqrRange();

    /**
     * Gets the speed of the sound - how long it will take to reach an effector.
     * @return The speed in m/s.
     */
    double speed();

    /**
     * Creates a sound effect.
     * @param sound The sound.
     * @param dropoff The distance at which the volume starts to drop.
     * @param range The distance at which the sound is not audible.
     * @param speed The speed of the sound.
     * @return The effect.
     */
    static SoundEffect soundEffect(Sound sound, double dropoff, double range, double speed) {
        return new SoundEffectImpl(sound, dropoff, dropoff*dropoff, range, range*range, speed);
    }

    /**
     * Creates a sound effect, with the {@link #SPEED default speed}.
     * @param sound The sound.
     * @param dropoff The distance at which the volume starts to drop.
     * @param range The distance at which the sound is not audible.
     * @return The effect.
     */
    static SoundEffect soundEffect(Sound sound, double dropoff, double range) {
        return new SoundEffectImpl(sound, dropoff, dropoff*dropoff, range, range*range, SPEED);
    }
}
