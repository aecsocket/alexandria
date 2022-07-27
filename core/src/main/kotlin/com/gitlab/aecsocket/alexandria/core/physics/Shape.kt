package com.gitlab.aecsocket.alexandria.core.physics

import com.gitlab.aecsocket.alexandria.core.extension.EPSILON
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required
import kotlin.math.abs
import kotlin.math.sqrt

data class Ray(
    val pos: Vector3,
    val dir: Vector3,
    val invDir: Vector3 = dir.inv
) {
    fun point(t: Double) = pos + dir * t
}

data class Collision(
    val tIn: Double,
    val tOut: Double,
    val normal: Vector3
)

sealed interface Shape {
    fun collides(ray: Ray): Collision?
}

object EmptyShape : Shape {
    override fun collides(ray: Ray) = null
}

@ConfigSerializable
data class SphereShape(
    @Required val radius: Double
) : Shape {
    private val sqrRadius = radius * radius

    override fun collides(ray: Ray): Collision? {
        val m = ray.pos
        val b = m.dot(ray.dir)
        val c = m.dot(m) - sqrRadius

        if (c > 0 && b > 0)
            return null
        val sqrDiscrim = b*b - c
        if (sqrDiscrim < 0)
            return null
        val discrim = sqrt(sqrDiscrim)

        val tIn = -b - discrim
        val tOut = -b + discrim
        return Collision(tIn, tOut, ray.point(tIn).normalized)
    }
}

@ConfigSerializable
data class BoxShape(
    @Required val halfExtent: Vector3
) : Shape {
    // https://iquilezles.org/articles/boxfunctions/
    // https://www.shadertoy.com/view/ld23DV
    override fun collides(ray: Ray): Collision? {
        val m = ray.invDir
        val n = m * ray.pos
        val k = m.abs * halfExtent
        val t1 = -n - k
        val t2 = -n + k

        val tN = t1.max
        val tF = t2.min

        if (tN > tF || tF < 0.0) return null

        val oN = -ray.dir.sign * step(Vector3(t1.y, t1.z, t1.x), t1) * step(Vector3(t1.z, t1.x, t1.y), t1)

        return Collision(tN, tF, oN)
    }

    companion object {
        val Half = BoxShape(Vector3(0.5))
        val One = BoxShape(Vector3.One)
    }
}

@ConfigSerializable
data class PlaneShape(
    @Required val normal: Vector3
) : Shape {
    // https://stackoverflow.com/questions/23975555/how-to-do-ray-plane-intersection
    override fun collides(ray: Ray): Collision? {
        val denom = normal.dot(ray.dir)
        if (abs(denom) > EPSILON) {
            val t = (-ray.pos).dot(normal) / denom
            return if (t >= EPSILON) Collision(t, t + EPSILON, normal) else null
        }
        return null
    }
}
