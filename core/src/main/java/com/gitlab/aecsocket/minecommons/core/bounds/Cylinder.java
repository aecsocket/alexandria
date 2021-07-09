package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.Validation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

/**
 * A cylinder-shaped volume, upright on the vertical axis.
 * @param base The position of the center at the bottom of the cylinder.
 * @param radius The radius.
 * @param height The height.
 * @param sqrRadius The square radius.
 */
@ConfigSerializable
public record Cylinder(Vector3 base, double radius, double height, double sqrRadius) implements Bound {
    /**
     * Creates a cylinder.
     * @param base The position of the center at the bottom of the cylinder.
     * @param radius The radius.
     * @param height The height.
     * @return The cylinder.
     */
    public static Cylinder cylinder(Vector3 base, double radius, double height) {
        Validation.notNull("base", base);
        Validation.greaterThan("radius", radius, 0);
        Validation.greaterThan("height", height, 0);
        return new Cylinder(base, radius, height, radius*radius);
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
