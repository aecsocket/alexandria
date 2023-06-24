package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.DVec3

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
