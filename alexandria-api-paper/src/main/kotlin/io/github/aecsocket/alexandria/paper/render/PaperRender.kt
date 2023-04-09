package io.github.aecsocket.alexandria.paper.render

import io.github.aecsocket.alexandria.Render
import io.github.aecsocket.klam.DAffine3
import io.github.aecsocket.klam.FVec3
import io.github.aecsocket.klam.RGBA
import io.github.aecsocket.klam.fromARGB
import net.kyori.adventure.text.Component
import org.bukkit.entity.Display.Billboard
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
    val scale: FVec3
    val billboard: Billboard
}

data class ModelDescriptor(
    override val scale: FVec3 = FVec3(1.0f),
    override val billboard: Billboard = Billboard.FIXED,
    val item: ItemStack,
) : RenderDescriptor

enum class TextAlignment {
    LEFT,
    RIGHT,
    CENTER,
}

data class TextDescriptor(
    override val scale: FVec3 = FVec3(1.0f),
    override val billboard: Billboard = Billboard.CENTER,
    val text: Component,
    val lineWidth: Int = 200,
    val backgroundColor: RGBA = fromARGB(0x40000000),
    val hasShadow: Boolean = false,
    val isSeeThrough: Boolean = false,
    val alignment: TextAlignment = TextAlignment.CENTER,
) : RenderDescriptor

interface Renders {
    fun createModel(descriptor: ModelDescriptor, tracker: PlayerTracker, transform: DAffine3): ModelRender

    fun createText(descriptor: TextDescriptor, tracker: PlayerTracker, transform: DAffine3): TextRender

    fun create(descriptor: RenderDescriptor, tracker: PlayerTracker, transform: DAffine3): PaperRender {
        return when (descriptor) {
            is ModelDescriptor -> createModel(descriptor, tracker, transform)
            is TextDescriptor -> createText(descriptor, tracker, transform)
        }
    }
}
