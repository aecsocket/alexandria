package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * An immutable (x, y) integer value pair, using the Cartesian coordinate system.
 * @param x The X component.
 * @param y The Y component.
 */
public record Point2(int x, int y) implements NumericalVector {
    /** An instance with all fields set to 0. */
    public static final Point2 ZERO = point2(0);

    /**
     * Creates a point.
     * @param x The X component.
     * @param y The Y component.
     * @return The point,
     */
    public static Point2 point2(int x, int y) {
        return new Point2(x, y);
    }

    /**
     * Creates a point.
     * @param v The value of all components.
     * @return The point,
     */
    public static Point2 point2(int v) {
        return point2(v, v);
    }

    /**
     * Creates a new point with the specified component changed.
     * @param x The new X component.
     * @return The new point.
     */
    public Point2 x(int x) { return new Point2(x, y); }

    /**
     * Creates a new point with the specified component changed.
     * @param y The new Y component.
     * @return The new point.
     */
    public Point2 y(int y) { return new Point2(x, y); }

    /**
     * Creates this point as a vector.
     * @return The vector.
     */
    public Vector2 vector() { return new Vector2(x, y); }

    @Override
    public String asString(DecimalFormat format) {
        return "%s, %s".formatted(format.format(x), format.format(y));
    }

    @Override
    public String asString(Locale locale, String format) {
        return String.format(locale, format, x, y);
    }

    @Override public String toString() { return asString(DEFAULT_FORMAT); }
}
