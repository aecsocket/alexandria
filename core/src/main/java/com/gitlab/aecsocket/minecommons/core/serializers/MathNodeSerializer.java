package com.gitlab.aecsocket.minecommons.core.serializers;

import com.gitlab.aecsocket.minecommons.core.expressions.math.MathNode;
import com.gitlab.aecsocket.minecommons.core.expressions.math.MathParser;
import com.gitlab.aecsocket.minecommons.core.expressions.parsing.NodeException;
import com.gitlab.aecsocket.minecommons.core.expressions.parsing.TokenzingException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link MathNode}.
 * <p>
 * Uses {@link MathParser#parse(String)}.
 */
public class MathNodeSerializer implements TypeSerializer<MathNode> {
    /** A singleton instance of this serializer. */
    public static final MathNodeSerializer INSTANCE = new MathNodeSerializer();

    @Override
    public void serialize(Type type, @Nullable MathNode obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
    }

    @Override
    public MathNode deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            return MathParser.parse(node.getString(""));
        } catch (TokenzingException | NodeException e) {
            throw new SerializationException(node, type, "Could not parse math expression", e);
        }
    }
}
