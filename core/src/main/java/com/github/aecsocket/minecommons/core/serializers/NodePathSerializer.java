package com.github.aecsocket.minecommons.core.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.aecsocket.minecommons.core.node.NodePath;

/**
 * Type serializer for a {@link NodePath}.
 */
public class NodePathSerializer implements TypeSerializer<NodePath> {
    /** A singleton instance of this serializer. */
    public static final NodePathSerializer INSTANCE = new NodePathSerializer();

    @Override
    public void serialize(Type type, @Nullable NodePath obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.setList(String.class, obj.list());
        }
    }

    @Override
    public NodePath deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (!node.isList())
            throw new SerializationException(node, type, "Node path must be list");
        List<String> path = new ArrayList<>();
        for (var elem : node.childrenList()) {
            path.add(require(elem, String.class));
        }
        return NodePath.path(path);
    }
}
