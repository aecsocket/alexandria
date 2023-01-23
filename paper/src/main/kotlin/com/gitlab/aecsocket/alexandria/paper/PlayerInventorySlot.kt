package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.paper.extension.bukkitAir
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

sealed interface PlayerInventorySlot {
    fun getFrom(inventory: PlayerInventory): ItemStack

    fun asInt(inventory: PlayerInventory): Int

    data class ByInteger(val slot: Int) : PlayerInventorySlot {
        override fun getFrom(inventory: PlayerInventory): ItemStack {
            return inventory.getItem(slot) ?: bukkitAir
        }

        override fun asInt(inventory: PlayerInventory) = slot
    }

    data class ByEquipment(val slot: EquipmentSlot) : PlayerInventorySlot {
        override fun getFrom(inventory: PlayerInventory): ItemStack {
            return inventory.getItem(slot)
        }

        override fun asInt(inventory: PlayerInventory) = SlotToInt[slot] ?: inventory.heldItemSlot
    }

    companion object {
        val IntToSlot = mapOf(
            40 to EquipmentSlot.OFF_HAND,
            36 to EquipmentSlot.FEET,
            37 to EquipmentSlot.LEGS,
            38 to EquipmentSlot.CHEST,
            39 to EquipmentSlot.HEAD,
        )

        val SlotToInt = IntToSlot.map { (a, b) -> b to a }.associate { it }

        fun intToSlot(inventory: PlayerInventory, slotId: Int) = IntToSlot[slotId]
            ?: if (inventory.heldItemSlot == slotId) EquipmentSlot.HAND else null

        fun from(inventory: PlayerInventory, slotId: Int): PlayerInventorySlot {
            return intToSlot(inventory, slotId)?.let { slot -> ByEquipment(slot) }
                ?: ByInteger(slotId)
        }
    }
}
