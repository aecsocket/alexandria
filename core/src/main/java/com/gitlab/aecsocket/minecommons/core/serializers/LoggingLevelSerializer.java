package com.gitlab.aecsocket.minecommons.core.serializers;

import com.gitlab.aecsocket.minecommons.core.Logging;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Type serializer for a {@link Logging.Level}.
 * <p>
 * Uses {@link Logging.Level#valueOf(String)}.
 */
public class LoggingLevelSerializer implements TypeSerializer<Logging.Level> {
    /** A singleton instance of this serializer. */
    public static final LoggingLevelSerializer INSTANCE = new LoggingLevelSerializer();

    @Override
    public void serialize(Type type, Logging.@Nullable Level obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.name());
        }
    }

    @Override
    public Logging.Level deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            return Logging.Level.valueOf(node.getString("").toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new SerializationException(node, type, "Could not parse Logging.Level", e);
        }
    }
}
