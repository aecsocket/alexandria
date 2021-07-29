package com.gitlab.aecsocket.minecommons.core.vector.polar;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * An (r, yaw, pitch) double triplet, using the spherical coordinate system.
 * @param r The radius in radians.
 * @param yaw The yaw (phi).
 * @param pitch The pitch (theta).
 */
public record Coord3(double r, double yaw, double pitch) {
    private static final double pi2 = Math.PI * 2;

    private static double normalize(double v) {
        v %= pi2;
        return v < 0 ? v + pi2 : v;
    }

    /**
     * Creates a coordinate.
     * @param r The radius in radians.
     * @param yaw The yaw (phi).
     * @param pitch The pitch (theta).
     * @return The coordinate.
     */
    public static Coord3 coord3(double r, double yaw, double pitch) {
        return new Coord3(r, normalize(yaw), normalize(pitch));
    }

    /**
     * Creates a new coordinate with the specified component changed.
     * @param r The new radius component.
     * @return The new coordinate.
     */
    public Coord3 r(double r) { return new Coord3(r, yaw, pitch); }

    /**
     * Creates a new coordinate with the specified component changed.
     * @param yaw The new yaw component.
     * @return The new coordinate.
     */
    public Coord3 yaw(double yaw) { return new Coord3(r, normalize(yaw), pitch); }

    /**
     * Creates a new coordinate with the specified component changed.
     * @param pitch The new pitch component.
     * @return The new coordinate.
     */
    public Coord3 pitch(double pitch) { return new Coord3(r, yaw, normalize(pitch)); }

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

    @Override public String toString() { return "(%s, %.1f°, %.1f°)".formatted(""+r, Math.toDegrees(yaw), Math.toDegrees(pitch)); }
}
