package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.gitlab.aecsocket.minecommons.paper.effect.ParticleEffect;
import org.bukkit.Particle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for a {@link ParticleEffect}.
 */
public class ParticleEffectSerializer implements TypeSerializer<ParticleEffect> {
    /** A singleton instance of this serializer. */
    public static final ParticleEffectSerializer INSTANCE = new ParticleEffectSerializer();

    /** The key for the field {@code particle}. */
    public static final String PARTICLE = "particle";
    /** The key for the field {@code count}. */
    public static final String COUNT = "count";
    /** The key for the field {@code size}. */
    public static final String SIZE = "size";
    /** The key for the field {@code speed}. */
    public static final String SPEED = "speed";
    /** The key for the field {@code data}. */
    public static final String DATA = "data";

    @Override
    public void serialize(Type type, @Nullable ParticleEffect obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node(PARTICLE).set(obj.name());
            node.node(COUNT).set(obj.count());
            node.node(SIZE).set(obj.size());
            node.node(SPEED).set(obj.speed());
            if (obj.data() != null)
                node.node(DATA).set(obj.data());
        }
    }

    @Override
    public ParticleEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Particle particle = require(node.node(PARTICLE), Particle.class);
        return new ParticleEffect(
                particle,
                node.node(COUNT).getInt(0),
                node.node(SIZE).get(Vector3.class, Vector3.ZERO),
                node.node(SPEED).getDouble(0),
                particle.getDataType() == void.class ? null : node.node(DATA).get(particle.getDataType())
        );
    }
}
