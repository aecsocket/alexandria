package com.gitlab.aecsocket.minecommons.core.serializers.vector;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Type serializer for a {@link Vector3}.
 */
public class Vector3Serializer extends AbstractVector3Serializer<Vector3> {
    public static final Vector3Serializer INSTANCE = new Vector3Serializer();

    @Override protected double x(Vector3 obj) { return obj.x(); }
    @Override protected double y(Vector3 obj) { return obj.y(); }
    @Override protected double z(Vector3 obj) { return obj.z(); }

    @Override protected Vector3 of(double x, double y, double z) { return new Vector3(x, y, z); }
}
