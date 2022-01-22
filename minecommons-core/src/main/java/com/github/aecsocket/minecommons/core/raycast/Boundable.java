package com.github.aecsocket.minecommons.core.raycast;

import com.github.aecsocket.minecommons.core.bounds.Bound;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * An object which takes up volume, as expressed by a bound.
 */
public interface Boundable {
    /**
     * The object's internal position.
     * @return The position.
     */
    Vector3 origin();

    /**
     * The bound that expresses this object.
     * @return THe bound.
     */
    Bound bound();

    /**
     * Creates a basic boundable.
     * @param origin The position.
     * @param bound The bound that expresses this object.
     * @return The boundable.
     */
    static Boundable of(Vector3 origin, Bound bound) {
        return new Boundable() {
            @Override public Vector3 origin() { return origin; }
            @Override public Bound bound() { return bound; }
        };
    }
}
