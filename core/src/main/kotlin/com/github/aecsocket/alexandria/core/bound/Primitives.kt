package com.github.aecsocket.alexandria.core.bound

import com.github.aecsocket.alexandria.core.spatial.Quaternion
import com.github.aecsocket.alexandria.core.spatial.Vector3
import com.github.aecsocket.alexandria.core.spatial.step
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required
import kotlin.math.sqrt

object Empty : Bound {
    override fun translated(vector: Vector3) = this

    override fun rotated(rotation: Quaternion) = this

    override fun collides(ray: Ray) = null
}

@ConfigSerializable
data class Sphere(
    @Required val center: Vector3,
    @Required val radius: Double
) : Bound {
    val sqrRadius = radius * radius

    override fun translated(vector: Vector3) = Sphere(center + vector, radius)

    override fun collides(ray: Ray): Collision? {
        val m = ray.pos - center
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
        return Collision(tIn, tOut, (ray.point(tIn) - center).normalized)
    }

    override fun rotated(rotation: Quaternion) = this
}

@ConfigSerializable
data class Box(
    @Required val min: Vector3,
    @Required val max: Vector3,
    val origin: Vector3 = Vector3.Zero,
    val rotation: Quaternion = Quaternion.Identity
) : Bound {
    val extent = max - min
    val center = min.midpoint(max)

    private val halfExtent = extent / 2.0
    private val invRotation = rotation.inverse

    override fun translated(vector: Vector3) = Box(min + vector, max + vector, origin + vector, rotation)

    override fun rotated(rotation: Quaternion) = Box(min, max, origin, rotation)

    // https://iquilezles.org/articles/boxfunctions/
    // https://www.shadertoy.com/view/ld23DV
    override fun collides(ray: Ray): Collision? {
        val center = center
        val (ro, rd, m) = if (rotation == Quaternion.Identity) {
            Triple(ray.pos - center, ray.dir, ray.invDir)
        } else {
            val pos = (invRotation * (ray.pos - origin)) + (origin - center)
            val dir = invRotation * ray.dir
            Triple(pos, dir, dir.inv)
        }

        // ray-box intersection in box space
        val s = rd.map { if (it < 0.0) 1.0 else -1.0 }
        val t1 = m * (-ro + s * halfExtent)
        val t2 = m * (-ro - s * halfExtent)

        val tN = t1.max
        val tF = t2.min

        if (tN > tF || tF < 0.0) return null

        val oN = rotation * (-rd.sign * step(Vector3(t1.y, t1.z, t1.x), t1) * step(Vector3(t1.z, t1.x, t1.y), t1))

        return Collision(tN, tF, oN)
    }

    companion object {
        val Unit = Box(Vector3.Zero, Vector3.One, Vector3.Zero)
        val CenteredOne = Box(Vector3(-0.5), Vector3(0.5))
    }
}

data class Compound(
    val bounds: List<Bound>
) : Bound {
    override fun translated(vector: Vector3) = Compound(bounds.map { it.translated(vector) })

    override fun rotated(rotation: Quaternion) = Compound(bounds.map { it.rotated(rotation) })

    override fun collides(ray: Ray): Collision? {
        return if (bounds.isEmpty()) null
        else bounds.mapNotNull { it.collides(ray) }.closest()
    }
}
