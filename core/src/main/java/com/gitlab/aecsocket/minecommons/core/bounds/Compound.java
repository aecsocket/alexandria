package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.checkerframework.checker.units.qual.min;

import java.util.Arrays;

/**
 * A combination of multiple bounds.
 * @param bounds The bounds that make up this compound.
 */
public record Compound(Bound... bounds) implements Bound {
    /**
     * Creates a compound.
     * @param bounds The bounds that make up this compound.
     * @return The compound.
     */
    public static Compound compound(Bound... bounds) {
        return new Compound(bounds);
    }

    @Override
    public boolean intersects(Vector3 point) {
        for (Bound bound : bounds) {
            if (bound.intersects(point))
                return true;
        }
        return false;
    }

    @Override
    public Compound shift(Vector3 vec) {
        Bound[] newBounds = new Bound[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            newBounds[i] = bounds[i].shift(vec);
        }
        return new Compound(newBounds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compound compound = (Compound) o;
        return Arrays.equals(bounds, compound.bounds);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bounds);
    }
}
