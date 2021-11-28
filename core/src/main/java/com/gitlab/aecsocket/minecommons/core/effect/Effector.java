package com.gitlab.aecsocket.minecommons.core.effect;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

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
