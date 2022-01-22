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

    /** The key for the field {@code type}. */
    public static final String TYPE = "type";
    /** The key for the field {@code duration}. */
    public static final String DURATION = "duration";
    /** The key for the field {@code amplifier}. */
    public static final String AMPLIFIER = "amplifier";
    /** The key for the field {@code ambient}. */
    public static final String AMBIENT = "ambient";
    /** The key for the field {@code particles}. */
    public static final String PARTICLES = "particles";
    /** The key for the field {@code icon}. */
    public static final String ICON = "icon";

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
