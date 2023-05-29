package io.github.aecsocket.alexandria.paper.extension

import io.github.aecsocket.alexandria.extension.DEFAULT
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

val isFolia = try {
    Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
    true
} catch (ex: ClassNotFoundException) {
    false
}

@Suppress("DEPRECATION")
fun nextEntityId() = Bukkit.getUnsafe().nextEntityId()

fun Plugin.key(value: String) = NamespacedKey(this, value)

fun Plugin.registerEvents(listener: Listener) =
    Bukkit.getPluginManager().registerEvents(listener, this)

fun <M : ItemMeta> ItemStack.withMeta(block: (M) -> Unit): ItemStack {
    editMeta { meta ->
        @Suppress("UNCHECKED_CAST")
        block(meta as M)
    }
    return this
}

fun <V> Map<String, V>.forWorld(world: World) = get(world.name) ?: get(DEFAULT)
