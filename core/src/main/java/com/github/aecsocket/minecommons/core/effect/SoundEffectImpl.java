package com.github.aecsocket.minecommons.core.effect;

import net.kyori.adventure.sound.Sound;

/* package */ record SoundEffectImpl(
        Sound sound,
        double dropoff,
        double sqrDropoff,
        double range,
        double sqrRange,
        double speed
) implements SoundEffect {
    @Override
    public String toString() {
        return sound.name() + "[" + sound.source() + "] @ (" +
                sound.volume() + ", " + sound.pitch() + "), (" +
                dropoff + " -> " + range + " @ " + speed + ")";
    }
}
