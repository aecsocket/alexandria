package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.DAffine3
import io.github.aecsocket.klam.FVec3

interface Render {
    var transform: DAffine3

    var scale: FVec3
}
