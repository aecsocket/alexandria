package com.github.aecsocket.minecommons.core.effect;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * An effector which forwards actions to other effects.
 */
public interface ForwardingEffector extends Effector {
    /**
     * The effectors to forward to.
     * @return The effectors.
     */
    Iterable<? extends Effector> effectors();

    @Override
    default void play(SoundEffect effect, Vector3 origin, double distance) {
        for (var e : effectors()) e.play(effect, origin, distance);
    }

    @Override
    default void play(SoundEffect effect, Vector3 origin) {
        for (var e : effectors()) e.play(effect, origin);
    }

    @Override
    default void spawn(ParticleEffect effect, Vector3 origin) {
        for (var e : effectors()) e.spawn(effect, origin);
    }
}
