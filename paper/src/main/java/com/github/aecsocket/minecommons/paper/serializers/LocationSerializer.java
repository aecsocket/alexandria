package com.github.aecsocket.minecommons.paper.serializers;

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

    private static final String
        WORLD = "world",
        X = "x",
        Y = "y",
        Z = "z",
        YAW = "yaw",
        PITCH = "pitch";

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
            node.node(YAW).getFloat(0),
            node.node(PITCH).getFloat(0)
        );
    }
}
