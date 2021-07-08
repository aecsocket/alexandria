package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.polar.Coord3;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

import static java.lang.Math.*;
import static com.gitlab.aecsocket.minecommons.core.Numbers.*;

/**
 * An immutable (x, y, z) double value triplet, using the Cartesian coordinate system.
 */
public record Vector3(double x, double y, double z) {
    /**
     * A component of a 3D vector.
     */
    public enum Component {
        X   (Vector3::x),
        Y   (Vector3::y),
        Z   (Vector3::z);

        private final Function<Vector3, Double> mapper;

        Component(Function<Vector3, Double> mapper) {
            this.mapper = mapper;
        }

        /**
         * Gets this component's value in a vector.
         * @param vec The vector.
         * @return The component's value.
         */
        public double get(Vector3 vec) { return mapper.apply(vec); }
    }

    /** An instance with all fields set to 0. */
    public static final Vector3 ZERO = new Vector3(0);

    public Vector3(double v) {
        this(v, v, v);
    }

    /**
     * Creates a new vector.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The vector.
     */
    public static Vector3 vec3(double x, double y, double z) { return new Vector3(x, y, z); }

    /**
     * Creates a new vector.
     * @param v The value of each component.
     * @return The vector.
     */
    public static Vector3 vec3(double v) { return new Vector3(v); }

    /**
     * Gets the value of a specific component in this vector.
     * @param component The component.
     * @return The component's value.
     */
    public double get(Component component) { return component.get(this); }

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
    public Vector3 add(@NonNull Vector3 o) { return add(o.x, o.y, o.z); }


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
    public Vector3 subtract(@NonNull Vector3 o) { return subtract(o.x, o.y, o.z); }


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
    public Vector3 multiply(@NonNull Vector3 o) { return multiply(o.x, o.y, o.z); }


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
    public Vector3 divide(@NonNull Vector3 o) { return multiply(o.x, o.y, o.z); }

    /**
     * Negates all components.
     * @return The resulting vector.
     */
    public Vector3 neg() { return new Vector3(-x, -y, -z); }

