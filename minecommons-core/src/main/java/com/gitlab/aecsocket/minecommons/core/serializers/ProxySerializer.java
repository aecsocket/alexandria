package com.gitlab.aecsocket.minecommons.core.serializers;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * De/serializes a class by operating on another type which extends {@link T}.
 * @param <T> The original type.
 */
public final class ProxySerializer<T> implements TypeSerializer<T> {
    private final TypeToken<? extends T> type;

    /**
     * Creates an instance.
     * @param type The operation type.
     */
    public ProxySerializer(TypeToken<? extends T> type) {
        this.type = type;
    }

    /**
     * Gets the operation type.
     * @return The operation type.
     */
    public TypeToken<? extends T> type() { return type; }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        node.set(type, obj);
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return node.get(this.type);
    }
}
