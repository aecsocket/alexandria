package com.gitlab.aecsocket.minecommons.core.serializers;

import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for a {@link Key}.
 * <p>
 * Uses {@link Key#key(String)}.
 */
public class KeySerializer implements TypeSerializer<Key> {
    /** A singleton instance of this serializer. */
    public static final KeySerializer INSTANCE = new KeySerializer();

    @Override
    public void serialize(Type type, @Nullable Key obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            if (obj.namespace().equals(Key.MINECRAFT_NAMESPACE))
                node.set(obj.value());
            else
                node.setList(String.class, Arrays.asList(obj.namespace(), obj.value()));
        }
    }

    @Override
    public Key deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            return Key.key(require(node, String.class));
        } catch (InvalidKeyException e) {
            throw new SerializationException(node, type, e);
        }
    }
}
