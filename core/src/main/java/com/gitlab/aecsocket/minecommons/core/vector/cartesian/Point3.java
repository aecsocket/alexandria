package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

/**
 * An immutable (x, y, z) integer value triplet, using the Cartesian coordinate system.
 * @param x The X component.
 * @param y The Y component.
 * @param z The Z component.
 */
public record Point3(int x, int y, int z) {
    /** An instance with all fields set to 0. */
    public static final Point3 ZERO = point3(0);

    /**
     * Creates a point.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The point,
     */
    public static Point3 point3(int x, int y, int z) {
        return new Point3(x, y, z);
    }

    /**
     * Creates a point.
     * @param v The value of all components.
     * @return The point,
     */
    public static Point3 point3(int v) {
        return point3(v, v, v);
    }

    /**
     * Creates a new point with the specified component changed.
     * @param x The new X component.
     * @return The new point.
     */
    public Point3 x(int x) { return new Point3(x, y, z); }

    /**
     * Creates a new point with the specified component changed.
     * @param y The new Y component.
     * @return The new point.
     */
    public Point3 y(int y) { return new Point3(x, y, z); }

    /**
     * Creates a new point with the specified component changed.
     * @param z The new Z component.
     * @return The new point.
     */
    public Point3 z(int z) { return new Point3(x, y, z); }

    /**
     * Creates this point as a vector.
     * @return The vector.
     */
    public Vector3 vector() { return new Vector3(x, y, z); }

    @Override public String toString() { return "(%d, %d, %d)".formatted(x, y, z); }
}
