package com.gitlab.aecsocket.minecommons.core.bounds;

/**
 * A volume in 3D space which can be rotated on the vertical axis.
 */
public interface OrientedBound extends Bound {
    /**
     * The angle that this volume is rotated at, on the vertical axis, in radians.
     * @return The angle, in radians.
     */
    double angle();

    /**
     * Rotates this volume on the vertical axis by an angle, in radians.
     * @param angle The angle, in radians.
     * @return A bound reflecting the changed state.
     */
    OrientedBound angle(double angle);
}
