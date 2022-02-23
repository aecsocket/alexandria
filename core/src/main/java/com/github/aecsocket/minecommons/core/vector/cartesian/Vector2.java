package com.github.aecsocket.minecommons.core.vector.cartesian;

import java.text.DecimalFormat;
import java.util.Locale;

import com.github.aecsocket.minecommons.core.Validation;
import com.github.aecsocket.minecommons.core.vector.polar.Coord2;

import static com.github.aecsocket.minecommons.core.Numbers.*;
import static java.lang.Math.*;

/**
 * An immutable (x, y) double value pair, using the Cartesian coordinate system.
 * @param x The X component.
 * @param y The Y component.
 */
public record Vector2(double x, double y) implements NumericalVector {
    /** An instance with all fields set to 0. */
    public static final Vector2 ZERO = vec2(0);
    /** The north in a Minecraft world, equivalent to (0, -1). */
    public static final Vector2 NORTH = vec2(0, -1);

    /**
     * Creates a vector.
     * @param x The X component.
     * @param y The Y component.
     * @return The vector.
     */
    public static Vector2 vec2(double x, double y) {
        return new Vector2(x, y);
    }

    /**
     * Creates a vector.
     * @param v The value of all components.
     * @return The vector.
     */
    public static Vector2 vec2(double v) {
        return vec2(v, v);
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
    public Vector2 add(Vector2 o) { return add(o.x, o.y); }


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
    public Vector2 subtract(Vector2 o) { return subtract(o.x(), o.y()); }


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
    public Vector2 multiply(Vector2 o) { return multiply(o.x, o.y); }


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
    public Vector2 divide(double v) { return divide(v, v); }

    /**
     * {@code (x1 / x2, y1 / y2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector2 divide(Vector2 o) { return divide(o.x, o.y); }

    /**
     * Negates all components.
     * @return The resulting vector.
     */
    public Vector2 neg() { return new Vector2(-x, -y); }

    /**
     * Applies {@link Math#abs(double)} on all components.
     * @return The resulting vector.
     */
    public Vector2 abs() { return new Vector2(Math.abs(x), Math.abs(y)); }

    /**
     * Gets the smallest component of this vector.
     * @return The component.
     */
    public double minComponent() { return Math.min(x, y); }

    /**
     * Gets the largest component of this vector.
     * @return The component.
     */
    public double maxComponent() { return Math.max(x, y); }

    /**
     * Normalizes this vector so that its length equals 1.
     * @return The normalized vector.
     * @throws IllegalStateException If the length of the vector equals 0.
     */
    public Vector2 normalize() {
        double length = length();
        Validation.assertNot(length == 0, "Vector has no length, cannot multiply by 0");
        return new Vector2(x / length, y / length);
    }

    /**
     * Linearly interpolates between this vector and another.
     * @param o The other vector.
     * @param f The interpolation factor.
     * @return The interpolated vector.
     */
    public Vector2 lerp(Vector2 o, double f) {
        return new Vector2(
            x + (o.x() - x) * f,
            y + (o.y() - y) * f
        );
    }

    /**
     * Gets the Manhattan length of this vector, equivalent to {@code abs(x) + abs(y)}.
     * @return The Manhattan length.
     */
    public double manhattanLength() { return Math.abs(x) + Math.abs(y); }

    /**
     * Gets the squared length of this vector, equivalent to {@code x^2 + y^2}.
     * Less expensive than {@link #length()}.
     * @return The length.
     */
    public double sqrLength() { return sqr(x) + sqr(y); }

    /**
     * Gets the length of this vector, equivalent to {@code sqrt(x^2 + y^2)}.
     * Expensive, may prefer to use {@link #sqrLength()}.
     * @return The length.
     */
    public double length() { return sqrt(sqrLength()); }

    /**
     * Gets the Manhattan distance between this vector and another, equivalent to {@code abs(x1 - x2) + abs(y1 - y2)}.
     * @param o The other vector.
     * @return The Manhattan distance.
     */
    public double manhattanDistance(Vector2 o) { return Math.abs(x - o.x) + Math.abs(y - o.y); }

    /**
     * Gets the squared distance between this vector and another, equivalent to {@code (x1 - x2)^2 + (y1 - y2)^2 + (z1 - z2)^2}.
     * Less expensive than {@link #distance(Vector2)}.
     * @param o The other vector.
     * @return The distance.
     */
    public double sqrDistance(Vector2 o) { return sqr(x - o.x) + sqr(y - o.y); }

    /**
     * Gets the distance between this vector and another, equivalent to {@code sqrt((x1 - x2)^2 + (y1 - y2)^2)}.
     * Expensive, may prefer to use {@link #sqrDistance(Vector2)}.
     * @param o The other vector.
     * @return The distance.
     */
    public double distance(Vector2 o) { return sqrt(sqrDistance(o)); }

    /**
     * Gets the dot product of this vector and another, equivalent to {@code (x1 * x2) + (y1 * y2)}.
     * @param o The other vector.
     * @return The dot product.
     */
    public double dot(Vector2 o) { return (x * o.x) + (y * o.y); }

    /**
     * Gets the angle between this vector and another, in radians.
     * @param o The other vector.
     * @return The angle.
     */
    public double angle(Vector2 o) {
        double dot = clamp(dot(o) / (length() * o.length()), -1, 1);
        return acos(dot);
    }

    /**
     * Gets the spherical ang component.
     * @return The component.
     */
    public double sphericalAng() { return atan2(y, x); }

    /**
     * Converts this to the spherical coordinate system, with a custom length.
     * @param length The length.
     * @return A vector in the spherical coordinate system.
     */
    public Coord2 spherical(double length) {
        return new Coord2(length, sphericalAng());
    }

    /**
     * Converts this to the spherical coordinate system, computing the length of this vector.
     * <p>
     * If you have already computed the length of this vector, use {@link #spherical(double)}.
     * @return A vector in the spherical coordinate system.
     */
    public Coord2 spherical() {
        return new Coord2(length(), sphericalAng());
    }

    /**
     * Converts this to an integer-vector Point instance.
     * @return The point.
     */
    public Point2 point() {
        return new Point2((int) x, (int) y);
    }

    @Override
    public String asString(DecimalFormat format) {
        return "(%s, %s)".formatted(format.format(x), format.format(y));
    }

    @Override
    public String asString(Locale locale, String format) {
        return String.format(locale, format, x, y);
    }

    @Override public String toString() { return asString(DEFAULT_FORMAT); }
}
