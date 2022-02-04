package com.github.aecsocket.minecommons.paper.serializers;

import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;
import com.github.aecsocket.minecommons.paper.effect.PaperParticleEffect;

import org.bukkit.Particle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link PaperParticleEffect}.
 */
public class ParticleEffectSerializer implements TypeSerializer<PaperParticleEffect> {
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
    public void serialize(Type type, @Nullable PaperParticleEffect obj, ConfigurationNode node) throws SerializationException {
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
    public PaperParticleEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Particle particle = require(node.node(PARTICLE), Particle.class);
        return new PaperParticleEffect(
            particle,
            node.node(COUNT).getInt(0),
            node.node(SIZE).get(Vector3.class, Vector3.ZERO),
            node.node(SPEED).getDouble(0),
            particle.getDataType() == void.class ? null : node.node(DATA).get(particle.getDataType())
        );
    }
}
