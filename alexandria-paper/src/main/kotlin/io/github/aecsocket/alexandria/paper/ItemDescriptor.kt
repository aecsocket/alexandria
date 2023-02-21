package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.paper.extension.withMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

@ConfigSerializable
data class ItemDescriptor(
    @Required val material: Material,
    val modelData: Int = 0,
    val damage: Int = 0,
    val unbreakable: Boolean = false,
    val flags: List<ItemFlag> = emptyList(),
) {
    fun create(): ItemStack {
        return ItemStack(material).withMeta<ItemMeta> { meta ->
            meta.setCustomModelData(modelData)
            if (meta is Damageable) {
                meta.damage = damage
            }
            meta.isUnbreakable = unbreakable
            meta.addItemFlags(*flags.toTypedArray())
        }
    }
}
