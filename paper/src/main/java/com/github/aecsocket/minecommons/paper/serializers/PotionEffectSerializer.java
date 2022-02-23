package com.github.aecsocket.minecommons.paper.serializers;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link PotionEffect}.
 */
public class PotionEffectSerializer implements TypeSerializer<PotionEffect> {
    /** A singleton instance of this serializer. */
    public static final PotionEffectSerializer INSTANCE = new PotionEffectSerializer();

    private static final String
        TYPE = "type",
        DURATION = "duration",
        AMPLIFIER = "amplifier",
        AMBIENT = "ambient",
        PARTICLES = "particles",
        ICON = "icon";

    @Override
    public void serialize(Type type, @Nullable PotionEffect obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node(TYPE).set(obj.getType());
            node.node(DURATION).set(obj.getDuration());
            node.node(AMPLIFIER).set(obj.getAmplifier());
            node.node(AMBIENT).set(obj.isAmbient());
            node.node(PARTICLES).set(obj.hasParticles());
            node.node(ICON).set(obj.hasIcon());
        }
    }

    @Override
    public PotionEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new PotionEffect(
            require(node.node(TYPE), PotionEffectType.class),
            require(node.node(DURATION), int.class),
            require(node.node(AMPLIFIER), int.class),
            node.node(AMBIENT).getBoolean(false),
            node.node(PARTICLES).getBoolean(true),
            node.node(ICON).getBoolean(true)
        );
    }
}
