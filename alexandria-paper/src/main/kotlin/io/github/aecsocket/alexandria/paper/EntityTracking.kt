package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.EventDispatch
import io.github.aecsocket.alexandria.paper.extension.isFolia
import io.github.aecsocket.alexandria.paper.extension.registerEvents
import io.papermc.paper.event.player.PlayerTrackEntityEvent
import io.papermc.paper.event.player.PlayerUntrackEntityEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.lang.IllegalStateException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object EntityTracking {
    private class Tracked {
        var players: Collection<UUID> = emptySet()
        val onTrack = EventDispatch<Player>()
        val onUntrack = EventDispatch<Player>()

        val removed = AtomicBoolean(false)
    }

    private lateinit var plugin: AlexandriaPlugin<*>
    private val entities: MutableMap<UUID, Tracked> = ConcurrentHashMap()

    internal fun init(plugin: AlexandriaPlugin<*>) {
        // we only care about the first plugin to initialize us
        if (this::plugin.isInitialized) return
        this.plugin = plugin

        // Folia doesn't call `Player(Un)TrackEntityEvent` yet
        if (!isFolia) {
            plugin.registerEvents(object : Listener {
                @EventHandler
                fun on(event: PlayerTrackEntityEvent) {
                    entities[event.entity.uniqueId]?.onTrack?.dispatch(event.player)
                }

                @EventHandler
                fun on(event: PlayerUntrackEntityEvent) {
                    entities[event.entity.uniqueId]?.onUntrack?.dispatch(event.player)
                }
            })
        }
    }

    fun register(entity: Entity) {
        if (entities.contains(entity.uniqueId)) return
        val tracked = Tracked()
        entities[entity.uniqueId] = tracked

        if (isFolia) {
            plugin.scheduling.onEntity(entity, onRetire = {
                tracked.players.forEach { playerId ->
                    val player = Bukkit.getPlayer(playerId) ?: return@forEach
                    plugin.scheduling.onEntity(player).runLater {
                        tracked.onUntrack.dispatch(player)
                    }
                }
            }).runRepeating { task ->
                if (tracked.removed.get()) {
                    task.cancel()
                    return@runRepeating
                }
                val oldTracked = tracked.players
                val newTracked = entity.trackedPlayers.map { it.uniqueId }
                tracked.players = newTracked

                newTracked.forEach { newPlayer ->
                    if (!oldTracked.contains(newPlayer)) {
                        Bukkit.getPlayer(newPlayer)?.let { tracked.onTrack.dispatch(it) }
                    }
                }
                oldTracked.forEach { oldPlayer ->
                    if (!newTracked.contains(oldPlayer)) {
                        Bukkit.getPlayer(oldPlayer)?.let { tracked.onUntrack.dispatch(it) }
                    }
                }
            }
        } else {
            plugin.scheduling.onEntity(entity).runRepeating { task ->
                if (tracked.removed.get()) {
                    task.cancel()
                    return@runRepeating
                }
                val newTracked = entity.trackedPlayers.map { it.uniqueId }
                tracked.players = newTracked
            }
        }
    }

    fun unregister(entityId: UUID) {
        val tracked = entities.remove(entityId) ?: return
        tracked.removed.set(true)
    }

    fun unregister(entity: Entity) = unregister(entity.uniqueId)

    private fun get(entityId: UUID) = entities[entityId]
        ?: throw IllegalStateException("Entity $entityId is not registered")

    fun trackedPlayers(entityId: UUID): Collection<Player> {
        // we don't clone `players` because we fully replace the players field when it updates,
        // so this object will never be mutated by us
        return get(entityId).players.mapNotNull { Bukkit.getPlayer(it) }
    }

    fun trackedPlayers(entity: Entity) = trackedPlayers(entity.uniqueId)

    fun onTrack(entity: Entity): EventDispatch<Player> {
        return get(entity.uniqueId).onTrack
    }

    fun onUntrack(entity: Entity): EventDispatch<Player> {
        return get(entity.uniqueId).onUntrack
    }
}
