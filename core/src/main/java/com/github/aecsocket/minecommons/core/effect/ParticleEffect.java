package com.github.aecsocket.minecommons.core.effect;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A particle effect.
 */
public interface ParticleEffect {
    /**
     * Gets the object representing the name of the particle.
     * @return The object.
     */
    Object name();

    /**
     * Gets the count of particles to spawn.
     * @return The count.
     */
    int count();

    /**
     * Gets the size of the particles, used for different effects.
     * @return The size.
     */
    Vector3 size();

    /**
     * Gets the speed of the particles, used for different effects.
     * @return The speed.
     */
    double speed();

    /**
     * Gets the data to be used for the particle.
     * @return The data. May be null.
     */
    @Nullable Object data();
}
