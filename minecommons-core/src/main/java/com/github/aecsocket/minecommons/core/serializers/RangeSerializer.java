package com.github.aecsocket.minecommons.core.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.github.aecsocket.minecommons.core.Range;

/**
 * Type serializer for a {@link Range}.
 */
public class RangeSerializer implements TypeSerializer<Range> {
    /** A singleton instance of this serializer. */
    public static final RangeSerializer INSTANCE = new RangeSerializer();

    @Override
    public void serialize(Type type, @Nullable Range obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            if (obj instanceof Range.Integer range)
                node.setList(Integer.class, Arrays.asList(range.min(), range.max()));
            if (obj instanceof Range.Long range)
                node.setList(Long.class, Arrays.asList(range.min(), range.max()));
            if (obj instanceof Range.Float range)
                node.setList(Float.class, Arrays.asList(range.min(), range.max()));
            if (obj instanceof Range.Double range)
                node.setList(Double.class, Arrays.asList(range.min(), range.max()));
        }
    }

    @Override
    public Range deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!node.isList() || node.childrenList().size() != 2)
            throw new SerializationException(node, type, "Range must be expressed as [min, max]");
        ConfigurationNode min = node.node(0);
        ConfigurationNode max = node.node(1);
        if (type == Range.Integer.class)
            return Range.ofInteger(require(min, int.class), require(max, int.class));
        if (type == Range.Long.class)
            return Range.ofLong(require(min, long.class), require(max, long.class));
        if (type == Range.Float.class)
            return Range.ofFloat(require(min, float.class), require(max, float.class));
        if (type == Range.Double.class)
            return Range.ofDouble(require(min, double.class), require(max, double.class));
        throw new SerializationException(node, type, "Invalid deserialization type");
    }
}
