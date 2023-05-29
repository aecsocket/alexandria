package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
import io.github.aecsocket.klam.IVec4
import io.github.aecsocket.klam.fromARGB

interface Render {
    var basePosition: DVec3

    var transform: FAffine3
}

enum class Billboard {
    NONE,
    VERTICAL,
    HORIZONTAL,
    ALL,
}

enum class TextAlignment {
    LEFT,
    RIGHT,
    CENTER,
}

sealed interface RenderDescriptor {
    val billboard: Billboard
    val viewRange: Float
    val interpolationDelay: Int
    val interpolationDuration: Int
}

data class ModelDescriptor(
    override val billboard: Billboard = Billboard.NONE,
    override val viewRange: Float = 1.0f,
    override val interpolationDelay: Int = 0,
    override val interpolationDuration: Int = 0,
) : RenderDescriptor

data class TextDescriptor(
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
