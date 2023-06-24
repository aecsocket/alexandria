package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.paper.extension.position
import io.github.aecsocket.alexandria.paper.extension.registerEvents
import io.github.aecsocket.klam.IVec2
import io.papermc.paper.event.packet.PlayerChunkLoadEvent
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.plugin.Plugin

object ChunkTracking {
    private lateinit var plugin: Plugin
    private val chunkToPlayers: MutableMap<World, MutableMap<IVec2, MutableSet<Player>>> = HashMap()

    internal fun init(plugin: Plugin) {
        // we only care about the first plugin to initialize us
        if (this::plugin.isInitialized) return
        this.plugin = plugin
        plugin.registerEvents(object : Listener {
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
                    .add(event.player)
            }

            @EventHandler
            fun on(event: PlayerChunkUnloadEvent) {
                chunkToPlayers[event.world]?.get(event.chunk.position())?.remove(event.player)
            }
        })
    }

    fun trackedPlayers(world: World, chunkPos: IVec2): Set<Player> {
        return chunkToPlayers[world]?.get(chunkPos) ?: emptySet()
    }

    fun trackedPlayers(chunk: Chunk) = trackedPlayers(chunk.world, chunk.position())
}
