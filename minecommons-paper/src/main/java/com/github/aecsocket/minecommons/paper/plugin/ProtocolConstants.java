package com.github.aecsocket.minecommons.paper.plugin;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.aecsocket.minecommons.core.CollectionBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

/**
 * Lists constants in ProtocolLib. In a separate class so that this does not get statically intialized.
 */
public final class ProtocolConstants {
    private ProtocolConstants() {}

    /**
     * A map of Bukkit {@link EquipmentSlot}s to protocol {@link EnumWrappers.ItemSlot}s.
     */
    public static final BiMap<EquipmentSlot, EnumWrappers.ItemSlot> SLOTS = HashBiMap.create(CollectionBuilder.map(new HashMap<EquipmentSlot, EnumWrappers.ItemSlot>())
            .put(EquipmentSlot.HAND, EnumWrappers.ItemSlot.MAINHAND)
            .put(EquipmentSlot.OFF_HAND, EnumWrappers.ItemSlot.OFFHAND)
            .put(EquipmentSlot.HEAD, EnumWrappers.ItemSlot.HEAD)
            .put(EquipmentSlot.CHEST, EnumWrappers.ItemSlot.CHEST)
            .put(EquipmentSlot.LEGS, EnumWrappers.ItemSlot.LEGS)
            .put(EquipmentSlot.FEET, EnumWrappers.ItemSlot.FEET)
            .build());

    /**
     * A map of Bukkit {@link EquipmentSlot}s to numerical protocol slot IDs.
     */
    public static final BiMap<EquipmentSlot, Integer> SLOT_IDS = HashBiMap.create(CollectionBuilder.map(new HashMap<EquipmentSlot, Integer>())
            .put(EquipmentSlot.FEET, 36)
            .put(EquipmentSlot.LEGS, 37)
            .put(EquipmentSlot.CHEST, 38)
            .put(EquipmentSlot.HEAD, 39)
            .put(EquipmentSlot.OFF_HAND, 40)
            .build());
}