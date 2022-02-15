package com.github.aecsocket.minecommons.core.effect;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * An audience-like object which can accept an effect.
 */
public interface Effector {
    /**
     * Gets an effector that does nothing.
     * @return The effector.
     */
    static Effector empty() {
        return EmptyEffector.INSTANCE;
    }

    /**
     * Plays a sound effect, with a precomputed distance.
     * @param effect The sound effect.
     * @param origin The origin position of the sound.
     * @param distance If already computed, the distance between the effector and the origin.
     */
    void play(SoundEffect effect, Vector3 origin, double distance);

    /**
     * Plays a sound effect.
     * @param effect The sound effect.
     * @param origin The origin position of the sound.
     */
    void play(SoundEffect effect, Vector3 origin);

    /**
     * Spawns a particle effect.
     * @param effect The particle effect.
     * @param origin The origin position of the particle.
     */
    void spawn(ParticleEffect effect, Vector3 origin);
}
