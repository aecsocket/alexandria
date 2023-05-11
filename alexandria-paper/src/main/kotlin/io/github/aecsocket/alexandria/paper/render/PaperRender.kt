package io.github.aecsocket.alexandria.paper.render

import io.github.aecsocket.alexandria.Billboard
import io.github.aecsocket.alexandria.Render
import io.github.aecsocket.alexandria.TextAlignment
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

    fun trackedPlayers() = tracker.trackedPlayers()

    fun spawn(players: Iterable<Player>)

    fun spawn(player: Player) = spawn(setOf(player))

    fun spawn() = spawn(trackedPlayers())

    fun despawn(players: Iterable<Player>)

    fun despawn(player: Player) = despawn(setOf(player))

    fun despawn() = despawn(trackedPlayers())
}


interface ModelRender : PaperRender {
    var item: ItemStack
}

interface TextRender : PaperRender {
    var text: Component
}

sealed interface RenderDescriptor {
    val tracker: PlayerTracker
    val billboard: Billboard
    val viewRange: Float
    val interpolationDelay: Int
    val interpolationDuration: Int
}

data class ModelDescriptor(
    val item: ItemStack,
    override val tracker: PlayerTracker,
    override val billboard: Billboard = Billboard.NONE,
    override val viewRange: Float = 1.0f,
    override val interpolationDelay: Int = 0,
    override val interpolationDuration: Int = 0,
) : RenderDescriptor

data class TextDescriptor(
    val text: Component,
    override val tracker: PlayerTracker,
    override val billboard: Billboard = Billboard.ALL,
    override val viewRange: Float = 1.0f,
    override val interpolationDelay: Int = 0,
    override val interpolationDuration: Int = 0,
    val lineWidth: Int = 200,
    val backgroundColor: IVec4 = fromARGB(0x40000000),
    val hasShadow: Boolean = false,
    val isSeeThrough: Boolean = false,
    val alignment: TextAlignment = TextAlignment.CENTER,
) : RenderDescriptor

interface Renders {
    fun createModel(descriptor: ModelDescriptor, basePosition: DVec3, transform: FAffine3): ModelRender

    fun createText(descriptor: TextDescriptor, basePosition: DVec3, transform: FAffine3): TextRender

    fun create(descriptor: RenderDescriptor, basePosition: DVec3, transform: FAffine3): PaperRender {
        return when (descriptor) {
            is ModelDescriptor -> createModel(descriptor, basePosition, transform)
            is TextDescriptor -> createText(descriptor, basePosition, transform)
        }
    }
}
