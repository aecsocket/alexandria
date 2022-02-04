package com.github.aecsocket.minecommons.paper.serializers.protocol;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Type serializer for a {@link WrappedGameProfile}.
 */
public class GameProfileSerializer implements TypeSerializer<WrappedGameProfile> {
    /** A singleton instance of this serializer. */
    public static final GameProfileSerializer INSTANCE = new GameProfileSerializer();

    @Override
    public void serialize(Type type, @Nullable WrappedGameProfile obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node("id").set(obj.getUUID());
            node.node("name").set(obj.getName());
            ConfigurationNode properties = node.node("properties");
            for (var entry : obj.getProperties().asMap().entrySet()) {
                properties.node(entry.getKey())
                    .setList(WrappedSignedProperty.class, new ArrayList<>(entry.getValue()));
            }
        }
    }

    @Override
    public WrappedGameProfile deserialize(Type type, ConfigurationNode node) throws SerializationException {
        WrappedGameProfile result = new WrappedGameProfile(
            node.node("id").get(UUID.class),
            node.node("name").getString()
        );
        var properties = node.node("properties").childrenMap();
        for (var entry : properties.entrySet()) {
            result.getProperties().asMap()
                .put(entry.getKey().toString(), entry.getValue().getList(WrappedSignedProperty.class));
        }
        return result;
    }
}
