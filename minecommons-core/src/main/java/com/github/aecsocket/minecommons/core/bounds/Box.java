package com.github.aecsocket.minecommons.core.bounds;

import static com.github.aecsocket.minecommons.core.vector.cartesian.Vector3.*;

import java.util.Objects;

import com.github.aecsocket.minecommons.core.Validation;
import com.github.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * A cuboid-shaped volume.
 * @param min The corner with the smallest coordinates.
 * @param max The corner with the largest coordinates.
 * @param angle The rotation of the box clockwise on the vertical axis.
 */
public record Box(Vector3 min, Vector3 max, Vector3 extent, double angle) implements OrientedBound {
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

        Vector3 n = invDir.multiply(orig);
        Vector3 k = invDir.abs().multiply(extent.divide(2));
        Vector3 t1 = n.neg().subtract(k);
        Vector3 t2 = n.neg().add(k);
        double near = t1.maxComponent();
        double far = t2.minComponent();
        if (near > far || far < 0)
            return null;
        Vector3 normal = dir.sign().neg()
                .multiply(vec3(t1.y(), t1.z(), t1.x()).step(t1))
                .multiply(vec3(t1.z(), t1.x(), t1.y()).step(t1))
                .rotateY(angle);
        return new Collision(near, far, normal);
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
