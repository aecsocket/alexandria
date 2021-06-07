package com.gitlab.aecsocket.minecommons.core.serializers.vector;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Point2;

/**
 * Type serializer for a {@link Point2}.
 * <p>
 * Uses the format of {@link AbstractVector2ISerializer}.
 */
public class Point2Serializer extends AbstractVector2ISerializer<Point2> {
    public static final Point2Serializer INSTANCE = new Point2Serializer();

    @Override protected int x(Point2 obj) { return obj.x(); }
    @Override protected int y(Point2 obj) { return obj.y(); }

    @Override protected Point2 of(int x, int y) { return new Point2(x, y); }
}
