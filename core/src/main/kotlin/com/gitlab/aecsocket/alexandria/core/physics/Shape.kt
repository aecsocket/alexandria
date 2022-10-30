package com.gitlab.aecsocket.alexandria.core.physics

import com.gitlab.aecsocket.alexandria.core.extension.EPSILON
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required
import org.spongepowered.configurate.objectmapping.meta.Setting
import kotlin.math.abs
import kotlin.math.sqrt

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

@ConfigSerializable
data class CompoundShape(
    @Setting(nodeFromParent = true) val children: List<Child>
) : Shape {
    @ConfigSerializable
    data class Child(
        val shape: Shape,
        val transform: Transform
    )
}

data class Ray(
    val pos: Vector3,
    val dir: Vector3,
    val invDir: Vector3 = dir.inv
) {
    fun point(t: Double) = pos + dir * t
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

fun testRaySphere(ray: Ray, sphere: SphereShape): CollisionInfo? {
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
    return CollisionInfo(tIn, tOut, ray.point(tIn).normalized)
}

// https://iquilezles.org/articles/boxfunctions/
// https://www.shadertoy.com/view/ld23DV
fun testRayBox(ray: Ray, box: BoxShape): CollisionInfo? {
    val m = ray.invDir
    val n = m * ray.pos
    val k = m.abs * box.halfExtent
    val t1 = -n - k
    val t2 = -n + k

    val tN = t1.max
    val tF = t2.min

    if (tN > tF || tF < 0.0) return null

    val oN = -ray.dir.sign * step(Vector3(t1.y, t1.z, t1.x), t1) * step(Vector3(t1.z, t1.x, t1.y), t1)

    return CollisionInfo(tN, tF, oN)
}

// https://stackoverflow.com/questions/23975555/how-to-do-ray-plane-intersection
fun testRayPlane(ray: Ray, plane: PlaneShape): CollisionInfo? {
    val norm = plane.normal
    val denom = norm.dot(ray.dir)
    if (abs(denom) > EPSILON) {
        val t = (-ray.pos).dot(norm) / denom
        return if (t >= EPSILON) CollisionInfo(t, t + EPSILON, norm) else null
    }
    return null
}

fun testRayCompound(ray: Ray, compound: CompoundShape): CollisionInfo? {
    val collisions = compound.children.mapNotNull { (child, transform) ->
        testRayShape(transform.invert(ray), child)
    }
    return collisions.minByOrNull{ it.tIn }
}

fun testRayShape(ray: Ray, shape: Shape): CollisionInfo? {
    return when (shape) {
        is EmptyShape -> null
        is SphereShape -> testRaySphere(ray, shape)
        is BoxShape -> testRayBox(ray, shape)
        is PlaneShape -> testRayPlane(ray, shape)
        is CompoundShape -> testRayCompound(ray, shape)
    }
}

