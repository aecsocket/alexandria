package com.github.aecsocket.alexandria.core.physics

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required
import kotlin.math.sqrt

object Empty : Shape {
    override fun collides(ray: Ray) = null
}

@ConfigSerializable
data class Sphere(
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
data class Box(
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
        val Half = Box(Vector3(0.5))
        val One = Box(Vector3.One)
    }
}
