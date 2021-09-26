package com.gitlab.aecsocket.minecommons.core.serializers;

import com.gitlab.aecsocket.minecommons.core.Duration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

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
        return Duration.duration(require(node, String.class));
    }
}
