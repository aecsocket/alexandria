package com.github.aecsocket.minecommons.core.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;

import com.github.aecsocket.minecommons.core.Duration;

/**
 * Type serializer for a {@link Duration}.
 * <p>
 * Uses {@link Duration#duration(String)}.
 */
public class DurationSerializer implements TypeSerializer<Duration> {
    /** A singleton instance of this serializer. */
    public static final DurationSerializer INSTANCE = new DurationSerializer();

    @Override
    public void serialize(Type type, @Nullable Duration obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.toString());
        }
    }

    @Override
    public Duration deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            return Duration.duration(require(node, String.class));
        } catch (IllegalArgumentException e) {
            throw new SerializationException(node, type, e);
        }
    }
}
