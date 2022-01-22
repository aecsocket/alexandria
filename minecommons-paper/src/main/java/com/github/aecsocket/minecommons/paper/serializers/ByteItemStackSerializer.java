package com.github.aecsocket.minecommons.paper.serializers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import static com.github.aecsocket.minecommons.core.serializers.Serializers.require;

import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Type serializer for an {@link ItemStack}, serializing as an internal representation of bytes.
 * <p>
 * Uses {@link ItemStack#serializeAsBytes()} and {@link ItemStack#deserializeBytes(byte[])}, as base 64.
 * If the material is {@link Material#AIR}, saves an empty string instead.
 */
public class ByteItemStackSerializer implements TypeSerializer<ItemStack> {
    /** A singleton instance of this serializer. */
    public static final ByteItemStackSerializer INSTANCE = new ByteItemStackSerializer();

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            node.set(obj.getType() == Material.AIR
                    ? "" : Base64.getEncoder().encodeToString(obj.serializeAsBytes()));
        }
    }

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String string = require(node, String.class);
        if (string.isEmpty())
            return new ItemStack(Material.AIR);
        try {
            byte[] bytes = Base64.getDecoder().decode(string);
            return ItemStack.deserializeBytes(bytes);
        } catch (IllegalArgumentException e) {
            throw new SerializationException(node, type, "Invalid base 64", e);
        } catch (RuntimeException e) {
            throw new SerializationException(node, type, "Could not deserialize item", e);
        }
    }
}
