package com.github.aecsocket.minecommons.core.bounds;

/**
 * A volume in 3D space which can be rotated on the vertical axis.
 */
public interface OrientedBound extends Bound {
    /**
     * The angle that this volume is rotated at clockwise, on the vertical axis, in radians.
     * @return The angle, in radians.
     */
    double angle();

    /**
     * Sets the rotation of this volume clockwise on the vertical axis, in radians.
     * @param angle The angle, in radians.
     * @return A bound reflecting the changed state.
     */
    OrientedBound angle(double angle);
}
