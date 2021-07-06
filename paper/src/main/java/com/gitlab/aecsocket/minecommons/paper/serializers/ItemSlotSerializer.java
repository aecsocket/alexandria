package com.gitlab.aecsocket.minecommons.paper.serializers;

import com.gitlab.aecsocket.minecommons.paper.ItemSlot;
import org.bukkit.inventory.EquipmentSlot;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Type serializer for an {@link ItemSlot}.
 * <p>
 * Deserializes either by an {@link EquipmentSlot} or a numerical slot index >= 0.
 */
public final class ItemSlotSerializer implements TypeSerializer<ItemSlot> {
    /** A singleton instance of this serializer. */
    public static final ItemSlotSerializer INSTANCE = new ItemSlotSerializer();

    @Override
    public void serialize(Type type, @Nullable ItemSlot obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) node.set(null);
        else {
            if (obj.isEquipment())
                node.set(obj.equipment());
            else
                node.set(obj.index());
        }
    }

    @Override
    public ItemSlot deserialize(Type type, ConfigurationNode node) throws SerializationException {
        int index = node.getInt(-1);
        if (index >= 0)
            return new ItemSlot(index);
        EquipmentSlot equipment = node.get(EquipmentSlot.class);
        if (equipment != null)
            return new ItemSlot(equipment);
        throw new SerializationException(node, type, "Invalid ItemSlot format: must be either numerical index >= 0, or of type " + EquipmentSlot.class);
    }
}
