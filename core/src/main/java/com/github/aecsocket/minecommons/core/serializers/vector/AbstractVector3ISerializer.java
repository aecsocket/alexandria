package com.github.aecsocket.minecommons.core.serializers.vector;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A serializer for generic 3-dimensional integer vectors.
 * <p>
 * If all components are the same, de/serializes as a single number.
 * Otherwise, de/serializes as a list of all components.
 * @param <T> The vector type
 */
public abstract class AbstractVector3ISerializer<T> implements TypeSerializer<T> {
    /**
     * Gets the X component of a vector object.
     * @param obj The object.
     * @return The component.
     */
    protected abstract int x(T obj);

    /**
     * Gets the Y component of a vector object.
     * @param obj The object.
     * @return The component.
     */
    protected abstract int y(T obj);

    /**
     * Gets the Z component of a vector object.
     * @param obj The object.
     * @return The component.
     */
    protected abstract int z(T obj);

    /**
     * Creates a vector object from an x, y, z triplet.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Z component.
     * @return The vector object.
     */
    protected abstract T of(int x, int y, int z);

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            int x = x(obj);
            int y = y(obj);
            int z = z(obj);
            if (x == y && x == z)
                node.set(x);
            else
                node.setList(Integer.class, Arrays.asList(x, y, z));
        }
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isList()) {
            return of(
                    node.node(0).getInt(0),
                    node.node(1).getInt(0),
                    node.node(2).getInt(0)
            );
        } else {
            int v = node.getInt();
            return of(v, v, v);
        }
    }
}
