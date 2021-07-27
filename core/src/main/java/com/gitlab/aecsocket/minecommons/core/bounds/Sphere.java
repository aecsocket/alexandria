package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Ray3;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

/**
 * A sphere-shaped volume.
 * @param center The center.
 * @param radius The radius.
 * @param sqrRadius The square radius.
 */
@ConfigSerializable
public record Sphere(Vector3 center, double radius, double sqrRadius) implements Bound {
    /**
     * Creates a sphere.
     * @param center The center.
     * @param radius The radius.
     * @return The sphere.
     */
    public static Sphere sphere(Vector3 center, double radius) {
        Validation.notNull("center", center);
        Validation.greaterThan("radius", radius, 0);
        return new Sphere(center, radius, radius*radius);
    }

    @Override
    public boolean intersects(Vector3 point) {
        return center.sqrDistance(point) <= sqrRadius;
    }

    @Override
    public Collision collision(Ray3 ray) {
        Vector3 m = ray.orig().subtract(center);
        double b = m.dot(ray.dir());
        double c = m.dot(m) - sqrRadius;

        if (c > 0 && b > 0)
            return null;
        double sqrDiscrim = b*b - c;
        if (sqrDiscrim < 0)
            return null;
        double discrim = Math.sqrt(sqrDiscrim);
        return new Collision(-b - discrim, -b + discrim);
    }

    @Override
    public Sphere shift(Vector3 vec) {
        return new Sphere(center.add(vec), radius, sqrRadius);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sphere sphere = (Sphere) o;
        return Double.compare(sphere.radius, radius) == 0 && Double.compare(sphere.sqrRadius, sqrRadius) == 0 && center.equals(sphere.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, radius, sqrRadius);
    }
}
