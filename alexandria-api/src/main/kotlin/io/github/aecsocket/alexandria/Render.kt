package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3

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
