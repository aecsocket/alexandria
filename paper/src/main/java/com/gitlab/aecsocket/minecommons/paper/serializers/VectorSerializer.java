package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.core.serializers.vector.AbstractVector3Serializer;
import org.bukkit.util.Vector;

/**
 * Type serializer for a {@link Vector}
 * <p>
 * Uses the format of {@link AbstractVector3Serializer}.
 */
public class VectorSerializer extends AbstractVector3Serializer<Vector> {
    public static final VectorSerializer INSTANCE = new VectorSerializer();

    @Override protected double x(Vector obj) { return obj.getX(); }
    @Override protected double y(Vector obj) { return obj.getY(); }
    @Override protected double z(Vector obj) { return obj.getZ(); }

    @Override protected Vector of(double x, double y, double z) { return new Vector(x, y, z); }
}
