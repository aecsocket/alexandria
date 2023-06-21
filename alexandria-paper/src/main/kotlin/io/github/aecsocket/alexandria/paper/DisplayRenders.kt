package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.*
import io.github.aecsocket.alexandria.paper.extension.location
import io.github.aecsocket.alexandria.paper.extension.position
import io.github.aecsocket.alexandria.paper.extension.spawn
import io.github.aecsocket.klam.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.TextDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f

sealed interface DisplayRender : Render {
    val entity: Display
}

interface ItemDisplayRender : DisplayRender, ItemRender {
    override val entity: ItemDisplay
    var item: ItemStack
}

interface TextDisplayRender : DisplayRender, TextRender {
    override val entity: TextDisplay
}

object DisplayRenders {
    private fun FAffine3.toBukkit() = Transformation(
        translation.run { Vector3f(x, y, z) },
        rotation.run { Quaternionf(x, y, z, w) },
        scale.run { Vector3f(x, y, z) },
        Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
    )

    private fun Transformation.toAx() = FAffine3(
        translation.run { FVec3(x, y, z) },
        leftRotation.run { FQuat(x, y, z, w) },
        scale.run { FVec3(x, y, z) },
    )

    private fun Billboard.toBukkit() = when (this) {
        Billboard.NONE       -> Display.Billboard.FIXED
        Billboard.HORIZONTAL -> Display.Billboard.HORIZONTAL
        Billboard.VERTICAL   -> Display.Billboard.VERTICAL
        Billboard.ALL        -> Display.Billboard.CENTER
    }

    private fun Display.Billboard.toAx() = when (this) {
        Display.Billboard.FIXED      -> Billboard.NONE
        Display.Billboard.HORIZONTAL -> Billboard.HORIZONTAL
        Display.Billboard.VERTICAL   -> Billboard.VERTICAL
        Display.Billboard.CENTER     -> Billboard.ALL
    }

    private fun TextAlignment.toBukkit() = when (this) {
        TextAlignment.CENTER -> TextDisplay.TextAlignment.CENTER
        TextAlignment.LEFT   -> TextDisplay.TextAlignment.LEFT
        TextAlignment.RIGHT  -> TextDisplay.TextAlignment.RIGHT
    }

    private fun TextDisplay.TextAlignment.toAx() = when (this) {
        TextDisplay.TextAlignment.CENTER -> TextAlignment.CENTER
        TextDisplay.TextAlignment.LEFT   -> TextAlignment.LEFT
        TextDisplay.TextAlignment.RIGHT  -> TextAlignment.RIGHT
    }

    private fun ARGB.toBukkit() = Color.fromARGB(a, r, g, b)
    private fun TextColor.toBukkit() = Color.fromRGB(red(), green(), blue())

    private fun Color.toARGB() = ARGB(alpha, red, green, blue)
    private fun Color.toTextColor() = TextColor.color(red, green, blue)

    fun createItem(
        world: World,
        position: DVec3,
    ): ItemDisplayRender {
        return OfItem(world.spawn<ItemDisplay>(position))
    }

    fun createText(
        world: World,
        position: DVec3,
    ): TextDisplayRender {
        return OfText(world.spawn<TextDisplay>(position))
    }

    private open class ByDisplay(
        override val entity: Display,
    ) : DisplayRender {
        override var position: DVec3
            get() = entity.location.position()
            set(value) {
                entity.teleportAsync(value.location(entity.world))
            }

        override var transform: FAffine3
            get() = entity.transformation.toAx()
            set(value) {
                entity.transformation = value.toBukkit()
            }

        override var billboard: Billboard
            get() = entity.billboard.toAx()
            set(value) {
                entity.billboard = value.toBukkit()
            }

        override var viewRange: Float
            get() = entity.viewRange
            set(value) {
                require(viewRange >= 0.0) { "requires viewRange >= 0.0" }
                entity.viewRange = value
            }

        override var interpolationDelay: Int
            get() = entity.interpolationDelay
            set(value) {
                require(interpolationDelay >= 0) { "requires interpolationDelay >= 0" }
                entity.interpolationDelay = value
            }

        override var interpolationDuration: Int
            get() = entity.interpolationDuration
            set(value) {
                require(interpolationDuration >= 0) { "requires interpolationDuration >= 0" }
                entity.interpolationDuration = value
            }

        override var glowing: Boolean
            get() = entity.isGlowing
            set(value) {
                entity.isGlowing = value
            }

        override var glowColor: TextColor
            get() = entity.glowColorOverride?.toTextColor() ?: NamedTextColor.WHITE
            set(value) {
                entity.glowColorOverride = value.toBukkit()
            }

        override fun remove() {
            entity.remove()
        }
    }

    private class OfItem(override val entity: ItemDisplay) : ByDisplay(entity), ItemDisplayRender {
        override var item: ItemStack
            get() = entity.itemStack ?: ItemStack(Material.AIR)
            set(value) {
                entity.itemStack = value
            }
    }

    private class OfText(override val entity: TextDisplay) : ByDisplay(entity), TextDisplayRender {
        override var text: Component
            get() = entity.text()
            set(value) {
                entity.text(value)
            }

        override var lineWidth: Int
            get() = entity.lineWidth
            set(value) {
                require(lineWidth > 0) { "requires lineWidth > 0" }
                entity.lineWidth = value
            }

        // Spigot is stupid and deprecated this for no reason
        @Suppress("DEPRECATION")
        override var backgroundColor: ARGB
            get() = entity.backgroundColor?.toARGB() ?: ARGB(0)
            set(value) {
                entity.backgroundColor = value.toBukkit()
            }

        override var hasShadow: Boolean
            get() = entity.isShadowed
            set(value) {
                entity.isShadowed = value
            }

        override var isSeeThrough: Boolean
            get() = entity.isSeeThrough
            set(value) {
                entity.isSeeThrough = value
            }

        override var alignment: TextAlignment
            get() = entity.alignment.toAx()
            set(value) {
                entity.alignment = value.toBukkit()
            }
    }
}
