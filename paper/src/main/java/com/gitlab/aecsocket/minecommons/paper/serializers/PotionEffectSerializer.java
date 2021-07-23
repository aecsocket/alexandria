package com.gitlab.aecsocket.minecommons.paper.serializers;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for a {@link PotionEffect}.
 */
public class PotionEffectSerializer implements TypeSerializer<PotionEffect> {
    /** A singleton instance of this serializer. */
    public static final PotionEffectSerializer INSTANCE = new PotionEffectSerializer();

    @Override
    public void serialize(Type type, @Nullable PotionEffect obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node("type").set(obj.getType());
            node.node("duration").set(obj.getDuration());
            node.node("amplifier").set(obj.getAmplifier());
            node.node("ambient").set(obj.isAmbient());
            node.node("particles").set(obj.hasParticles());
            node.node("icon").set(obj.hasIcon());
        }
    }

    @Override
    public PotionEffect deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new PotionEffect(
                require(node.node("type"), PotionEffectType.class),
                require(node.node("duration"), int.class),
                require(node.node("amplifier"), int.class),
                node.node("ambient").getBoolean(false),
                node.node("particles").getBoolean(true),
                node.node("icon").getBoolean(true)
        );
    }
}
