package io.github.aecsocket.alexandria.paper.extension

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import java.io.IOException
import java.io.InputStream

val isFolia = try {
    Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
    true
} catch (ex: ClassNotFoundException) {
    false
}

@Suppress("DEPRECATION")
fun nextEntityId() = Bukkit.getUnsafe().nextEntityId()

fun Plugin.key(value: String) = NamespacedKey(this, value)

fun Plugin.resource(path: String): InputStream {
    val url = javaClass.classLoader.getResource(path)
        ?: throw RuntimeException("Resource at $path does not exist")
    try {
        val connection = url.openConnection()
        connection.useCaches = false
        return connection.getInputStream()
    } catch (ex: IOException) {
        throw RuntimeException("Could not load resource from $path", ex)
    }
}

fun Plugin.registerEvents(listener: Listener) =
    Bukkit.getPluginManager().registerEvents(listener, this)

fun <M : ItemMeta> ItemStack.withMeta(block: (M) -> Unit): ItemStack {
    editMeta { meta ->
        @Suppress("UNCHECKED_CAST")
        block(meta as M)
    }
    return this
}
