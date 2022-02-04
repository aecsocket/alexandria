package com.github.aecsocket.minecommons.core.serializers;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.logging.Level;

import com.github.aecsocket.minecommons.core.Duration;
import com.github.aecsocket.minecommons.core.Logging;
import com.github.aecsocket.minecommons.core.Range;
import com.github.aecsocket.minecommons.core.effect.SoundEffect;
import com.github.aecsocket.minecommons.core.expressions.math.MathNode;
import com.github.aecsocket.minecommons.core.i18n.Format;
import com.github.aecsocket.minecommons.core.serializers.color.TextColorSerializer;
import com.github.aecsocket.minecommons.core.serializers.vector.Point2Serializer;
import com.github.aecsocket.minecommons.core.serializers.vector.Point3Serializer;
import com.github.aecsocket.minecommons.core.serializers.vector.Vector2Serializer;
import com.github.aecsocket.minecommons.core.serializers.vector.Vector3Serializer;
import com.github.aecsocket.minecommons.core.vector.cartesian.Point2;
import com.github.aecsocket.minecommons.core.vector.cartesian.Point3;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector2;
import com.github.aecsocket.minecommons.core.vector.cartesian.Vector3;

/**
 * Utilities for serializers.
 */
public final class Serializers {
    private Serializers() {}

    private static final String VALUE_REQUIRED = "A value is required for this field";

    /**
     * A {@link TypeSerializerCollection} with the default serializers defined in this package.
     */
    public static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.builder()
        .registerExact(Level.class, LevelSerializer.INSTANCE)
        .registerExact(Logging.Level.class, LoggingLevelSerializer.INSTANCE)
        .registerExact(Locale.class, LocaleSerializer.INSTANCE)
        .registerExact(Vector2.class, Vector2Serializer.INSTANCE)
        .registerExact(Vector3.class, Vector3Serializer.INSTANCE)
        .registerExact(Point2.class, Point2Serializer.INSTANCE)
        .registerExact(Point3.class, Point3Serializer.INSTANCE)
        .registerExact(TextColor.class, TextColorSerializer.INSTANCE)
        .registerExact(Duration.class, DurationSerializer.INSTANCE)
        .registerExact(MathNode.class, MathNodeSerializer.INSTANCE)
        .registerExact(Format.class, I18NFormatSerializer.INSTANCE)
        .register(Range.class, RangeSerializer.INSTANCE)
        .register(SoundEffect.class, SoundEffectSerializer.INSTANCE)
        .registerAll(ConfigurateComponentSerializer.configurate().serializers())
        .build();

    /**
     * Requires a value to be present, or throws an exception.
     * @param node The node to get the value from.
     * @param type The type of value.
     * @param <V> The type of value.
     * @return The value.
     * @throws SerializationException If the value was not present.
     */
    public static <V> V require(ConfigurationNode node, Class<V> type) throws SerializationException {
        V result = node.get(type);
        if (result == null)
            throw new SerializationException(node, type, VALUE_REQUIRED);
        return result;
    }

    /**
     * Requires a value to be present, or throws an exception.
     * @param node The node to get the value from.
     * @param type The type of value.
     * @param <V> The type of value.
     * @return The value.
     * @throws SerializationException If the value was not present.
     */
    public static <V> V require(ConfigurationNode node, TypeToken<V> type) throws SerializationException {
        V result = node.get(type);
        if (result == null)
            throw new SerializationException(node, type.getType(), VALUE_REQUIRED);
        return result;
    }

    /**
     * Requires a value to be present, or throws an exception.
     * @param node The node to get the value from.
     * @param type The type of value.
     * @return The value.
     * @throws SerializationException If the value was not present.
     */
    public static Object require(ConfigurationNode node, Type type) throws SerializationException {
        Object result = node.get(type);
        if (result == null)
            throw new SerializationException(node, type, VALUE_REQUIRED);
        return result;
    }
}
