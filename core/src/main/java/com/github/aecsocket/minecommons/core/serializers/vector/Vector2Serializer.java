package com.github.aecsocket.minecommons.core.serializers.vector;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector2;

/**
 * Type serializer for a {@link Vector2}.
 * <p>
 * Uses the format of {@link AbstractVector2DSerializer}.
 */
public class Vector2Serializer extends AbstractVector2DSerializer<Vector2> {
    /** A singleton instance of this serializer. */
    public static final Vector2Serializer INSTANCE = new Vector2Serializer();

    @Override protected double x(Vector2 obj) { return obj.x(); }
    @Override protected double y(Vector2 obj) { return obj.y(); }

    @Override protected Vector2 of(double x, double y) { return new Vector2(x, y); }
}
