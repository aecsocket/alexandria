package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * A volume in 3D space which can be intersected by a point.
 */
public interface Bound {
    /**
     * Gets if a point is inside of this volume.
     * @param point The point.
     * @return The status.
     */
    boolean intersects(Vector3 point);

    /**
     * Translates this bound in some direction.
     * @param vec The vector to translate by.
     * @return A bound reflecting the changed state.
     */
    Bound shift(Vector3 vec);
}
