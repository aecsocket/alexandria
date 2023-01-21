package com.gitlab.aecsocket.alexandria.core.physics

import com.gitlab.aecsocket.alexandria.core.extension.EPSILON
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required
import org.spongepowered.configurate.objectmapping.meta.Setting
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

sealed interface Shape {
    fun testRay(ray: Ray): CollisionInfo?
}

data class Ray(
    val pos: Vector3,
    val dir: Vector3,
    val invDir: Vector3 = dir.inv
) {
    fun at(t: Double) = pos + dir * t

    fun asString(fmt: String = "%f") = "Ray(${pos.asString(fmt)}, ${dir.asString(fmt)})"

    override fun toString() = asString(DECIMAL_FORMAT)
}

fun Transform.apply(r: Ray): Ray {
    return if (rotation == Quaternion.Identity) Ray(apply(r.pos), r.dir, r.invDir)
    else Ray(apply(r.pos), rotation * r.dir)
}

fun Transform.invert(r: Ray): Ray {
    return if (rotation == Quaternion.Identity) Ray(invert(r.pos), r.dir, r.invDir)
    else Ray(invert(r.pos), rotation.inverse * r.dir)
}

data class CollisionInfo(
    val tIn: Double,
    val tOut: Double,
    val normal: Vector3,
)

enum class ShapeAxis { X, Y, Z }

object EmptyShape : Shape {
    override fun testRay(ray: Ray) = null
}

@ConfigSerializable
data class CompoundShape(
    @Setting(nodeFromParent = true) val children: List<Child>
) : Shape {
    override fun testRay(ray: Ray): CollisionInfo? {
        return children
            .mapNotNull { (child, transform) ->
                child.testRay(transform.invert(ray))
            }
            .minByOrNull { it.tIn }
    }

    @ConfigSerializable
    data class Child(
        val shape: Shape,
        val transform: Transform
    )
}

@ConfigSerializable
data class PlaneShape(
    @Required val normal: Vector3
) : Shape {
    // https://stackoverflow.com/questions/23975555/how-to-do-ray-plane-intersection
    override fun testRay(ray: Ray): CollisionInfo? {
        val denom = normal.dot(ray.dir)
        if (abs(denom) > EPSILON) {
            val t = (-ray.pos).dot(normal) / denom
            return if (t >= EPSILON) CollisionInfo(t, t + EPSILON, normal) else null
        }
        return null
    }

    companion object {
        val X = PlaneShape(Vector3.X)
        val Y = PlaneShape(Vector3.Y)
        val Z = PlaneShape(Vector3.Z)
    }
}

@ConfigSerializable
data class SphereShape(
    @Required val radius: Double
) : Shape {
    val sqrRadius = radius * radius

    override fun testRay(ray: Ray): CollisionInfo? {
        val m = ray.pos
        val b = m.dot(ray.dir)
        val c = m.dot(m) - sqrRadius

        if (c > 0 && b > 0) return null
        val sqrDiscrim = b*b - c
        if (sqrDiscrim < 0) return null
        val discrim = sqrt(sqrDiscrim)

        val tIn = -b - discrim
        val tOut = -b + discrim
        return CollisionInfo(tIn, tOut, ray.at(tIn).normalized)
    }

    companion object {
        val Unit = SphereShape(1.0)
    }
}

@ConfigSerializable
data class BoxShape(
    @Required val halfExtent: Vector3
) : Shape {
    // https://iquilezles.org/articles/boxfunctions/
    // https://www.shadertoy.com/view/ld23DV
    override fun testRay(ray: Ray): CollisionInfo? {
        val m = ray.invDir
        val n = m * ray.pos
        val k = m.abs * halfExtent
        val t1 = -n - k
        val t2 = -n + k

        val tN = t1.max
        val tF = t2.min

        if (tN > tF || tF < 0.0) return null

        val oN = -ray.dir.sign * step(Vector3(t1.y, t1.z, t1.x), t1) * step(Vector3(t1.z, t1.x, t1.y), t1)
        return CollisionInfo(tN, tF, oN)
    }

    companion object {
        val Unit = BoxShape(Vector3.One)
        val Half = BoxShape(Vector3(0.5))
    }
}

@ConfigSerializable
data class CylinderShape(
    @Required val radius: Double,
    @Required val height: Double,
    @Required val axis: ShapeAxis
) : Shape {
    // https://iquilezles.org/articles/intersectors/
    // NOTE: here, tIn = tOut
    override fun testRay(ray: Ray): CollisionInfo? {
        val pa = when (axis) {
            ShapeAxis.X -> Vector3(height / 2, 0.0, 0.0)
            ShapeAxis.Y -> Vector3(0.0, height / 2, 0.0)
            ShapeAxis.Z -> Vector3(0.0, 0.0, height / 2)
        }
        val pb = -pa

        val ba = pb - pa
        val oc = ray.pos - pa
        val baba = ba.dot(ba)
        val bard = ba.dot(ray.dir)
        val baoc = ba.dot(oc)
        val k2 = baba - bard * bard
        val k1 = baba * oc.dot(ray.dir) - baoc * bard
        val k0 = baba * oc.dot(oc) - baoc * baoc - radius * radius * baba
        var h = k1 * k1 - k2 * k0

        if (h < 0.0) return null

        h = sqrt(h)
        var t = (-k1 - h) / k2

        // body
        val y = baoc + t * bard
        if (y > 0.0 && y < baba) return CollisionInfo(t, t, (oc + (ray.dir * t) - ba * y / baba) / radius)
        // caps
        t = ((if (y < 0.0) 0.0 else baba) - baoc) / bard
        return if (abs(k1 + k2 * t) < h) CollisionInfo(t, t, ba * sign(y) / sqrt(baba))
        else null
    }
}
