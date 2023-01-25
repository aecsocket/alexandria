package io.gitlab.aecsocket.alexandria.paper.extension

import org.bukkit.Material
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun ItemStack?.isEmpty() = this == null || type == Material.AIR || amount == 0

inline fun <reified M : ItemMeta> ItemStack.withMeta(action: (M) -> Unit): ItemStack {
    itemMeta?.let { meta ->
        action(meta as M)
        itemMeta = meta
    }
    return this
}

@JvmName("withRawMeta")
inline fun ItemStack.withMeta(action: (ItemMeta) -> Unit) = withMeta<ItemMeta>(action)

fun EntityEquipment.forEach(action: (ItemStack) -> Unit) {
    action(itemInMainHand)
    action(itemInOffHand)
    helmet?.let { action(it) }
    chestplate?.let { action(it) }
    leggings?.let { action(it) }
    boots?.let { action(it) }
}
