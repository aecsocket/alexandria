package com.gitlab.aecsocket.minecommons.core.serializers;

import com.gitlab.aecsocket.minecommons.core.Quantifier;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link Quantifier}.
 */
public class QuantifierSerializer<T> implements TypeSerializer<Quantifier<T>> {
    private final TypeToken<T> type;

    /**
     * Creates an instance.
     * @param type The unwrapped type.
     */
    public QuantifierSerializer(TypeToken<T> type) {
        this.type = type;
    }

    /**
     * Gets the unwrapped type.
     * @return The unwrapped type.
     */
    public TypeToken<T> type() { return type; }

    @Override
    public void serialize(Type type, @Nullable Quantifier<T> obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.appendListNode().set(obj.object());
            node.appendListNode().set(obj.amount());
        }
    }

    @Override
    public Quantifier<T> deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var list = node.childrenList();
        if (list.size() != 2)
            throw new SerializationException(node, type, "Node must be list of [object, amount]");
        return new Quantifier<>(
                Serializers.require(list.get(0), this.type),
                list.get(1).getInt()
        );
    }
}
