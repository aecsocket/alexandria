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

sealed interface Shape

object EmptyShape : Shape

@ConfigSerializable
data class SphereShape(
    @Required val radius: Double
) : Shape {
    val sqrRadius = radius * radius

    companion object {
        val Unit = SphereShape(1.0)
    }
}

@ConfigSerializable
data class BoxShape(
    @Required val halfExtent: Vector3
) : Shape {
    companion object {
        val Unit = BoxShape(Vector3.One)
        val Half = BoxShape(Vector3(0.5))
    }
}

@ConfigSerializable
data class PlaneShape(
    @Required val normal: Vector3
) : Shape {
    companion object {
        val X = PlaneShape(Vector3.X)
        val Y = PlaneShape(Vector3.Y)
        val Z = PlaneShape(Vector3.Z)
    }
}

fun testRaySphere(ray: Ray, sphere: SphereShape): Collision? {
    val m = ray.pos
    val b = m.dot(ray.dir)
    val c = m.dot(m) - sphere.sqrRadius

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

// https://iquilezles.org/articles/boxfunctions/
// https://www.shadertoy.com/view/ld23DV
fun testRayBox(ray: Ray, box: BoxShape): Collision? {
    val m = ray.invDir
    val n = m * ray.pos
    val k = m.abs * box.halfExtent
    val t1 = -n - k
    val t2 = -n + k

    val tN = t1.max
    val tF = t2.min

    if (tN > tF || tF < 0.0) return null

    val oN = -ray.dir.sign * step(Vector3(t1.y, t1.z, t1.x), t1) * step(Vector3(t1.z, t1.x, t1.y), t1)

    return Collision(tN, tF, oN)
}

// https://stackoverflow.com/questions/23975555/how-to-do-ray-plane-intersection
fun testRayPlane(ray: Ray, plane: PlaneShape): Collision? {
    val norm = plane.normal
    val denom = norm.dot(ray.dir)
    if (abs(denom) > EPSILON) {
        val t = (-ray.pos).dot(norm) / denom
        return if (t >= EPSILON) Collision(t, t + EPSILON, norm) else null
    }
    return null
}

fun testRayShape(ray: Ray, shape: Shape): Collision? {
    return when (shape) {
        is EmptyShape -> null
        is SphereShape -> testRaySphere(ray, shape)
        is BoxShape -> testRayBox(ray, shape)
        is PlaneShape -> testRayPlane(ray, shape)
    }
}
