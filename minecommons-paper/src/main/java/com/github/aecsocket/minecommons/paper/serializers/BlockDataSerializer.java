package com.github.aecsocket.minecommons.paper.serializers;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;

/**
 * Type serializer for a {@link BlockData}.
 * <p>
 * Uses {@link BlockData#getAsString(boolean)} and {@link Bukkit#createBlockData(String)}.
 */
public class BlockDataSerializer implements TypeSerializer<BlockData> {
    /** A singleton instance of this serializer. */
    public static final BlockDataSerializer INSTANCE = new BlockDataSerializer();

    @Override
    public void serialize(Type type, @Nullable BlockData obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.getAsString(true));
        }
    }

    @Override
    public BlockData deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return Bukkit.createBlockData(require(node, String.class));
    }
}
