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
import org.bukkit.entity.TextDisplay.TextAligment
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import org.joml.Vector3f

sealed interface DisplayRender : Render {
    val entity: Display

    var billboard: Billboard
    var viewRange: Float
    var interpolationDelay: Int
    var interpolationDuration: Int
    var glowColor: TextColor?

    fun remove()
}

interface ItemRender : DisplayRender {
    override val entity: ItemDisplay

    var item: ItemStack
}

interface TextRender : DisplayRender {
    override val entity: TextDisplay

    var text: Component
    var lineWidth: Int
    var backgroundColor: ARGB
    var hasShadow: Boolean
    var isSeeThrough: Boolean
    var alignment: TextAlignment
}

object DisplayRenders {
    private fun FAffine3.convert() = Transformation(
        translation.run { Vector3f(x, y, z) },
        rotation.run { Quaternionf(x, y, z, w) },
        scale.run { Vector3f(x, y, z) },
        Quaternionf(0.0f, 0.0f, 0.0f, 1.0f),
    )

    private fun Transformation.convert() = FAffine3(
        translation.run { FVec3(x, y, z) },
        leftRotation.run { FQuat(x, y, z, w) },
        scale.run { FVec3(x, y, z) },
    )

    private fun Billboard.convert() = when (this) {
        Billboard.NONE       -> Display.Billboard.FIXED
        Billboard.HORIZONTAL -> Display.Billboard.HORIZONTAL
        Billboard.VERTICAL   -> Display.Billboard.VERTICAL
        Billboard.ALL        -> Display.Billboard.CENTER
    }

    private fun Display.Billboard.convert() = when (this) {
        Display.Billboard.FIXED      -> Billboard.NONE
        Display.Billboard.HORIZONTAL -> Billboard.HORIZONTAL
        Display.Billboard.VERTICAL   -> Billboard.VERTICAL
        Display.Billboard.CENTER     -> Billboard.ALL
    }

    // "TextAligment"? Seriously Spigot?
    private fun TextAlignment.convert() = when (this) {
        TextAlignment.CENTER -> TextAligment.CENTER
        TextAlignment.LEFT   -> TextAligment.LEFT
        TextAlignment.RIGHT  -> TextAligment.RIGHT
    }

    private fun TextAligment.convert() = when (this) {
        TextAligment.CENTER -> TextAlignment.CENTER
        TextAligment.LEFT   -> TextAlignment.LEFT
        TextAligment.RIGHT  -> TextAlignment.RIGHT
    }

    private fun ARGB.convert() = Color.fromARGB(a, r, g, b)
    private fun TextColor.convert() = Color.fromRGB(red(), green(), blue())

    private fun Color.toARGB() = ARGB(alpha, red, green, blue)
    private fun Color.toTextColor() = TextColor.color(red, green, blue)

    private fun setUp(desc: DisplayRenderDesc, target: ByDisplay) {
        target.entity.isPersistent = false
        target.billboard = desc.billboard
        target.viewRange = desc.viewRange
        target.interpolationDelay = desc.interpolationDelay
        target.interpolationDuration = desc.interpolationDuration
    }

    fun createItem(
        world: World,
        position: DVec3,
        transform: FAffine3,
        item: ItemStack,
        desc: ItemRenderDesc,
    ): ItemRender {
        return OfItem(world.spawn<ItemDisplay>(position)).also {
            it.transform = transform
            it.item = item
            setUp(desc, it)
        }
    }

    fun createText(
        world: World,
        position: DVec3,
        transform: FAffine3,
        text: Component,
        desc: TextRenderDesc,
    ): TextRender {
        return OfText(world.spawn<TextDisplay>(position)).also {
            it.transform = transform
            it.text = text
            setUp(desc, it)
            it.lineWidth = desc.lineWidth
            it.backgroundColor = desc.backgroundColor
            it.hasShadow = desc.hasShadow
            it.isSeeThrough = desc.isSeeThrough
            it.alignment = desc.alignment
        }
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
            get() = entity.transformation.convert()
            set(value) {
                entity.transformation = value.convert()
            }

        override var billboard: Billboard
            get() = entity.billboard.convert()
            set(value) {
                entity.billboard = value.convert()
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

        // Spigot is stupid and deprecated this for no reason
        @Suppress("DEPRECATION")
        override var glowColor: TextColor?
            get() = entity.glowColorOverride?.toTextColor()
            set(value) {
                entity.glowColorOverride = value?.convert()
            }

        override fun remove() {
            entity.remove()
        }
    }

    private class OfItem(override val entity: ItemDisplay) : ByDisplay(entity), ItemRender {
        override var item: ItemStack
            get() = entity.itemStack ?: ItemStack(Material.AIR)
            set(value) {
                entity.itemStack = value
            }
    }

    private class OfText(override val entity: TextDisplay) : ByDisplay(entity), TextRender {
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
                entity.backgroundColor = value.convert()
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
            get() = entity.alignment.convert()
            set(value) {
                entity.alignment = value.convert()
            }
    }
}
