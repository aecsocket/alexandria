package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.polar.Coord3;

import java.util.function.Function;

import static java.lang.Math.*;
import static com.gitlab.aecsocket.minecommons.core.Numbers.*;
import static com.gitlab.aecsocket.minecommons.core.vector.polar.Coord3.*;

/**
 * An immutable (x, y, z) double value triplet, using the Cartesian coordinate system.
 * @param x The X component.
 * @param y The Y component.
 * @param z The Z component.
 */
public record Vector3(double x, double y, double z) {
    private static final double PI2 = PI * 2;

    /** An instance with all fields set to 0. */
    public static final Vector3 ZERO = vec3(0);

    /**
     * Creates a vector.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The vector.
     */
    public static Vector3 vec3(double x, double y, double z) {
        return new Vector3(x, y, z);
    }

    /**
     * Creates a vector.
     * @param v The value of all components.
     * @return The vector.
     */
    public static Vector3 vec3(double v) {
        return vec3(v, v, v);
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
    public Vector3 add(Vector3 o) { return add(o.x, o.y, o.z); }


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
    public Vector3 subtract(Vector3 o) { return subtract(o.x, o.y, o.z); }


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
    public Vector3 multiply(Vector3 o) { return multiply(o.x, o.y, o.z); }


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
    public Vector3 divide(double v) { return divide(v, v, v); }

    /**
     * {@code (x1 / x2, y1 / y2, z1 / z2)}.
     * @param o The other vector.
     * @return The resulting vector.
     */
    public Vector3 divide(Vector3 o) { return divide(o.x, o.y, o.z); }

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

    /**
     * Rotates this vector around a unit axis.
     * @param a The unit axis.
     * @param angle The angle to rotate by, in radians.
     * @return The resulting vector.
     */
    public Vector3 rotate(Vector3 a, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double dot = dot(a);
        return new Vector3(
                a.x * dot * (1 - cos) + x * cos + (-a.z * y + a.y * z) * sin,
                a.y * dot * (1 - cos) + y * cos + (a.z * x - a.x * z) * sin,
                a.z * dot * (1 - cos) + z * cos + (-a.y * x + a.x * y) * sin
        );
    }

    /**
     * Gets a 2D vector of {@code (x, x)}.
     * @return The 2D vector.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vector2 xx() { return new Vector2(x, x); }
    /**
     * Gets a 2D vector of {@code (x, y)}.
     * @return The 2D vector.
     */
    public Vector2 xy() { return new Vector2(x, y); }
    /**
     * Gets a 2D vector of {@code (x, z)}.
     * @return The 2D vector.
     */
    public Vector2 xz() { return new Vector2(x, z); }

    /**
     * Gets a 2D vector of {@code (y, x)}.
     * @return The 2D vector.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vector2 yx() { return new Vector2(y, x); }
    /**
     * Gets a 2D vector of {@code (y, y)}.
     * @return The 2D vector.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vector2 yy() { return new Vector2(y, y); }
    /**
     * Gets a 2D vector of {@code (y, z)}.
     * @return The 2D vector.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vector2 yz() { return new Vector2(y, z); }

    /**
     * Gets a 2D vector of {@code (z, x)}.
     * @return The 2D vector.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vector2 zx() { return new Vector2(z, x); }
    /**
     * Gets a 2D vector of {@code (z, y)}.
     * @return The 2D vector.
     */
    public Vector2 zy() { return new Vector2(z, y); }
    /**
     * Gets a 2D vector of {@code (z, z)}.
     * @return The 2D vector.
     */
    public Vector2 zz() { return new Vector2(z, z); }

    /**
     * Creates a new vector, with each component mapped to a different number through a mapper function.
     * @param mapper The mapper.
     * @return The new vector.
     */
    public Vector3 map(Function<Double, Double> mapper) {
        return new Vector3(mapper.apply(x), mapper.apply(y), mapper.apply(z));
    }

    /**
     * Gets {@code 1 / this}.
     * @return The reciprocal of this vector.
     */
    public Vector3 reciprocal() {
        return new Vector3(1 / x, 1 / y, 1 / z);
    }

    /**
     * Normalizes this vector so that its length equals 1.
     * @return The normalized vector.
     * @throws IllegalStateException If the length of the vector equals 0.
     */
    public Vector3 normalize() {
        double length = length();
        Validation.assertNot(length == 0, "Vector has no length, cannot multiply by 0");
        return new Vector3(x / length, y / length, z / length);
    }

    /**
     * Linearly interpolates between this vector and another.
     * @param o The other vector.
     * @param f The interpolation factor.
     * @return The interpolated vector.
     */
    public Vector3 lerp(Vector3 o, double f) {
        return new Vector3(
                x + (o.x - x) * f,
                y + (o.y - y) * f,
                z + (o.z - z) * f
        );
    }

    /**
     * Returns a vector with each component mapped: if {@code o[component]} is less than {@code this[component]},
     * {@code 0} is used, otherwise {@code 1}.
     * @param o The other vector.
     * @return The stepped vector.
     */
    public Vector3 step(Vector3 o) {
        return new Vector3(
                o.x < x ? 0 : 1,
                o.y < y ? 0 : 1,
                o.z < z ? 0 : 1
        );
    }

    /**
     * Gets the midpoint between this vector and another.
     * @param o The other vector.
     * @return The midpoint.
     */
    public Vector3 midpoint(Vector3 o) {
        return new Vector3(
                (x + o.x) / 2,
                (y + o.y) / 2,
                (z + o.z) / 2
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
     * Gets the cross product of this vector and another.
     * @param o The other vector.
     * @return The cross product.
     */
    public Vector3 cross(Vector3 o) {
        return new Vector3(
                y * o.z - o.y * z,
                z * o.x - o.z * x,
                x * o.y - o.x * y
        );
    }

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
     * Gets a vector expressed as the sign of each component.
     * @return The vector.
     */
    public Vector3 sign() {
        return new Vector3(Math.signum(x), Math.signum(y), Math.signum(z));
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
    public double sphericalYaw() { return atan2(-x, z) + PI2; }

    /**
     * Gets the spherical pitch component.
     * @return The component.
     */
    public double sphericalPitch() { return atan(-y / sqrt(x*x + z*z)); }

    /**
     * Converts this to the spherical coordinate system.
     * @return A vector in the spherical coordinate system.
     */
    public Coord3 spherical() {
        return coord3(sphericalR(), sphericalYaw(), sphericalPitch());
    }

    @Override public String toString() { return "(%s, %s, %s)".formatted(""+x, ""+y, ""+z); }

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

    /**
     * Reflects a vector around a normal.
     * @param vec The vector to reflect.
     * @param norm The normalized normal vector.
     * @return The reflected vector.
     */
    public static Vector3 reflect(Vector3 vec, Vector3 norm) {
        return vec.subtract(norm.multiply(2 * vec.dot(norm)));
    }

    /**
     * Gets a direction offset by another vector.
     * @param dir The original direction.
     * @param offset The offset.
     * @return The offset vector.
     */
    public static Vector3 offset(Vector3 dir, Vector3 offset) {
        Vector3 xzTan = vec3(-dir.z, 0, dir.x).normalize();
        Vector3 yTan = xzTan.cross(dir).normalize();

        return xzTan
                .multiply(offset.x)
                .add(dir.multiply(offset.z)
                        .add(yTan.multiply(offset.y)));
    }
}
