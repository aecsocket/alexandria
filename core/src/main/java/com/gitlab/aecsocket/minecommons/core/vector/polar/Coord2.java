package com.gitlab.aecsocket.minecommons.core.vector.polar;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector2;

/**
 * An (r, ang) double pair, using the polar coordinate system.
 */
public record Coord2(double r, double ang) {
    public Coord2 {
        double _2pi = 2 * Math.PI;
        ang %= _2pi;
        if (ang < 0)
            ang += _2pi;
    }

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

    @Override public String toString() { return "%f, %.0f°".formatted(r, Math.toDegrees(ang)); }
}
