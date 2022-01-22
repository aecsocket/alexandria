package com.github.aecsocket.minecommons.core.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * Type serializer for a {@link Locale}.
 * <p>
 * Uses {@link Locale#forLanguageTag(String)}.
 */
public class LocaleSerializer implements TypeSerializer<Locale> {
    /** A singleton instance of this serializer. */
    public static final LocaleSerializer INSTANCE = new LocaleSerializer();

    @Override
    public void serialize(Type type, @Nullable Locale obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.toLanguageTag());
        }
    }

    @Override
    public Locale deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return Locale.forLanguageTag(require(node, String.class));
    }
}
