package com.github.aecsocket.minecommons.core.bounds;

import java.util.Arrays;

import com.github.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

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
    public Collision collision(Ray3 ray) {
        for (Bound bound : bounds) {
            Collision result = bound.collision(ray);
            if (result != null)
                return result;
        }
        return null;
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
    public String toString() { return Arrays.toString(bounds); }

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
