package com.gitlab.aecsocket.minecommons.core.bounds;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

@ConfigSerializable
public record Box(Vector3 min, Vector3 max, double angle) implements Bound, OrientedBound {
    public Box {
        min = new Vector3(
                Math.min(min.x(), max.x()),
                Math.min(min.y(), max.y()),
                Math.min(min.z(), max.z())
        );
        max = new Vector3(
                Math.max(min.x(), max.x()),
                Math.max(min.y(), max.y()),
                Math.max(min.z(), max.z())
        );
    }

    public static Box box(Vector3 min, Vector3 max, double rotation) {
        return new Box(min, max, rotation);
    }

    public static Box box(Vector3 min, Vector3 max) {
        return new Box(min, max, 0);
    }

    @Override
    public Box angle(double angle) {
        return new Box(min, max, angle);
    }

    public Vector3 center() { return min.lerp(max, 0.5); }
    public Vector3 size() { return max.subtract(min); }

    public Vector3[] corners() {
        return new Vector3[] {
                min, new Vector3(max.x(), min.y(), min.z()), new Vector3(max.x(), max.y(), min.z()),
                max, new Vector3(min.x(), max.y(), max.z()), new Vector3(min.x(), min.y(), max.z())
        };
    }

    @Override
    public boolean intersects(Vector3 point) {
        // 1. rotate [p] [-ang] around [center]
        Vector3 center = center();
        point = point.subtract(center).rotateY(-angle).add(center);
        // 2. calculate
        return point.x() >= min.x() && point.x() <= max.x()
                && point.y() >= min.y() && point.y() <= max.y()
                && point.z() >= min.z() && point.z() <= max.z();
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
        return Double.compare(box.angle, angle) == 0 && min.equals(box.min) && max.equals(box.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, angle);
    }
}
