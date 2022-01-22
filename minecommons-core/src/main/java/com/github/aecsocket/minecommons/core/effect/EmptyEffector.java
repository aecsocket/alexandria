package com.github.aecsocket.minecommons.core.effect;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * An effector which does nothing.
 */
/* package */ final class EmptyEffector implements Effector {
    public static final EmptyEffector INSTANCE = new EmptyEffector();

    private EmptyEffector() {}

    @Override public void play(SoundEffect effect, Vector3 origin) {}
    @Override public void spawn(ParticleEffect effect, Vector3 origin) {}
}
