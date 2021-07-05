package com.gitlab.aecsocket.minecommons.core.serializers;

import com.gitlab.aecsocket.minecommons.core.Logging;
import com.gitlab.aecsocket.minecommons.core.serializers.color.TextColorSerializer;
import com.gitlab.aecsocket.minecommons.core.serializers.vector.Point2Serializer;
import com.gitlab.aecsocket.minecommons.core.serializers.vector.Vector3Serializer;
import com.gitlab.aecsocket.minecommons.core.translation.Translation;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Point2;
import com.gitlab.aecsocket.minecommons.core.vector.cartesian.Vector3;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.Required;
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
            .register(Vector3.class, Vector3Serializer.INSTANCE)
            .register(Point2.class, Point2Serializer.INSTANCE)
            .register(TextColor.class, TextColorSerializer.INSTANCE)
            .registerExact(Key.class, KeySerializer.INSTANCE)
            .registerAll(ConfigurateComponentSerializer.configurate().serializers())
            .build();

    public static <V> V require(ConfigurationNode node, Class<V> type) throws SerializationException {
        V result = node.get(type);
        if (result == null)
            throw new SerializationException(node, type, "A value is required for this field");
        return result;
    }

    public static <V> V require(ConfigurationNode node, TypeToken<V> type) throws SerializationException {
        V result = node.get(type);
        if (result == null)
            throw new SerializationException(node, type.getType(), "A value is required for this field");
        return result;
    }

    public static Object require(ConfigurationNode node, Type type) throws SerializationException {
        Object result = node.get(type);
        if (result == null)
            throw new SerializationException(node, type, "A value is required for this field");
        return result;
    }
}