package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

import static com.gitlab.aecsocket.minecommons.core.Numbers.sqr;

@ConfigSerializable
public record Sphere(Vector3 center, double radius, double sqrRadius) implements Bound {
    public Sphere {
        Validation.greaterThan("radius", radius, 0);
    }

    public static Sphere sphere(Vector3 center, double radius) {
        return new Sphere(center, radius, sqr(radius));
    }

    @Override
    public boolean intersects(Vector3 point) {
        return center.sqrDistance(point) <= sqrRadius;
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
