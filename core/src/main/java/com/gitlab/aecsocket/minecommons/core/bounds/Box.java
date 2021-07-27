package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

import java.util.Objects;

/**
 * A cuboid-shaped volume.
 * @param min The corner with the smallest coordinates.
 * @param max The corner with the largest coordinates.
 * @param angle The rotation of the box clockwise on the vertical axis.
 */
public record Box(Vector3 min, Vector3 max, double angle) implements Bound, OrientedBound {
    /**
     * Creates a box.
     * <p>
     * The corners passed are automatically adjusted to be the real minimum and maximum values.
     * @param min The corner with the smallest coordinates.
     * @param max The corner with the largest coordinates.
     * @param angle The rotation of the box clockwise on the vertical axis.
     * @return The box.
     */
    public static Box box(Vector3 min, Vector3 max, double angle) {
        Validation.notNull("min", min);
        Validation.notNull("max", max);
        return new Box(
                Vector3.min(min, max),
                Vector3.max(min, max),
                angle
        );
    }

    /**
     * Creates a box with an angle of 0 radians.
     * <p>
     * The corners passed are automatically adjusted to be the real minimum and maximum values.
     * @param min The corner with the smallest coordinates.
     * @param max The corner with the largest coordinates.
     * @return The box.
     */
    public static Box box(Vector3 min, Vector3 max) {
        return box(min, max, 0);
    }

    @Override
    public Box angle(double angle) {
        return new Box(min, max, angle);
    }

    /**
     * Gets the center of this cuboid.
     * @return The center.
     */
    public Vector3 center() {
        return min.midpoint(max);
    }

    /**
     * Gets the vector between the maximum and minimum points of this cuboid.
     * @return The result.
     */
    public Vector3 size() {
        return max.subtract(min);
    }

    /**
     * Gets an array of six vectors representing the corners of this cuboid.
     * @return The array.
     */
    public Vector3[] corners() {
        return new Vector3[] {
                min, new Vector3(max.x(), min.y(), min.z()), new Vector3(max.x(), max.y(), min.z()),
                max, new Vector3(min.x(), max.y(), max.z()), new Vector3(min.x(), min.y(), max.z())
        };
    }

    private Vector3 bound(boolean sign) {
        return sign ? max : min;
    }

    @Override
    public boolean intersects(Vector3 point) {
        // 1. rotate [p] [-ang] around [center]
        Vector3 center = center();
        Vector3 mapped = point.subtract(center).rotateX(-angle).add(center);
        // 2. calculate
        return mapped.x() >= min.x() && mapped.x() <= max.x()
                && mapped.y() >= min.y() && mapped.y() <= max.y()
                && mapped.z() >= min.z() && mapped.z() <= max.z();
    }

    @Override
    public Collision collision(Ray3 ray) {
        Vector3 center = center();
        Vector3 orig = ray.orig().subtract(center).rotateY(-angle).add(center);
        Vector3 invDir = ray.dir().rotateY(-angle).reciprocal();
        boolean signX = invDir.x() < 0;
        boolean signY = invDir.y() < 0;
        boolean signZ = invDir.z() < 0;

        double tMin = (bound(signX).x() - orig.x()) * invDir.x();
        double tMax = (bound(!signX).x() - orig.x()) * invDir.x();
        double tyMin = (bound(signY).y() - orig.y()) * invDir.y();
        double tyMax = (bound(!signY).y() - orig.y()) * invDir.y();

        if ((tMin > tyMax) || (tyMin > tMax))
            return null;
        if (tyMin > tMin) tMin = tyMin;
        if (tyMax < tMax) tMax = tyMax;

        double tzMin = (bound(signZ).z() - orig.z()) * invDir.z();
        double tzMax = (bound(!signZ).z() - orig.z()) * invDir.z();

        if ((tMin > tzMax) || (tzMin > tMax))
            return null;
        if (tzMin > tMin) tMin = tzMin;
        if (tzMax < tMax) tMax = tzMax;

        return new Collision(tMin, tMax);
    }

    @Override
    public Box shift(Vector3 vec) {
        return new Box(min.add(vec), max.add(vec), angle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return Double.compare(box.angle(), angle) == 0 && min.equals(box.min()) && max.equals(box.max());
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, angle);
    }
}
