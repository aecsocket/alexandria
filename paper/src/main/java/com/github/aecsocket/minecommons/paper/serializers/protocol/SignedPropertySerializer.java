package com.github.aecsocket.minecommons.paper.serializers.protocol;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link WrappedSignedProperty}.
 */
public class SignedPropertySerializer implements TypeSerializer<WrappedSignedProperty> {
    /** A singleton instance of this serializer. */
    public static final SignedPropertySerializer INSTANCE = new SignedPropertySerializer();

    @Override
    public void serialize(Type type, @Nullable WrappedSignedProperty obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node("name").set(obj.getName());
            node.node("value").set(obj.getValue());
            node.node("signature").set(obj.getSignature());
        }
    }

    @Override
    public WrappedSignedProperty deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new WrappedSignedProperty(
                node.node("name").getString(),
                node.node("value").getString(),
                node.node("signature").getString()
        );
    }
}
