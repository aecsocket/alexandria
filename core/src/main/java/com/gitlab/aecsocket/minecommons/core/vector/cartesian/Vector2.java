package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.polar.Coord2;
import org.checkerframework.checker.nullness.qual.NonNull;

import static java.lang.Math.*;
import static com.gitlab.aecsocket.minecommons.core.Numbers.*;

/**
 * An immutable (x, y) double value pair, using the Cartesian coordinate system.
 * @param x The X component.
 * @param y The Y component.
 */
public record Vector2(double x, double y) {
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
    public Vector2 add(@NonNull Vector2 o) { return add(o.x, o.y); }


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
    public Vector2 multiply(@NonNull Vector2 o) { return multiply(o.x, o.y); }


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
    public Vector2 divide(@NonNull Vector2 o) { return multiply(o.x, o.y); }

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
    public @NonNull Vector2 normalize() {
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
    public @NonNull Vector2 lerp(@NonNull Vector2 o, double f) {
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
     * Gets the polar radius component.
     * @return The component.
     */
    public double polarR() { return sqrt(sqr(x) + sqr(y)); }

    /**
     * Gets the polar ang component.
     * @return The component.
     */
    public double polarAng() { return atan2(y, x); }

    /**
     * Converts this to the polar coordinate system.
     * @return A vector in the polar coordinate system.
     */
    public Coord2 polar() {
        return new Coord2(polarR(), polarAng());
    }

    @Override public String toString() { return "(%s, %s)".formatted(Double.toString(x), Double.toString(y)); }
}
