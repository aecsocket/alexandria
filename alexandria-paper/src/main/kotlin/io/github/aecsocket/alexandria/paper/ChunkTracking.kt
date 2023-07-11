package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.paper.extension.position
import io.github.aecsocket.alexandria.paper.extension.registerEvents
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.IVec2
import io.github.aecsocket.klam.xz
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent

object ChunkTracking {
  private lateinit var plugin: AlexandriaPlugin<*>
  private val chunkToPlayers: MutableMap<World, MutableMap<IVec2, MutableSet<UUID>>> = HashMap()
  private val playerToChunks: MutableMap<UUID, MutableSet<IVec2>> = HashMap()

  internal fun init(plugin: AlexandriaPlugin<*>) {
    // we only care about the first plugin to initialize us
    if (this::plugin.isInitialized) return
    this.plugin = plugin

    plugin.registerEvents(
        object : Listener {
          @EventHandler
          fun on(event: PlayerQuitEvent) {
            val player = event.player
            val chunksPos = playerToChunks.remove(player.uniqueId) ?: return
            val forWorld = chunkToPlayers[player.world] ?: return
            chunksPos.forEach { chunkPos -> forWorld[chunkPos]?.remove(player.uniqueId) }
          }

          @EventHandler
          fun on(event: WorldLoadEvent) {
            chunkToPlayers.putIfAbsent(event.world, HashMap())
          }

          @EventHandler
          fun on(event: WorldUnloadEvent) {
            chunkToPlayers.remove(event.world)
          }

          @EventHandler
          fun on(event: ChunkLoadEvent) {
            chunkToPlayers
                .computeIfAbsent(event.world) { HashMap() }
                .putIfAbsent(event.chunk.position(), HashSet())
          }

          @EventHandler
          fun on(event: ChunkUnloadEvent) {
            chunkToPlayers[event.world]?.remove(event.chunk.position())
          }

          @EventHandler
          fun on(event: PlayerChunkLoadEvent) {
            chunkToPlayers
                .computeIfAbsent(event.world) { HashMap() }
                .computeIfAbsent(event.chunk.position()) { HashSet() }
                .add(event.player.uniqueId)
            playerToChunks
                .computeIfAbsent(event.player.uniqueId) { HashSet() }
                .add(event.chunk.position())
          }

          @EventHandler
          fun on(event: PlayerChunkUnloadEvent) {
            chunkToPlayers[event.world]?.get(event.chunk.position())?.remove(event.player.uniqueId)
            playerToChunks[event.player.uniqueId]?.remove(event.chunk.position())
          }
        })
  }

  fun trackedPlayers(world: World, chunkPos: IVec2): Collection<Player> {
    return (chunkToPlayers[world]?.get(chunkPos)?.toSet() ?: emptySet()).mapNotNull {
      Bukkit.getPlayer(it)
    }
  }

  fun trackedPlayers(world: World, worldPos: DVec3) =
      trackedPlayers(world, worldPos.xz.toInt().map { it shr 4 })

  fun trackedPlayers(chunk: Chunk) = trackedPlayers(chunk.world, chunk.position())

  fun trackedChunkPos(player: Player): Collection<IVec2> {
    return playerToChunks[player.uniqueId]?.toSet() ?: emptySet()
  }

  fun trackedChunks(player: Player) =
      trackedChunkPos(player).map { (x, z) -> player.world.getChunkAt(x, z) }
}
