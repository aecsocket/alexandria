package com.gitlab.aecsocket.minecommons.core.serializers;

import com.gitlab.aecsocket.minecommons.core.Logging;
import com.gitlab.aecsocket.minecommons.core.translation.Translation;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Utilities for serializers.
 */
public final class Serializers {
    private Serializers() {}

    /**
     * A {@link TypeSerializerCollection} with the default serializers defined in this package.
     */
    public static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.builder()
            .register(Level.class, LevelSerializer.INSTANCE)
            .register(Logging.Level.class, LoggingLevelSerializer.INSTANCE)
            .register(Locale.class, LocaleSerializer.INSTANCE)
            .register(Translation.class, TranslationSerializer.INSTANCE)
            .build();

    /**
     * Asserts that a node is a string, using {@link ConfigurationNode#getString()}.
     * @param node The node.
     * @param type The target type.
     * @return The string.
     * @throws SerializationException If the node cannot be coerced to a string.
     */
    public static String assertString(ConfigurationNode node, Type type) throws SerializationException {
        String value = node.getString();
        if (node.virtual() || value == null)
            throw new SerializationException(node, type, "Must be string");
        return value;
    }
}
