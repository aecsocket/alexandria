package com.github.aecsocket.minecommons.core.serializers.vector;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * A serializer for generic 3-dimensional double vectors.
 * <p>
 * If all components are the same, de/serializes as a single number.
 * Otherwise, de/serializes as a list of all components.
 * @param <T> The vector type
 */
public abstract class AbstractVector3DSerializer<T> implements TypeSerializer<T> {
    /**
     * Gets the X component of a vector object.
     * @param obj The object.
     * @return The component.
     */
    protected abstract double x(T obj);

    /**
     * Gets the Y component of a vector object.
     * @param obj The object.
     * @return The component.
     */
    protected abstract double y(T obj);

    /**
     * Gets the Z component of a vector object.
     * @param obj The object.
     * @return The component.
     */
    protected abstract double z(T obj);

    /**
     * Creates a vector object from an x, y, z triplet.
     * @param x The X component.
     * @param y The Y component.
     * @param z The Y component.
     * @return The vector object.
     */
    protected abstract T of(double x, double y, double z);

    @Override
    public void serialize(Type type, @Nullable T obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            double x = x(obj);
            double y = y(obj);
            double z = z(obj);
            if (Double.compare(x, y) == 0 && Double.compare(x, z) == 0)
                node.set(x);
            else
                node.setList(Double.class, Arrays.asList(x, y, z));
        }
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isList()) {
            return of(
                node.node(0).getDouble(0),
                node.node(1).getDouble(0),
                node.node(2).getDouble(0)
            );
        } else if (node.raw() instanceof String string) {
            if (string.startsWith("#")) {
                try {
                    int hex = (int) Long.parseLong(string.substring(1), 16);
                    return of(
                            ((hex >> 16) & 0xff) / 255d,
                            ((hex >> 8) & 0xff) / 255d,
                            (hex & 0xff) / 255d
                    );
                } catch (NumberFormatException e) {
                    throw new SerializationException(node, type, "Invalid hex color '" + string + "'", e);
                }
            } else {
                TextColor color = NamedTextColor.NAMES.value(string);
                if (color != null)
                    return of(color.red(), color.green(), color.blue());
            }
        }

        double v = node.getDouble();
        return of(v, v, v);
    }
}
