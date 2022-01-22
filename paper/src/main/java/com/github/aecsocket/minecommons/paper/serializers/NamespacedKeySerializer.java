package com.github.aecsocket.minecommons.paper.serializers;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link NamespacedKey}.
 * <p>
 * Uses the same format as {@link net.kyori.adventure.serializer.configurate4.KeySerializer}
 */
public class NamespacedKeySerializer implements TypeSerializer<NamespacedKey> {
    /** A singleton instance of this serializer. */
    public static final NamespacedKeySerializer INSTANCE = new NamespacedKeySerializer();

    @Override
    public void serialize(Type type, @Nullable NamespacedKey obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(Key.class, obj);
        }
    }

    @Override
    public NamespacedKey deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Key key = require(node, Key.class);
        @SuppressWarnings("deprecation") // No.
        NamespacedKey result = new NamespacedKey(key.namespace(), key.value());
        return result;
    }
}
