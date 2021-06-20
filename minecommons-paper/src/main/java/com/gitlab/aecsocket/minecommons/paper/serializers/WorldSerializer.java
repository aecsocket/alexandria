package com.gitlab.aecsocket.minecommons.paper.serializers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link World}.
 * <p>
 * Uses {@link Bukkit#getWorld(String)}.
 */
public class WorldSerializer implements TypeSerializer<World> {
    /** A singleton instance of this serializer. */
    public static final WorldSerializer INSTANCE = new WorldSerializer();

    @Override
    public void serialize(Type type, @Nullable World obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.getName());
        }
    }

    @Override
    public World deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String name = node.require(String.class);
        World world = Bukkit.getWorld(name);
        if (world == null)
            throw new SerializationException(node, type, "Invalid world `" + name + "`");
        return world;
    }
}
