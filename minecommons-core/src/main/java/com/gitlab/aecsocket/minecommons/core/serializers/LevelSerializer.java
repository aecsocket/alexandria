package com.gitlab.aecsocket.minecommons.core.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.logging.Level;

/**
 * Type serializer for a {@link Level}.
 * <p>
 * Uses {@link Level#parse(String)}.
 */
public class LevelSerializer implements TypeSerializer<Level> {
    /** A singleton instance of this serializer. */
    public static final LevelSerializer INSTANCE = new LevelSerializer();

    @Override
    public void serialize(Type type, @Nullable Level obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.getName() == null ? obj.intValue() : obj.getName());
        }
    }

    @Override
    public Level deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            return Level.parse(node.getString(""));
        } catch (IllegalArgumentException e) {
            throw new SerializationException(node, type, "Could not parse level", e);
        }
    }
}
