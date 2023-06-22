package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.ARGB
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
import io.github.aecsocket.klam.FVec2
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.spongepowered.configurate.objectmapping.ConfigSerializable

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

@ConfigSerializable
data class RenderInterpolation(
    val delay: Int = 0,
    val duration: Int = 0,
) {
    init {
        require(delay >= 0) { "requires delay >= 0" }
        require(duration >= 0) { "requires duration >= 0" }
    }
}

// applies to 0 or more clients, not to all tracking clients
interface Render {
    fun position(value: DVec3): Render
    fun transform(value: FAffine3): Render

    fun interpolation(value: RenderInterpolation): Render
    fun billboard(value: Billboard): Render
    fun viewRange(value: Float): Render
    fun cullBox(value: FVec2): Render
    fun glowing(value: Boolean): Render
    fun glowColor(value: TextColor): Render
}

interface ItemRender : Render {
    override fun position(value: DVec3): ItemRender
    override fun transform(value: FAffine3): ItemRender
    override fun interpolation(value: RenderInterpolation): ItemRender
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
    override fun position(value: DVec3): TextRender
    override fun transform(value: FAffine3): TextRender
    override fun interpolation(value: RenderInterpolation): TextRender
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
