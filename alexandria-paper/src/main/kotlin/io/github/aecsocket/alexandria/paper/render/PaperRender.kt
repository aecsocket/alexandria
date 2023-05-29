package io.github.aecsocket.alexandria.paper.render

import io.github.aecsocket.alexandria.ModelDescriptor
import io.github.aecsocket.alexandria.Render
import io.github.aecsocket.alexandria.TextDescriptor
import io.github.aecsocket.klam.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun interface PlayerTracker {
    fun trackedPlayers(): Collection<Player>
}

fun Entity.playerTracker() = PlayerTracker { trackedPlayers }

sealed interface PaperRender : Render {
    var tracker: PlayerTracker

    fun spawn(players: Iterable<Player>)

    fun despawn(players: Iterable<Player>)


    fun trackedPlayers() = tracker.trackedPlayers()

    fun spawn(player: Player) = spawn(setOf(player))

    fun spawn() = spawn(trackedPlayers())

    fun despawn(player: Player) = despawn(setOf(player))

    fun despawn() = despawn(trackedPlayers())
}

interface ModelRender : PaperRender {
    var item: ItemStack
}

interface TextRender : PaperRender {
    var text: Component
}

interface Renders {
    fun createModel(
        descriptor: ModelDescriptor,
        item: ItemStack,
        tracker: PlayerTracker,
        basePosition: DVec3,
        transform: FAffine3,
    ): ModelRender

    fun createText(
        descriptor: TextDescriptor,
        text: Component,
        tracker: PlayerTracker,
        basePosition: DVec3,
        transform: FAffine3,
    ): TextRender
}
