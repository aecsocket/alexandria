package io.github.aecsocket.alexandria.paper.extension

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import io.github.aecsocket.klam.DVec3
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

/** Gets if the server is currently running on a Folia or a Paper build. */
val isFolia =
    try {
      Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
      true
    } catch (ex: ClassNotFoundException) {
      false
    }

/** Increments and gets the internal entity ID counter. */
@Suppress("DEPRECATION") fun nextEntityId() = Bukkit.getUnsafe().nextEntityId()

fun Plugin.key(value: String) = NamespacedKey(this, value)

fun Plugin.registerEvents(listener: Listener) =
    Bukkit.getPluginManager().registerEvents(listener, this)

fun <M : ItemMeta> ItemStack.withMeta(block: (M) -> Unit): ItemStack {
  editMeta { meta -> @Suppress("UNCHECKED_CAST") block(meta as M) }
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

fun World.spawnTracker(position: DVec3, beforeSpawn: (Entity) -> Unit = {}): Entity {
  // Markers are not tracked by clients; use a different entity
  return spawn<ItemDisplay>(position) { entity ->
    entity.isPersistent = false
    beforeSpawn(entity)
  }
}

/** Sends a packet to a player using the global [PacketEvents.getAPI] instance. */
fun Player.sendPacket(packet: PacketWrapper<*>) {
  PacketEvents.getAPI().playerManager.sendPacket(this, packet)
}
