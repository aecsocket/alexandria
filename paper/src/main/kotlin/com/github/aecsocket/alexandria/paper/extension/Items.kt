package com.github.aecsocket.alexandria.paper.extension

import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack

fun EntityEquipment.forEach(action: (ItemStack) -> Unit) {
    action(itemInMainHand)
    action(itemInOffHand)
    helmet?.let { action(it) }
    chestplate?.let { action(it) }
    leggings?.let { action(it) }
    boots?.let { action(it) }
}
