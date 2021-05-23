package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

import com.gitlab.aecsocket.minecommons.core.Numbers;
import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.polar.Coord2;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An immutable (x, y) double value pair, using the Cartesian coordinate system.
 */
public record Vector2(double x, double y) {
    /** An instance with all fields set to 0. */
    public static final Vector2 ZERO = new Vector2(0);
    /** The north in a Minecraft world, equivalent to (0, -1). */
    public static final Vector2 NORTH = new Vector2(0, -1);

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(double v) {
        this(v, v);
    }

    /**
     * Creates a new vector with the specified component changed.
     * @param x The new X component.
     * @return The new vector.
     */
    public Vector2 x(double x) { return new Vector2(x, y); }

    /**
     * Creates a new vector with the specified component changed.
     * @param y The new Y component.
     * @return The new vector.
     */
    public Vector2 y(double y) { return new Vector2(x, y); }


    /**
     * {@code (x1 + x2, y1 + y2)}.
     * @param x The X component.
     * @param y The Y component.
     * @return The resulting vector.
     */
    public Vector2 add(double x, double y) { return new Vector2(x() + x, y() + y); }

    /**
     * {@code (x1 + v, y1 + v)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector2 add(double v) { return add(v, v); }

    /**
     * {@code (x1 + x2, y1 + y2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector2 add(@NonNull Vector2 o) { return add(o.x(), o.y()); }


    /**
     * {@code (x1 - x2, y1 - y2)}.
     * @param x The X component.
     * @param y The Y component.
     * @return The resulting vector.
     */
    public Vector2 subtract(double x, double y) { return new Vector2(x() - x, y() - y); }

    /**
     * {@code (x1 - v, y1 - v)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector2 subtract(double v) { return subtract(v, v); }

    /**
     * {@code (x1 - x2, y1 - y2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector2 subtract(@NonNull Vector2 o) { return subtract(o.x(), o.y()); }


    /**
     * {@code (x1 * x2, y1 * y2)}.
     * @param x The X component.
     * @param y The Y component.
     * @return The resulting vector.
     */
    public Vector2 multiply(double x, double y) { return new Vector2(x() * x, y() * y); }

    /**
     * {@code (x1 * v, y1 * v)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector2 multiply(double v) { return multiply(v, v); }

    /**
     * {@code (x1 * x2, y1 * y2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector2 multiply(@NonNull Vector2 o) { return multiply(o.x(), o.y()); }


    /**
     * {@code (x1 / x2, y1 / y2)}.
     * @param x The X component.
     * @param y The Y component.
     * @return The resulting vector.
     */
    public Vector2 divide(double x, double y) { return new Vector2(x() / x, y() / y); }

    /**
     * {@code (x1 / v, y1 / v)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector2 divide(double v) { return multiply(v, v); }

    /**
     * {@code (x1 / x2, y1 / y2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector2 divide(@NonNull Vector2 o) { return multiply(o.x(), o.y()); }

    /**
     * Normalizes this vector so that its length equals 1.
     * @return The normalized vector.
     * @throws IllegalStateException If the length of the vector equals 0.
     */
    public @NonNull Vector2 normalize() {
        double length = length();
        Validation.is(length == 0, "Vector has no length, cannot multiply by 0");
        return new Vector2(x / length, y / length);
    }

    /**
     * Linearly interpolates between this vector and another.
     * @param o The other vector.
     * @param f The interpolation factor.
     * @return The interpolated vector.
     */
    public @NonNull Vector2 lerp(@NonNull Vector2 o, float f) {
        return new Vector2(
                x + (o.x() - x) * f,
                y + (o.y() - y) * f
        );
    }

    /**
     * Gets the Manhattan length of this vector, equivalent to {@code abs(x) + abs(y)}.
     * @return The Manhattan length.
     */
    public double manhattanLength() { return Math.abs(x()) + Math.abs(y()); }

    /**
     * Gets the length of this vector, equivalent to {@code sqrt(x^2 + y^2)}. May be expensive.
     * @return The length.
     */
    public double length() { return Math.sqrt(Numbers.sqr(x()) + Numbers.sqr(y())); }

    /**
     * Gets the Manhattan distance between this vector and another, equivalent to {@code abs(x1 - x2) + abs(y1 - y2)}.
     * @param o The other vector.
     * @return The Manhattan distance.
     */
    public double manhattanDistance(Vector2 o) { return Math.abs(x() - o.x()) + Math.abs(y() - o.y()); }

    /**
     * Gets the distance between this vector and another, equivalent to {@code sqrt((x1 - x2)^2 + (y1 - y2)^2)}.
     * @param o The other vector.
     * @return The distance.
     */
    public double distance(Vector2 o) { return Math.sqrt(Numbers.sqr(x() - o.x()) + Numbers.sqr(y() - o.y())); }

    /**
     * Gets the dot product of this vector and another, equivalent to {@code (x1 * x2) + (y1 * y2)}.
     * @param o The other vector.
     * @return The dot product.
     */
    public double dot(Vector2 o) { return (x() * o.x()) + (y() * o.y()); }

    /**
     * Gets the angle between this vector and another, in radians.
     * @param o The other vector.
     * @return The angle.
     */
    public double angle(Vector2 o) {
        double dot = Numbers.clamp(dot(o) / (length() * o.length()), -1, 1);
        return Math.acos(dot);
    }

    /**
     * Gets the polar radius component.
     * @return The component.
     */
    public double polarR() { return Math.sqrt(Numbers.sqr(x()) + Numbers.sqr(y())); }

    /**
     * Gets the polar ang component.
     * @return The component.
     */
    public double polarAng() { return Math.atan2(y(), x()); }

    /**
     * Converts this to the polar coordinate system.
     * @return A vector in the polar coordinate system.
     */
    public Coord2 polar() {
        return new Coord2(polarR(), polarAng());
    }

    @Override public String toString() { return "%f, %f".formatted(x, y); }
}
