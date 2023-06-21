package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.ARGB
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
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

interface Render {
    var position: DVec3
    var transform: FAffine3

    var billboard: Billboard
    var viewRange: Float
    var interpolationDelay: Int
    var interpolationDuration: Int
    var glowing: Boolean
    var glowColor: TextColor

    fun remove()
}

interface ItemRender : Render

interface TextRender : Render {
    var text: Component

    val lineWidth: Int
    val backgroundColor: ARGB
    val hasShadow: Boolean
    val isSeeThrough: Boolean
    val alignment: TextAlignment
}

//@ConfigSerializable
//data class ItemRenderDesc(
//    override val billboard: Billboard = Billboard.NONE,
//    override val viewRange: Float = 1.0f,
//    override val interpolationDelay: Int = 0,
//    override val interpolationDuration: Int = 0,
//    override val glowColor: TextColor? = null,
//) : DisplayRenderDesc {
//    init {
//        require(viewRange >= 0.0) { "requires viewRange >= 0.0" }
//        require(interpolationDelay >= 0) { "requires interpolationDelay >= 0" }
//        require(interpolationDuration >= 0) { "requires interpolationDuration >= 0" }
//    }
//}
//
//@ConfigSerializable
//data class TextRenderDesc(
//    override val billboard: Billboard = Billboard.ALL,
//    override val viewRange: Float = 1.0f,
//    override val interpolationDelay: Int = 0,
//    override val interpolationDuration: Int = 0,
//    override val glowColor: TextColor? = null,
//) : DisplayRenderDesc {
//    init {
//        require(viewRange >= 0.0) { "requires viewRange >= 0.0" }
//        require(interpolationDelay >= 0) { "requires interpolationDelay >= 0" }
//        require(interpolationDuration >= 0) { "requires interpolationDuration >= 0" }
//        require(lineWidth > 0) { "requires lineWidth > 0" }
//    }
//}
