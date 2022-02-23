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

    private static final String
        NAME = "name",
        COUNT = "count",
        SIZE = "size",
        SPEED = "speed",
        DATA = "data";

    @Override
    public void serialize(Type type, @Nullable PaperParticleEffect obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node(NAME).set(obj.name());
            node.node(COUNT).set(obj.count());
            node.node(SIZE).set(obj.size());
            node.node(SPEED).set(obj.speed());
            if (obj.data() != null)
                node.node(DATA).set(obj.data());
        }
    }

    @Override
    public PaperParticleEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Particle particle = require(node.node(NAME), Particle.class);
        return new PaperParticleEffect(
            particle,
            node.node(COUNT).getDouble(0),
            node.node(SIZE).get(Vector3.class, Vector3.ZERO),
            node.node(SPEED).getDouble(0),
            particle.getDataType() == void.class ? null : node.node(DATA).get(particle.getDataType())
        );
    }
}
