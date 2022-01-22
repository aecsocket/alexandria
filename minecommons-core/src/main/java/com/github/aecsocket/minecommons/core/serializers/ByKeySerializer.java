package com.github.aecsocket.minecommons.core.serializers;

import com.google.common.collect.BiMap;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Type serializer for objects which can be associated with a key.
 */
public class ByKeySerializer<T> implements TypeSerializer<T> {
    private final Function<String, T> toT;
    private final Function<T, String> toKey;

    /**
     * Creates an instance.
     * @param toT Function mapping a key to a T.
     * @param toKey Function mapping a T to a key.
     */
    public ByKeySerializer(Function<String, T> toT, Function<T, String> toKey) {
        this.toT = toT;
        this.toKey = toKey;
    }

    /**
     * Creates an instance.
     * @param map The bi-map to generate the T/key mapping functions from.
     */
    public ByKeySerializer(BiMap<String, T> map) {
        toT = map::get;
        toKey = map.inverse()::get;
    }

    /**
     * Gets the function mapping a key to a T.
     * @return The function.
     */
    public Function<String, T> toT() { return toT; }

    /**
     * Gets the function mapping a T to a key.
     * @return The function.
     */
    public Function<T, String> toKey() { return toKey; }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(toKey.apply(obj));
        }
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String key = require(node, String.class);
        T obj = toT.apply(key);
        if (obj == null)
            throw new SerializationException(node, type, "Invalid key '" + key + "'");
        return obj;
    }
}
