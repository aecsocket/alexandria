package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.DAffine3
import io.github.aecsocket.klam.DVec3
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.time.Duration

@ConfigSerializable
data class GizmoDraw(
    val color: TextColor = NamedTextColor.WHITE,
    val duration: Duration = Duration.ZERO,
)

interface Gizmos {
    fun line(from: DVec3, to: DVec3)

    fun path(path: Iterable<DVec3>) {
        var last: DVec3? = null
        path.forEach { point ->
            last?.let { last ->
                line(last, point)
            }
            last = point
        }
    }

    fun path(vararg path: DVec3) {
        path(path.asIterable())
    }

    fun box(halfExtent: DVec3, transform: DAffine3) {
        val min = -halfExtent
        val max = halfExtent

        return  line(DVec3(min.x, min.y, min.z), DVec3(min.x, max.y, min.z)) +
                line(DVec3(max.x, min.y, min.z), DVec3(max.x, max.y, min.z)) +
                line(DVec3(min.x, min.y, max.z), DVec3(min.x, max.y, max.z)) +
                line(DVec3(max.x, min.y, max.z), DVec3(max.x, max.y, max.z)) +

                line(DVec3(min.x, min.y, min.z), DVec3(max.x, min.y, min.z)) +
                line(DVec3(max.x, min.y, min.z), DVec3(max.x, min.y, max.z)) +
                line(DVec3(max.x, min.y, max.z), DVec3(min.x, min.y, max.z)) +
                line(DVec3(min.x, min.y, max.z), DVec3(min.x, min.y, min.z)) +

                line(DVec3(min.x, max.y, min.z), DVec3(max.x, max.y, min.z)) +
                line(DVec3(max.x, max.y, min.z), DVec3(max.x, max.y, max.z)) +
                line(DVec3(max.x, max.y, max.z), DVec3(min.x, max.y, max.z)) +
                line(DVec3(min.x, max.y, max.z), DVec3(min.x, max.y, min.z))
    }
}
