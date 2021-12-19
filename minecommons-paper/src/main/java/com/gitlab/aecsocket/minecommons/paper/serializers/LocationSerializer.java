package com.gitlab.aecsocket.minecommons.paper.serializers;

import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link Location}.
 */
public class LocationSerializer implements TypeSerializer<Location> {
    /** A singleton instance of this serializer. */
    public static final LocationSerializer INSTANCE = new LocationSerializer();

    /** The key for the field {@code world}. */
    public static final String WORLD = "world";
    /** The key for the field {@code x}. */
    public static final String X = "x";
    /** The key for the field {@code y}. */
    public static final String Y = "y";
    /** The key for the field {@code z}. */
    public static final String Z = "z";
    /** The key for the field {@code yaw}. */
    public static final String YAW = "yaw";
    /** The key for the field {@code pitch}. */
    public static final String PITCH = "pitch";

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node(WORLD).set(obj.getWorld());
            node.node(X).set(obj.getX());
            node.node(Y).set(obj.getY());
            node.node(Z).set(obj.getZ());
            node.node(YAW).set(obj.getYaw());
            node.node(PITCH).set(obj.getPitch());
        }
    }

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new Location(
                node.node(WORLD).get(World.class),
                node.node(X).getDouble(),
                node.node(Y).getDouble(),
                node.node(Z).getDouble(),
                // I don't remember why you don't just do #getFloat here.
                // But I'm pretty sure if you use #getFloat, it breaks.
                // TODO confirm this.
                (float) node.node(YAW).getDouble(0),
                (float) node.node(PITCH).getDouble(0)
        );
    }
}
