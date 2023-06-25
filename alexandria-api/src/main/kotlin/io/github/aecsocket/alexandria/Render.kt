package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.ARGB
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
import io.github.aecsocket.klam.FVec2
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

enum class Billboard {
    NONE,
    VERTICAL,
    HORIZONTAL,
    ALL,
}

// Represents text "aligment".
enum class TextAlignment {
    CENTER,
    LEFT,
    RIGHT,
}

// applies to 0 or more clients, not to all tracking clients
interface Render {
    fun spawn(position: DVec3): Render

    fun despawn(): Render

    fun position(value: DVec3): Render

    fun transform(value: FAffine3): Render

    fun interpolationDelay(value: Int): Render

    fun interpolationDuration(value: Int): Render

    fun billboard(value: Billboard): Render

    fun viewRange(value: Float): Render

    fun cullBox(value: FVec2): Render

    fun glowing(value: Boolean): Render

    fun glowColor(value: TextColor): Render
}

interface ItemRender : Render {
    override fun spawn(position: DVec3): ItemRender

    override fun despawn(): ItemRender

    override fun position(value: DVec3): ItemRender

    override fun transform(value: FAffine3): ItemRender

    override fun interpolationDelay(value: Int): ItemRender

    override fun interpolationDuration(value: Int): ItemRender

    override fun billboard(value: Billboard): ItemRender

    override fun viewRange(value: Float): ItemRender

    override fun cullBox(value: FVec2): ItemRender

    override fun glowing(value: Boolean): ItemRender

    override fun glowColor(value: TextColor): ItemRender
}

data class TextFlags(
    val hasShadow: Boolean,
    val isSeeThrough: Boolean,
    val defaultBackgroundColor: Boolean,
    val alignment: TextAlignment,
)

interface TextRender : Render {
    override fun spawn(position: DVec3): TextRender

    override fun despawn(): TextRender

    override fun position(value: DVec3): TextRender

    override fun transform(value: FAffine3): TextRender

    override fun interpolationDelay(value: Int): TextRender

    override fun interpolationDuration(value: Int): TextRender

    override fun billboard(value: Billboard): TextRender

    override fun viewRange(value: Float): TextRender

    override fun cullBox(value: FVec2): TextRender

    override fun glowing(value: Boolean): TextRender

    override fun glowColor(value: TextColor): TextRender

    fun text(value: Component): TextRender

    fun lineWidth(value: Int): TextRender

    fun backgroundColor(value: ARGB): TextRender

    fun textOpacity(value: Byte): TextRender

    fun textFlags(value: TextFlags): TextRender
}
