package com.github.aecsocket.alexandria.core.bound

import com.github.aecsocket.alexandria.core.vector.Vector3
import kotlin.math.sqrt

object Empty : Bound {
    override fun translated(vector: Vector3) = Empty

    override fun intersects(ray: Ray) = null
}

data class Sphere(
    val center: Vector3,
    val radius: Double
) : Bound {
    val sqrRadius by lazy { radius * radius }

    override fun translated(vector: Vector3) = Sphere(center + vector, radius)

    override fun intersects(ray: Ray): Bound.Intersection? {
        val m = ray.origin - center
        val b = m.dot(ray.direction)
        val c = m.dot(m) - sqrRadius

        if (c > 0 && b > 0)
            return null
        val sqrDiscrim = b*b - c
        if (sqrDiscrim < 0)
            return null
        val discrim = sqrt(sqrDiscrim)

        val tIn = -b - discrim
        val tOut = -b + discrim
        return Bound.Intersection(tIn, tOut, (ray.point(tIn) - center).normalized)
    }
}

data class Box(
    val min: Vector3,
    val max: Vector3,
    override val angle: Double = 0.0
) : Bound.Oriented {
    val extent by lazy { max - min }

    val center by lazy { min.midpoint(max) }

    override fun translated(vector: Vector3) = Box(min + vector, max + vector, angle)

    override fun oriented(angle: Double) = Box(min, max, angle)

    override fun intersects(ray: Ray): Bound.Intersection? {
        val offset = -center

        val (orig, dir) = if (angle.compareTo(0) == 0) {
            ray.origin + offset to ray.direction
        } else {
            (ray.origin - center).rotateY(-angle) + center + offset to
                ray.direction.rotateY(-angle)
        }
        val invDir = dir.reciprocal

        val n = invDir * orig
        val k = invDir.abs * (extent / 2.0)
        val t1 = -n - k
        val t2 = -n + k
        val near = t1.maxComponent
        val far = t2.minComponent
        if (near > far || far < 0)
            return null
        /*val normal = (((-dir.sign) *
                t1.yzx.step(t1)) *
                t1.zxy.step(t1))
            .rotateY(angle)*/
        /*val normal = dir.sign.negated
            .times(Vector3(t1.y, t1.z, t1.x).step(t1))
            .times(Vector3(t1.z, t1.x, t1.y).step(t1))
            .rotateY(angle)*/
        val normal = (-dir.sign * t1.yzx.step(t1) * t1.zxy.step(t1)).rotateY(angle)
        return Bound.Intersection(near, far, normal)
    }

    companion object {
        val ZERO_ONE = Box(Vector3.ZERO, Vector3.ONE)
    }
}

data class Compound(
    val bounds: List<Bound>
) : Bound {
    override fun translated(vector: Vector3) = Compound(bounds.map { it.translated(vector) })

    override fun intersects(ray: Ray): Bound.Intersection? {
        var closest: Bound.Intersection? = null
        bounds.forEach { bound ->
            bound.intersects(ray)?.let { collision ->
                closest = closest?.let {
                    if (collision.tIn < it.tIn) collision else closest
                } ?: collision
            }
        }
        return closest
    }
}
