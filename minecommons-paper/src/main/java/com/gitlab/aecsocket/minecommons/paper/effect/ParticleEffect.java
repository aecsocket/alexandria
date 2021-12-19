package com.gitlab.aecsocket.minecommons.paper.effect;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Paper implementation of a particle effect.
 */
public record ParticleEffect(
        org.bukkit.Particle name,
        int count,
        Vector3 size,
        double speed,
        @Nullable Object data
) implements com.gitlab.aecsocket.minecommons.core.effect.ParticleEffect {
    @Override
    public String toString() {
        return name.name() + " x" + count + " " + size + " @ " + speed +
                (data == null ? "" : "[" + data + "]");
    }
}
