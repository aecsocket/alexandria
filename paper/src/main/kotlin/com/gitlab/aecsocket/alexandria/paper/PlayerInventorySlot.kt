package com.gitlab.aecsocket.alexandria.paper

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

sealed interface PlayerInventorySlot {
    fun getFrom(inventory: PlayerInventory): ItemStack

    data class ByInteger(val slot: Int) : PlayerInventorySlot {
        override fun getFrom(inventory: PlayerInventory): ItemStack {
            return inventory.getItem(slot) ?: ItemStack(Material.AIR)
        }
    }

    data class ByEquipment(val slot: EquipmentSlot) : PlayerInventorySlot {
        override fun getFrom(inventory: PlayerInventory): ItemStack {
            return inventory.getItem(slot)
        }
    }
}
