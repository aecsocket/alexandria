package com.github.aecsocket.minecommons.paper.serializers;

import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link PotionEffectType}.
 * <p>
 * Uses {@link PotionEffectType#getByName(String)}.
 */
public class PotionEffectTypeSerializer implements TypeSerializer<PotionEffectType> {
    /** A singleton instance of this serializer. */
    public static final PotionEffectTypeSerializer INSTANCE = new PotionEffectTypeSerializer();

    @Override
    public void serialize(Type type, @Nullable PotionEffectType obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.getName());
        }
    }

    @Override
    public PotionEffectType deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String name = require(node, String.class);
        PotionEffectType result = PotionEffectType.getByName(name);
        if (result == null)
            throw new SerializationException(node, type, "Invalid potion effect type [" + name + "]");
        return result;
    }
}
