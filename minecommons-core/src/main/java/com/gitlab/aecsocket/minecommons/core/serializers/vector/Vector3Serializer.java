package com.gitlab.aecsocket.minecommons.core.serializers.vector;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * Type serializer for a {@link Vector3}.
 * <p>
 * Uses the format of {@link AbstractVector3DSerializer}.
 */
public class Vector3Serializer extends AbstractVector3DSerializer<Vector3> {
    /** A singleton instance of this serializer. */
    public static final Vector3Serializer INSTANCE = new Vector3Serializer();

    @Override protected double x(Vector3 obj) { return obj.x(); }
    @Override protected double y(Vector3 obj) { return obj.y(); }
    @Override protected double z(Vector3 obj) { return obj.z(); }

    @Override protected Vector3 of(double x, double y, double z) { return new Vector3(x, y, z); }
}
