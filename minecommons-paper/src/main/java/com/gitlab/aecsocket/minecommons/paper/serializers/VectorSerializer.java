package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.core.serializers.vector.AbstractVector3DSerializer;
import org.bukkit.util.Vector;

/**
 * Type serializer for a {@link Vector}
 * <p>
 * Uses the format of {@link AbstractVector3DSerializer}.
 */
public class VectorSerializer extends AbstractVector3DSerializer<Vector> {
    /** A singleton instance of this serializer. */
    public static final VectorSerializer INSTANCE = new VectorSerializer();

    @Override protected double x(Vector obj) { return obj.getX(); }
    @Override protected double y(Vector obj) { return obj.getY(); }
    @Override protected double z(Vector obj) { return obj.getZ(); }

    @Override protected Vector of(double x, double y, double z) { return new Vector(x, y, z); }
}
