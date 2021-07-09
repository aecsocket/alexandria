package com.gitlab.aecsocket.minecommons.core.serializers.color;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A serializer for generic R, G, B triples.
 * <p>
 * If all components are the same, de/serializes as a single number.
 * Otherwise, de/serializes as a list of all components.
 * @param <T> The triplet type.
 */
public abstract class AbstractRGBSerializer<T> implements TypeSerializer<T> {
    /**
     * The manner in which to serialize the value.
     */
    public enum Format {
        /** As the raw integer. */
        VALUE,
        /** As a hex representation, beginning with {@code #}. */
        HEX,
        /** As a list of the individual R, G, B components. */
        COMPONENTS
    }

    private Format format;

    /**
     * Creates an instance.
     * @param format The format to use.
     */
    public AbstractRGBSerializer(Format format) {
        this.format = format;
    }

    /**
     * Gets the format.
     * @return The format.
     */
    public Format format() { return format; }

    /**
     * Sets the format.
     * @param format The format.
     */
    public void format(Format format) { this.format = format; }

    /**
     * Gets the integer-compressed value of a color object.
     * @param obj The object.
     * @return The value.
     */
    protected abstract int value(T obj);

    /**
     * Gets the red component of a color object.
     * @param obj The object.
     * @return The value.
     */
    protected abstract double r(T obj);

    /**
     * Gets the green component of a color object.
     * @param obj The object.
     * @return The value.
     */
    protected abstract double g(T obj);

    /**
     * Gets the blue component of a color object.
     * @param obj The object.
     * @return The value.
     */
    protected abstract double b(T obj);

    /**
     * Creates a color object from an integer-compressed value.
     * @param value The value.
     * @return The object.
     */
    protected abstract T of(int value);

    /**
     * Creates a color object from red, green, blue values.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return The object.
     */
    protected T of(int r, int g, int b) {
        return of(
                (r & 0xff) << 16
                | (g & 0xff) << 8
                | (b & 0xff)
        );
    }

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            switch (format) {
                case VALUE -> node.set(value(obj));
                case HEX -> node.set("#" + String.format("#%x", value(obj)));
                case COMPONENTS -> node.setList(Double.class, Arrays.asList(r(obj), g(obj), b(obj)));
            }
        }
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String hex;
        if (node.isList()) {
            return of(
                    node.node(0).getInt(0),
                    node.node(1).getInt(0),
                    node.node(2).getInt(0)
            );
        } else if ((hex = node.getString("")).startsWith("#")) {
            return of((int) Long.parseLong(hex.substring(1), 16));
        } else {
            return fallback(type, node);
        }
    }

    /**
     * Performs deserialization if no other deserialization option could be performed.
     * @param type The type.
     * @param node The node.
     * @return The fallback value.
     * @throws SerializationException If there was an error when serializing.
     */
    protected T fallback(Type type, ConfigurationNode node) throws SerializationException {
        return of(node.getInt());
    }
}
