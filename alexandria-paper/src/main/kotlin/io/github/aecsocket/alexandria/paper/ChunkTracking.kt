package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.paper.extension.position
import io.github.aecsocket.alexandria.paper.extension.registerEvents
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.IVec2
import io.github.aecsocket.klam.xz
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
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

/**
 * Global bookkeeper for storing:
 * - which chunks is a specific player tracking?
 * - which players are tracked by a specific chunk?
 *
 * Tracking will happen automatically; simply call the relevant methods to access the data. The API
 * is thread-safe, returning copies of the data rather than live views.
 */
object ChunkTracking {
  private lateinit var plugin: AlexandriaPlugin<*>
  private val chunkToPlayers: MutableMap<World, MutableMap<IVec2, MutableSet<UUID>>> =
      ConcurrentHashMap()
  private val playerToChunks: MutableMap<UUID, MutableSet<IVec2>> = ConcurrentHashMap()

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
            chunkToPlayers.putIfAbsent(event.world, ConcurrentHashMap())
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

  /** Gets all players tracked by a chunk at a specified chunk position. */
  fun trackedPlayers(world: World, chunkPos: IVec2): Collection<Player> {
    return (chunkToPlayers[world]?.get(chunkPos)?.toSet() ?: emptySet()).mapNotNull {
      Bukkit.getPlayer(it)
    }
  }

  /** Gets all players tracked by a chunk at a specified world position. */
  fun trackedPlayers(world: World, worldPos: DVec3) =
      trackedPlayers(world, worldPos.xz.toInt().map { it shr 4 })

  /** Gets all players tracked by a chunk. */
  fun trackedPlayers(chunk: Chunk) = trackedPlayers(chunk.world, chunk.position())

  /** Gets all chunk positions tracked by a player. */
  fun trackedChunkPos(player: Player): Collection<IVec2> {
    return playerToChunks[player.uniqueId]?.toSet() ?: emptySet()
  }

  /** Gets all chunks tracked by a player. */
  fun trackedChunks(player: Player) =
      trackedChunkPos(player).map { (x, z) -> player.world.getChunkAt(x, z) }
}
