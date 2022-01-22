package com.github.aecsocket.minecommons.core.serializers.vector;

import com.github.aecsocket.minecommons.core.vector.cartesian.Point3;

/**
 * Type serializer for a {@link Point3}.
 * <p>
 * Uses the format of {@link AbstractVector3ISerializer}.
 */
public class Point3Serializer extends AbstractVector3ISerializer<Point3> {
    /** A singleton instance of this serializer. */
    public static final Point3Serializer INSTANCE = new Point3Serializer();

    @Override protected int x(Point3 obj) { return obj.x(); }
    @Override protected int y(Point3 obj) { return obj.y(); }
    @Override protected int z(Point3 obj) { return obj.z(); }

    @Override protected Point3 of(int x, int y, int z) { return Point3.point3(x, y, z); }
}
