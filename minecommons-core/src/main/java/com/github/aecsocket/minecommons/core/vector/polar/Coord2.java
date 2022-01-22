package com.github.aecsocket.minecommons.core.vector.polar;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector2;

/**
 * An (r, ang) double pair, using the polar coordinate system.
 * @param r The radius.
 * @param ang The angle in radians.
 */
public record Coord2(double r, double ang) {
    private static final double pi2 = Math.PI * 2;

    private static double normalize(double v) {
        v %= pi2;
        return v < 0 ? v + pi2 : v;
    }

    /**
     * Creates a coordinate.
     * @param r The radius in radians.
     * @param ang The angle in radians.
     * @return The coordinate.
     */
    public static Coord2 coord2(double r, double ang) {
        return new Coord2(r, normalize(ang));
    }

    /**
     * Creates a new coordinate with the specified component changed.
     * @param r The new radius component.
     * @return The new coordinate.
     */
    public Coord2 r(double r) { return new Coord2(r, ang); }

    /**
     * Creates a new coordinate with the specified component changed.
     * @param ang The new angle component.
     * @return The new coordinate.
     */
    public Coord2 ang(double ang) { return new Coord2(r, normalize(ang)); }

    /**
     * Gets the Cartesian X component.
     * @return The component.
     */
    public double cartesianX() { return r * Math.cos(ang); }

    /**
     * Gets the Cartesian Y component.
     * @return The component.
     */
    public double cartesianY() { return r * Math.sin(ang); }

    /**
     * Converts this to the Cartesian coordinate system.
     * @return A vector in the Cartesian coordinate system.
     */
    public Vector2 cartesian() {
        return new Vector2(cartesianX(), cartesianY());
    }

    @Override public String toString() { return "(%s, %.1f°)".formatted(""+r, Math.toDegrees(ang)); }
}
