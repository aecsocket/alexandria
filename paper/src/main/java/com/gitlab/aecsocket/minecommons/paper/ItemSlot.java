package com.gitlab.aecsocket.minecommons.paper;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * A slot in an inventory which supports both {@link EquipmentSlot}s and numerical indexes.
 * <p>
 * Supported for {@link PlayerInventory}.
 */
public final class ItemSlot {
    private final EquipmentSlot equipment;
    private final int index;

    /**
     * Creates a slot.
     * @param equipment The underlying equipment slot.
     */
    public ItemSlot(EquipmentSlot equipment) {
        this.equipment = equipment;
        index = -1;
    }

    /**
     * Creates a slot.
     * @param index The underlying integer slot index.
     */
    public ItemSlot(int index) {
        equipment = null;
        this.index = index;
    }

    /**
     * Gets the equipment slot stored.
     * @return The equipment slot.
     * @throws NoSuchElementException If there is no equipment slot present.
     */
    public @NonNull EquipmentSlot equipment() throws NoSuchElementException {
        if (equipment == null)
            throw new NoSuchElementException("No equipment slot present");
        return equipment;
    }

    /**
     * Gets the slot index stored.
     * @return The slot index.
     * @throws NoSuchElementException If there is no index present.
     */
    public int index() throws NoSuchElementException {
        if (index < 0)
            throw new NoSuchElementException("No index present");
        return index;
    }

    /**
     * If the value stored is an equipment slot.
     * @return The result.
     */
    public boolean isEquipment() { return equipment != null; }

    /**
     * Runs an action depending on if an equipment slot or slot index is stored.
     * @param ifEquipment Function to run if an equipment slot is present.
     * @param ifIndex Function to run if a slot index is present.
     */
    public void run(Consumer<EquipmentSlot> ifEquipment, IntConsumer ifIndex) {
        if (isEquipment())
            ifEquipment.accept(equipment);
        else
            ifIndex.accept(index);
    }

    /**
     * Gets a value from a mapper function depending on if an equipment slot or slot index is stored.
     * @param ifEquipment Function to apply if an equipment slot is present.
     * @param ifIndex Function to apply if a slot index is present.
     * @return The item result.
     */
    public ItemStack get(Function<EquipmentSlot, ItemStack> ifEquipment, IntFunction<ItemStack> ifIndex) {
        if (isEquipment())
            return ifEquipment.apply(equipment);
        else
            return ifIndex.apply(index);
    }

    /**
     * Gets the item stored in an inventory based on this slot.
     * @param inventory The inventory.
     * @return The item stored.
     */
    public ItemStack get(PlayerInventory inventory) {
        return get(inventory::getItem, inventory::getItem);
    }

    /**
     * Sets the item stored in an inventory based on this slot.
     * @param inventory The inventory.
     * @param item The item to store.
     */
    public void set(PlayerInventory inventory, ItemStack item) {
        run(e -> inventory.setItem(e, item), i -> inventory.setItem(i, item));
    }
}
