package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.ARGB
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
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

interface Render {
    var position: DVec3
    var transform: FAffine3
}

sealed interface DisplayRenderDesc {
    val billboard: Billboard
    val viewRange: Float
    val interpolationDelay: Int
    val interpolationDuration: Int
}

@ConfigSerializable
data class ItemRenderDesc(
    override val billboard: Billboard = Billboard.NONE,
    override val viewRange: Float = 1.0f,
    override val interpolationDelay: Int = 0,
    override val interpolationDuration: Int = 0,
) : DisplayRenderDesc {
    init {
        require(viewRange >= 0.0) { "requires viewRange >= 0.0" }
        require(interpolationDelay >= 0) { "requires interpolationDelay >= 0" }
        require(interpolationDuration >= 0) { "requires interpolationDuration >= 0" }
    }
}

@ConfigSerializable
data class TextRenderDesc(
    override val billboard: Billboard = Billboard.ALL,
    override val viewRange: Float = 1.0f,
    override val interpolationDelay: Int = 0,
    override val interpolationDuration: Int = 0,
    val lineWidth: Int = 200,
    val backgroundColor: ARGB = ARGB(64, 0, 0, 0),
    val hasShadow: Boolean = false,
    val isSeeThrough: Boolean = false,
    val alignment: TextAlignment = TextAlignment.CENTER,
) : DisplayRenderDesc {
    init {
        require(viewRange >= 0.0) { "requires viewRange >= 0.0" }
        require(interpolationDelay >= 0) { "requires interpolationDelay >= 0" }
        require(interpolationDuration >= 0) { "requires interpolationDuration >= 0" }
        require(lineWidth > 0) { "requires lineWidth > 0" }
    }
}
