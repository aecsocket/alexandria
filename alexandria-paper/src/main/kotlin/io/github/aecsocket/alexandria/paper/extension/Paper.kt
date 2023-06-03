package io.github.aecsocket.alexandria.paper.extension

import io.github.aecsocket.klam.DVec3
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
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

fun <V> Map<String, V>.forWorld(world: World) = get(world.name) ?: get("default")

inline fun <reified E : Entity> World.spawn(
    location: Location,
    reason: SpawnReason = SpawnReason.CUSTOM,
    crossinline beforeSpawn: (E) -> Unit = {},
) = spawn(location, E::class.java, reason) { beforeSpawn(it) }

inline fun <reified E : Entity> World.spawn(
    position: DVec3,
    reason: SpawnReason = SpawnReason.CUSTOM,
    crossinline beforeSpawn: (E) -> Unit = {},
) = spawn(position.location(this), reason, beforeSpawn)
