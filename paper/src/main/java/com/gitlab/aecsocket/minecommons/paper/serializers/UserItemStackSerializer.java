package com.gitlab.aecsocket.minecommons.paper.serializers;

import io.leangen.geantyref.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Map;

import static com.gitlab.aecsocket.minecommons.core.serializers.Serializers.require;

/**
 * Type serializer for an {@link ItemStack}, serializing as the map representation, configurable by users.
 * <p>
 * Uses {@link ItemStack#serialize()} and {@link ItemStack#deserialize(Map)}.
 */
public class UserItemStackSerializer implements TypeSerializer<ItemStack> {
    /** A singleton instance of this serializer. */
    public static final UserItemStackSerializer INSTANCE = new UserItemStackSerializer();

    private static final String keyMeta = "meta";

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            var map = obj.serialize();
            if (map.containsKey(keyMeta)) {
                var meta = (ItemMeta) map.get(keyMeta);
                map.put(keyMeta, meta.serialize());
            }
            node.set(map);
        }
    }

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var map = require(node, new TypeToken<Map<String, Object>>() {});
        if (map.containsKey(keyMeta)) {
            @SuppressWarnings("unchecked")
            var metaMap = (Map<String, Object>) map.get(keyMeta);
            map.put(keyMeta, ConfigurationSerialization.deserializeObject(metaMap));
        }
        return ItemStack.deserialize(map);
    }
}
