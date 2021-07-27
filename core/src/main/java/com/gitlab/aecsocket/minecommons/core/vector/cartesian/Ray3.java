package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

/**
 * A pair of an origin and a direction.
 * @param orig The starting position, origin.
 * @param dir The normalized ray direction.
 */
public record Ray3(Vector3 orig, Vector3 dir) {
    /**
     * Creates a ray.
     * @param orig The starting position, origin.
     * @param dir The normalized ray direction.
     * @return The ray.
     */
    public static Ray3 ray3(Vector3 orig, Vector3 dir) {
        return new Ray3(orig, dir);
    }

    /**
     * Returns a new ray at the new origin position.
     * @param orig The new origin.
     * @return The new ray.
     */
    public Ray3 at(Vector3 orig) {
        return new Ray3(orig, dir);
    }

    /**
     * Returns a new ray facing the new direction.
     * @param dir The new direction.
     * @return The new ray.
     */
    public Ray3 facing(Vector3 dir) {
        return ray3(orig, dir);
    }

    /**
     * Gets a point of the line at {@code t} units from the origin.
     * @param t The units.
     * @return The point.
     */
    public Vector3 point(double t) {
        return orig.add(dir.multiply(t));
    }
}
