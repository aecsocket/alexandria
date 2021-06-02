package com.gitlab.aecsocket.minecommons.core.raycast;

import com.gitlab.aecsocket.minecommons.core.bounds.Bound;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

public interface Boundable {
    Vector3 origin();
    Bound bound();

    static Boundable of(Vector3 origin, Bound bound) {
        return new Boundable() {
            @Override public Vector3 origin() { return origin; }
            @Override public Bound bound() { return bound; }
        };
    }
}
