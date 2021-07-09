package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.gitlab.aecsocket.minecommons.paper.display.Particles;
import org.bukkit.Particle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for a {@link Particles}.
 */
public class ParticlesSerializer implements TypeSerializer<Particles> {
    /** A singleton instance of this serializer. */
    public static final ParticlesSerializer INSTANCE = new ParticlesSerializer();

    @Override
    public void serialize(Type type, @Nullable Particles obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node("particle").set(obj.particle());
            node.node("count").set(obj.count());
            node.node("size").set(obj.size());
            node.node("speed").set(obj.speed());
            if (obj.data() != null)
                node.node("data").set(obj.data());
        }
    }

    @Override
    public Particles deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Particle particle = require(node.node("particle"), Particle.class);
        return Particles.particles(
                particle,
                node.node("count").getInt(0),
                node.node("size").get(Vector3.class, Vector3.ZERO),
                node.node("speed").getDouble(0),
                particle.getDataType() == Void.class ? null : node.node("data").get(particle.getDataType())
        );
    }
}
