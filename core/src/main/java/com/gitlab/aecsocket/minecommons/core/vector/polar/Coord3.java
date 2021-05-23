package com.gitlab.aecsocket.minecommons.core.vector.polar;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * An (r, yaw, pitch) double triplet, using the spherical coordinate system.
 * <p>
 * r: r
 * <p>
 * theta: pitch
 * <p>
 * phi: yaw
 */
public record Coord3(double r, double yaw, double pitch) {
    public Coord3 {
        double _2pi = 2 * Math.PI;
        yaw %= _2pi;
        if (yaw < 0) yaw += _2pi;
        pitch %= _2pi;
        if (pitch < 0) pitch += _2pi;
    }

    /**
     * Gets the Cartesian X component.
     * @return The component.
     */
    public double cartesianX() { return r * Math.cos(yaw) * Math.sin(pitch); }

    /**
     * Gets the Cartesian Y component.
     * @return The component.
     */
    public double cartesianY() { return r * Math.sin(yaw) * Math.cos(pitch); }

    /**
     * Gets the Cartesian Z component.
     * @return The component.
     */
    public double cartesianZ() { return r * Math.cos(pitch); }

    /**
     * Converts this to the Cartesian coordinate system.
     * @return A vector in the Cartesian coordinate system.
     */
    public Vector3 cartesian() {
        return new Vector3(cartesianX(), cartesianY(), cartesianZ());
    }

    @Override public String toString() { return "%f, %.0f, %.0f°".formatted(r, Math.toDegrees(yaw), Math.toDegrees(pitch)); }
}
