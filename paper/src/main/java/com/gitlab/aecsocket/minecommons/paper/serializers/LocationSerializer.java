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

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.node("world").set(obj.getWorld());
            node.node("x").set(obj.getX());
            node.node("y").set(obj.getY());
            node.node("z").set(obj.getZ());
            node.node("yaw").set(obj.getYaw());
            node.node("pitch").set(obj.getPitch());
        }
    }

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new Location(
                node.node("world").get(World.class),
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                (float) node.node("yaw").getDouble(0),
                (float) node.node("pitch").getDouble(0)
        );
    }
}
