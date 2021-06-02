package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

import static com.gitlab.aecsocket.minecommons.core.Numbers.sqr;

@ConfigSerializable
public record Cylinder(Vector3 base, double radius, double height, double sqrRadius) implements Bound {
    public Cylinder {
        Validation.greaterThan("radius", radius, 0);
        Validation.greaterThan("height", height, 0);
    }

    public static Cylinder cylinder(Vector3 base, double radius, double height) {
        return new Cylinder(base, radius, height, sqr(radius));
    }

    @Override
    public boolean intersects(Vector3 point) {
        return point.y() >= base.y() && point.y() <= base.y() + height
                && base.xz().sqrDistance(point.xz()) <= sqrRadius;
    }

    @Override
    public Cylinder shift(Vector3 vec) {
        return new Cylinder(base.add(vec), radius, height, sqrRadius);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cylinder cylinder = (Cylinder) o;
        return Double.compare(cylinder.radius, radius) == 0 && Double.compare(cylinder.height, height) == 0 && Double.compare(cylinder.sqrRadius, sqrRadius) == 0 && base.equals(cylinder.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, radius, height, sqrRadius);
    }
}
