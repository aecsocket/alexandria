package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.physics.Transform
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import com.gitlab.aecsocket.alexandria.paper.extension.rotation
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

private val NAME_OFFSET = Vector3(0.0, -0.4, 0.0)

class CallbackEntry internal constructor(
    name: Component,
    val function: () -> Unit,
    internal val label: Mesh
) {
    var name: Component = name
        set(value) {
            field = value
        }
}

class ContextCallback internal constructor(
    private val alexandria: Alexandria,
    private val player: Player,
    position: Vector3,
    internal val prompt: Mesh
) {
    var position: Vector3 = position
        set(value) {
            field = value
            prompt.transform = byNameOffset(position)
        }

    private val _topEntries = ArrayList<CallbackEntry>()
    val topEntries: List<CallbackEntry> get() = _topEntries

    private val _bottomEntries = ArrayList<CallbackEntry>()
    val bottomEntries: List<CallbackEntry> get() = _bottomEntries

    fun topEntry(index: Int) = _topEntries[index]

    fun addTopEntry(name: Component, function: () -> Unit): CallbackEntry {
        val offset = topEntries.size + 1
        val label = alexandria.meshes.create(
            AIR,
            Transform(position, player.location.rotation()) + Transform(Vector3(0.0, offset * 0.5, 0.0)),
            { setOf(player) },
            false
        )
        return CallbackEntry(name, function, label).also {
            _topEntries.add(it)
        }
    }

    fun removeTopEntry(entry: CallbackEntry) {
        if (!_topEntries.remove(entry)) return
        alexandria.meshes.remove(entry.label)
    }

    fun removeAll() {
        listOf(_topEntries, _bottomEntries).forEach { entries ->
            entries.forEach {
                alexandria.meshes.remove(it.label)
            }
            entries.clear()
        }
    }
}

private fun byNameOffset(position: Vector3) = Transform(position + NAME_OFFSET)

private val AIR = ItemStack(Material.AIR)

class ContextCallbacks internal constructor(
    private val alexandria: Alexandria
) : PlayerFeature<ContextCallbacks.PlayerData> {
    inner class PlayerData(val player: AlexandriaPlayer) : PlayerFeature.PlayerData {
        fun create(position: Vector3): ContextCallback {
            val mesh = alexandria.meshes.create(
                AIR,
                byNameOffset(position),
                { setOf(player.handle) },
                false
            )
            return ContextCallback(alexandria, player.handle, position, mesh)
        }

        fun remove(callback: ContextCallback) {
            callback.removeAll()
            alexandria.meshes.remove(callback.prompt)
        }
    }

    override fun createFor(player: AlexandriaPlayer) = PlayerData(player)

    internal fun load() {
        /*alexandria.registerEvents(object : Listener {
            @EventHandler
            fun on(event: PlayerSwapHandItemsEvent) {
                val player = event.player
                player.alexandria.featureData(this@ContextCallbacks).create(Vector3())
            }
        })*/
    }
}