    /**
     * Applies {@link Math#abs(double)} on all components.
     * @return The resulting vector.
     */
    public Vector3 abs() { return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z)); }

    /**
     * Gets the smallest component of this vector.
     * @return The component.
     */
    public double minComponent() { return Math.min(Math.min(x, y), z); }

    /**
     * Gets the largest component of this vector.
     * @return The component.
     */
    public double maxComponent() { return Math.max(Math.max(x, y), z); }

    /**
     * Rotates this vector around the X axis.
     * @param ang The angle to rotate by, in radians.
     * @return The resulting vector.
     */
    public Vector3 rotateX(double ang) {
        double cos = cos(ang);
        double sin = sin(ang);
        return new Vector3(
                x,
                cos * y - sin * z,
                sin * y + cos * z
        );
    }

    /**
     * Rotates this vector around the Y axis.
     * @param ang The angle to rotate by, in radians.
     * @return The resulting vector.
     */
    public Vector3 rotateY(double ang) {
        double cos = cos(ang);
        double sin = sin(ang);
        return new Vector3(
                cos * x + sin * z,
                y,
                -sin * x + cos * z
        );
    }

    /**
     * Rotates this vector around the Z axis.
     * @param ang The angle to rotate by, in radians.
     * @return The resulting vector.
     */
    public Vector3 rotateZ(double ang) {
        double cos = cos(ang);
        double sin = sin(ang);
        return new Vector3(
                cos * x - sin * y,
                sin * x + cos * y,
                z
        );
    }

    public Vector2 xx() { return new Vector2(x, x); }
    public Vector2 xy() { return new Vector2(x, y); }
    public Vector2 xz() { return new Vector2(x, z); }

    public Vector2 yx() { return new Vector2(y, x); }
    public Vector2 yy() { return new Vector2(y, y); }
    public Vector2 yz() { return new Vector2(y, z); }

    public Vector2 zx() { return new Vector2(z, x); }
    public Vector2 zy() { return new Vector2(z, y); }
    public Vector2 zz() { return new Vector2(z, z); }

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
    public @NonNull Vector3 lerp(@NonNull Vector3 o, double f) {
        return new Vector3(
                x + (o.x - x) * f,
                y + (o.y - y) * f,
                z + (o.z - z) * f
        );
    }

    /**
     * Gets the Manhattan length of this vector, equivalent to {@code abs(x) + abs(y) + abs(z)}.
     * @return The Manhattan length.
     */
    public double manhattanLength() { return Math.abs(x) + Math.abs(y) + Math.abs(z); }

    /**
     * Gets the squared length of this vector, equivalent to {@code x^2 + y^2 + z^2}.
     * Less expensive than {@link #length()}.
     * @return The length.
     */
    public double sqrLength() { return sqr(x) + sqr(y) + sqr(z); }

    /**
     * Gets the length of this vector, equivalent to {@code sqrt(x^2 + y^2 + z^2)}.
     * Expensive, may prefer to use {@link #sqrLength()}.
     * @return The length.
     */
    public double length() { return sqrt(sqrLength()); }

    /**
     * Gets the Manhattan distance between this vector and another, equivalent to {@code abs(x1 - x2) + abs(y1 - y2) + abs(z1 - z2)}.
     * @param o The other vector.
     * @return The Manhattan distance.
     */
    public double manhattanDistance(Vector3 o) { return Math.abs(x - o.x) + Math.abs(y - o.y) + Math.abs(z - o.z); }

    /**
     * Gets the squared distance between this vector and another, equivalent to {@code (x1 - x2)^2 + (y1 - y2)^2 + (z1 - z2)^2}.
     * Less expensive than {@link #distance(Vector3)}.
     * @param o The other vector.
     * @return The distance.
     */
    public double sqrDistance(Vector3 o) { return sqr(x - o.x) + sqr(y - o.y) + sqr(z - o.z); }

    /**
     * Gets the distance between this vector and another, equivalent to {@code sqrt((x1 - x2)^2 + (y1 - y2)^2 + (z1 - z2)^2)}.
     * Expensive, may prefer to use {@link #sqrDistance(Vector3)}.
     * @param o The other vector.
     * @return The distance.
     */
    public double distance(Vector3 o) { return sqrt(sqrDistance(o)); }

    /**
     * Gets the dot product of this vector and another, equivalent to {@code (x1 * x2) + (y1 * y2) + (z1 * z2)}.
     * @param o The other vector.
     * @return The dot product.
     */
    public double dot(Vector3 o) { return (x * o.x) + (y * o.y) + (z * o.z); }

    /**
     * Gets the angle between this vector and another, in radians.
     * @param o The other vector.
     * @return The angle.
     */
    public double angle(Vector3 o) {
        double dot = clamp(dot(o) / (length() * o.length()), -1, 1);
        return acos(dot);
    }

    /**
     * Gets the spherical radius component.
     * @return The component.
     */
    public double sphericalR() { return sqrt(sqr(x) + sqr(y) + sqr(z)); }

    /**
     * Gets the spherical yaw component.
     * @return The component.
     */
    public double sphericalYaw() { return atan(sqrt(sqr(x) + sqr(y)) / z); }

    /**
     * Gets the spherical pitch component.
     * @return The component.
     */
    public double sphericalPitch() { return atan(y / x); }

    /**
     * Converts this to the spherical coordinate system.
     * @return A vector in the spherical coordinate system.
     */
    public Coord3 spherical() {
        return new Coord3(sphericalR(), sphericalYaw(), sphericalPitch());
    }

    @Override public String toString() { return "(%s, %s, %s)".formatted(Double.toString(x), Double.toString(y), Double.toString(z)); }

    /**
     * Gets a vector of a combination of the smallest components from each vector.
     * @param a The first vector.
     * @param b The second vector.
     * @return The resulting vector.
     */
    public static Vector3 min(Vector3 a, Vector3 b) {
        return new Vector3(
                Math.min(a.x, b.x),
                Math.min(a.y, b.y),
                Math.min(a.z, b.z)
        );
    }

    /**
     * Gets a vector of a combination of the largest components from each vector.
     * @param a The first vector.
     * @param b The second vector.
     * @return The resulting vector.
     */
    public static Vector3 max(Vector3 a, Vector3 b) {
        return new Vector3(
                Math.max(a.x, b.x),
                Math.max(a.y, b.y),
                Math.max(a.z, b.z)
        );
    }
}
