package com.gitlab.aecsocket.minecommons.core.serializers.vector;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A serializer for generic 2-dimensional integer vectors.
 * <p>
 * If all components are the same, de/serializes as a single number.
 * Otherwise, de/serializes as a list of all components.
 * @param <T> The vector type
 */
public abstract class AbstractVector2ISerializer<T> implements TypeSerializer<T> {
    protected abstract int x(T obj);
    protected abstract int y(T obj);

    protected abstract T of(int x, int y);

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            int x = x(obj);
            int y = y(obj);
            if (x == y)
                node.set(x);
            else
                node.setList(Integer.class, Arrays.asList(x, y));
        }
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isList()) {
            return of(
                    node.node(0).getInt(0),
                    node.node(1).getInt(0)
            );
        } else {
            int v = node.getInt();
            return of(v, v);
        }
    }
}
