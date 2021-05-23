package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

import com.gitlab.aecsocket.minecommons.core.Numbers;
import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.polar.Coord3;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An immutable (x, y, z) double value triplet, using the Cartesian coordinate system.
 */
public record Vector3(double x, double y, double z) {
    /** An instance with all fields set to 0. */
    public static final Vector3 ZERO = new Vector3(0);

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(double v) {
        this(v, v, v);
    }

    /**
     * Creates a new vector with the specified component changed.
     * @param x The new X component.
     * @return The new vector.
     */
    public Vector3 x(double x) { return new Vector3(x, y, z); }

    /**
     * Creates a new vector with the specified component changed.
     * @param y The new Y component.
     * @return The new vector.
     */
    public Vector3 y(double y) { return new Vector3(x, y, z); }

    /**
     * Creates a new vector with the specified component changed.
     * @param z The new Z component.
     * @return The new vector.
     */
    public Vector3 z(double z) { return new Vector3(x, y, z); }


    /**
     * {@code (x1 + x2, y1 + y2, z1 + z2)}.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The resulting vector.
     */
    public Vector3 add(double x, double y, double z) { return new Vector3(x() + x, y() + y, z() + z); }

    /**
     * {@code (x1 + v, y1 + v, z1 + v)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector3 add(double v) { return add(v, v, v); }

    /**
     * {@code (x1 + x2, y1 + y2, z1 + z2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector3 add(@NonNull Vector3 o) { return add(o.x(), o.y(), o.z()); }


    /**
     * {@code (x1 - x2, y1 - y2, z1 - z2)}.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The resulting vector.
     */
    public Vector3 subtract(double x, double y, double z) { return new Vector3(x() - x, y() - y, z() - z); }

    /**
     * {@code (x1 - v, y1 - v, z1 - v)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector3 subtract(double v) { return subtract(v, v, v); }

    /**
     * {@code (x1 - x2, y1 - y2, z1 - z2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector3 subtract(@NonNull Vector3 o) { return subtract(o.x(), o.y(), o.z()); }


    /**
     * {@code (x1 * x2, y1 * y2, z1 * z2)}.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The resulting vector.
     */
    public Vector3 multiply(double x, double y, double z) { return new Vector3(x() * x, y() * y, z() * z); }

    /**
     * {@code (x1 * v, y1 * v, z1 * z)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector3 multiply(double v) { return multiply(v, v, v); }

    /**
     * {@code (x1 * x2, y1 * y2, z1 * z2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector3 multiply(@NonNull Vector3 o) { return multiply(o.x(), o.y(), o.z()); }


    /**
     * {@code (x1 / x2, y1 / y2, z1 / z2)}.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The resulting vector.
     */
    public Vector3 divide(double x, double y, double z) { return new Vector3(x() / x, y() / y, z() / z); }

    /**
     * {@code (x1 / v, y1 / v, z1 / v)}.
     * @param v The value.
     * @return The resulting vector.
     */
    public Vector3 divide(double v) { return multiply(v, v, v); }

    /**
     * {@code (x1 / x2, y1 / y2, z1 / z2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector3 divide(@NonNull Vector3 o) { return multiply(o.x(), o.y(), o.z()); }

    /**
     * Normalizes this vector so that its length equals 1.
     * @return The normalized vector.
     * @throws IllegalStateException If the length of the vector equals 0.
     */
    public @NonNull Vector3 normalize() {
        double length = length();
        Validation.is(length == 0, "Vector has no length, cannot multiply by 0");
        return new Vector3(x / length, y / length, z / length);
    }

    /**
     * Linearly interpolates between this vector and another.
     * @param o The other vector.
     * @param f The interpolation factor.
     * @return The interpolated vector.
     */
    public @NonNull Vector3 lerp(@NonNull Vector3 o, float f) {
        return new Vector3(
                x + (o.x() - x) * f,
                y + (o.y() - y) * f,
                z + (o.z() - z) * f
        );
    }

    /**
     * Gets the Manhattan length of this vector, equivalent to {@code abs(x) + abs(y) + abs(z)}.
     * @return The Manhattan length.
     */
    public double manhattanLength() { return Math.abs(x()) + Math.abs(y()) + Math.abs(z()); }

    /**
     * Gets the length of this vector, equivalent to {@code sqrt(x^2 + y^2 + z^2)}. May be expensive.
     * @return The length.
     */
    public double length() { return Math.sqrt(Numbers.sqr(x()) + Numbers.sqr(y()) + Numbers.sqr(z())); }

    /**
     * Gets the Manhattan distance between this vector and another, equivalent to {@code abs(x1 - x2) + abs(y1 - y2) + abs(z1 - z2)}.
     * @param o The other vector.
     * @return The Manhattan distance.
     */
    public double manhattanDistance(Vector3 o) { return Math.abs(x() - o.x()) + Math.abs(y() - o.y()) + Math.abs(z() - o.z()); }

    /**
     * Gets the distance between this vector and another, equivalent to {@code sqrt((x1 - x2)^2 + (y1 - y2)^2 + (z1 - z2)^2)}.
     * @param o The other vector.
     * @return The distance.
     */
    public double distance(Vector3 o) { return Math.sqrt(Numbers.sqr(x() - o.x()) + Numbers.sqr(y() - o.y()) + Numbers.sqr(z() - o.z())); }

    /**
     * Gets the dot product of this vector and another, equivalent to {@code (x1 * x2) + (y1 * y2) + (z1 * z2)}.
     * @param o The other vector.
     * @return The dot product.
     */
    public double dot(Vector3 o) { return (x() * o.x()) + (y() * o.y()) + (z() * o.z()); }

    /**
     * Gets the angle between this vector and another, in radians.
     * @param o The other vector.
     * @return The angle.
     */
    public double angle(Vector3 o) {
        double dot = Numbers.clamp(dot(o) / (length() * o.length()), -1, 1);
        return Math.acos(dot);
    }

    /**
     * Gets the spherical radius component.
     * @return The component.
     */
    public double sphericalR() { return Math.sqrt(Numbers.sqr(x()) + Numbers.sqr(y()) + Numbers.sqr(z())); }

    /**
     * Gets the spherical yaw component.
     * @return The component.
     */
    public double sphericalYaw() { return Math.atan(Math.sqrt(Numbers.sqr(x()) + Numbers.sqr(y())) / z()); }

    /**
     * Gets the spherical pitch component.
     * @return The component.
     */
    public double sphericalPitch() { return Math.atan(y() / x()); }

    /**
     * Converts this to the spherical coordinate system.
     * @return A vector in the spherical coordinate system.
     */
    public Coord3 spherical() {
        return new Coord3(sphericalR(), sphericalYaw(), sphericalPitch());
    }

    @Override public String toString() { return "%f, %f, %f".formatted(x, y, z); }
}
