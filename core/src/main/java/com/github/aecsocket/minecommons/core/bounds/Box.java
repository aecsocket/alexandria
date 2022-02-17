package com.github.aecsocket.minecommons.core.bounds;

import java.util.Objects;

import com.github.aecsocket.minecommons.core.Validation;
import com.github.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

import static com.github.aecsocket.minecommons.core.vector.cartesian.Vector3.*;

/**
 * A cuboid-shaped volume.
 * <p>
 * This implementation considers rays that are on the same plane as a side
 * to be NOT intersecting.
 * @param min The corner with the smallest coordinates.
 * @param max The corner with the largest coordinates.
 * @param angle The rotation of the box clockwise on the vertical axis.
 */
public record Box(Vector3 min, Vector3 max, Vector3 extent, double angle) implements OrientedBound {
    private static final double BIAS = 1.00001;

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
        Vector3 rMin = Vector3.min(min, max);
        Vector3 rMax = Vector3.max(min, max);
        return new Box(rMin, rMax, max.subtract(min), angle);
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
        return new Box(min, max, extent, angle);
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

    @Override
    public Collision collision(Ray3 ray) {
        Vector3 center = center();
        Vector3 offset = center.neg();

        Vector3 orig, dir, invDir;
        if (Double.compare(angle, 0) == 0) {
            orig = ray.orig().add(offset);
            dir = ray.dir();
            invDir = ray.invDir();
        } else {
            orig = ray.orig().subtract(center).rotateY(-angle).add(center).add(offset);
            dir = ray.dir().rotateY(-angle);
            invDir = dir.reciprocal();
        }

        // https://tavianator.com/2015/ray_box_nan.html
        double t1 = (min.x() - orig.x()) * invDir.x();
        double t2 = (max.x() - orig.x()) * invDir.x();

        double tMin = Math.min(t1, t2);
        double tMax = Math.max(t1, t2);

        for (int i = 1; i < 3; i++) {
            t1 = (min.get(i) - orig.get(i)) * invDir.get(i);
            t2 = (max.get(i) - orig.get(i)) * invDir.get(i);

            tMin = Math.max(tMin, Math.min(Math.min(t1, t2), tMax));
            tMax = Math.min(tMax, Math.max(Math.max(t1, t2), tMin));
        }

        if (tMax <= Math.max(tMin, 0))
            return null;

        // https://blog.johnnovak.net/2016/10/22/the-nim-ray-tracer-project-part-4-calculating-box-normals/
        Vector3 p = ray.point(tMin).subtract(center);
        Vector3 d = min.subtract(max).multiply(0.5);
        return new Collision(tMin, tMax, vec3(
            (int) (p.x() / Math.abs(d.x()) * BIAS),
            (int) (p.y() / Math.abs(d.y()) * BIAS),
            (int) (p.z() / Math.abs(d.z()) * BIAS)
        ));
        // We don't normalize here because it might be slow. I don't care about corner edge cases.
    }

    @Override
    public Box shift(Vector3 vec) {
        Vector3 min = this.min.add(vec);
        Vector3 max = this.max.add(vec);
        return new Box(min, max, extent, angle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return Double.compare(box.angle, angle) == 0 && min.equals(box.min) && max.equals(box.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, angle);
    }
}
