package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.*

data class Segment(
    val from: DVec3,
    val to: DVec3,
) {
    operator fun plus(v: DVec3) = Segment(
        from = from + v,
        to = to + v,
    )

    operator fun minus(v: DVec3) = Segment(
        from = from - v,
        to = to - v,
    )
}

object Shaping {
    fun lineTransform(delta: FVec3, width: Float): FAffine3 {
        return FAffine3(
            translation = delta / 2.0f,
            rotation = FQuat.fromTo(FVec3(0.0f, 1.0f, 0.0f), normalize(delta)),
            scale = FVec3(width, length(delta), width),
        )
    }

    fun box(halfExtent: DVec3): List<Segment> {
        val min = -halfExtent
        val max = halfExtent

        return listOf(
            Segment(DVec3(min.x, min.y, min.z), DVec3(max.x, min.y, min.z)),
            Segment(DVec3(max.x, min.y, min.z), DVec3(max.x, min.y, max.z)),
            Segment(DVec3(max.x, min.y, max.z), DVec3(min.x, min.y, max.z)),
            Segment(DVec3(min.x, min.y, max.z), DVec3(min.x, min.y, min.z)),

            Segment(DVec3(min.x, max.y, min.z), DVec3(max.x, max.y, min.z)),
            Segment(DVec3(max.x, max.y, min.z), DVec3(max.x, max.y, max.z)),
            Segment(DVec3(max.x, max.y, max.z), DVec3(min.x, max.y, max.z)),
            Segment(DVec3(min.x, max.y, max.z), DVec3(min.x, max.y, min.z)),

            Segment(DVec3(min.x, min.y, min.z), DVec3(min.x, max.y, min.z)),
            Segment(DVec3(max.x, min.y, min.z), DVec3(max.x, max.y, min.z)),
            Segment(DVec3(min.x, min.y, max.z), DVec3(min.x, max.y, max.z)),
            Segment(DVec3(max.x, min.y, max.z), DVec3(max.x, max.y, max.z)),
        )
    }
}
