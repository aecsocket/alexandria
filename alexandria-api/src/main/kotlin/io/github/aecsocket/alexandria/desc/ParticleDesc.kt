package io.github.aecsocket.alexandria.desc

import io.github.aecsocket.klam.*
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

interface RawParticle

@ConfigSerializable
data class ParticleDesc(
    @Required val type: RawParticle,
    val count: Int = 0,
    val size: DVec3 = DVec3(0.0),
    val speed: Double = 0.0,
)

@ConfigSerializable
data class ParticleShaping(
    val lineStep: Double = 1.0,
    val lineMaxSteps: Int = 40,
) {
    fun line(from: DVec3, to: DVec3): List<DVec3> {
        val distance = distance(from, to)
        val numSteps = clamp((distance / lineStep).toInt(), 0, lineMaxSteps)
        val stepLen = distance / numSteps.toDouble()
        val stepDir = normalize(to - from) * stepLen
        return (0 until numSteps).map { step ->
            val delta = stepDir * step.toDouble()
            from + delta
        }
    }

    fun box(halfExtent: DVec3): List<DVec3> {
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

    fun aabb(aabb: DAabb3): List<DVec3> {
        val halfExtent = (aabb.max - aabb.min) / 2.0
        val center = aabb.min + halfExtent
        return box(halfExtent).map { it + center }
    }
}
