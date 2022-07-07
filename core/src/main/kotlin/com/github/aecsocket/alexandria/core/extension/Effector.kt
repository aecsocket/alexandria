package com.github.aecsocket.alexandria.core.extension

import com.github.aecsocket.alexandria.core.bound.*
import com.github.aecsocket.alexandria.core.effect.Effector
import com.github.aecsocket.alexandria.core.effect.ParticleEffect
import com.github.aecsocket.alexandria.core.spatial.Quaternion
import com.github.aecsocket.alexandria.core.spatial.Vector3


fun Effector.particleLine(effect: ParticleEffect, from: Vector3, to: Vector3, step: Double) {
    val delta = to - from
    val stepVec = delta.normalized * step
    (0 until (delta.length / step).toInt()).forEach {
        showParticle(effect, from + (stepVec * it.toDouble()))
    }
}

data class CuboidVertices(val bottom: Array<Vector3>, val top: Array<Vector3>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CuboidVertices

        if (!bottom.contentEquals(other.bottom)) return false
        if (!top.contentEquals(other.top)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bottom.contentHashCode()
        result = 31 * result + top.contentHashCode()
        return result
    }
}

fun Effector.particleCuboid(effect: ParticleEffect, vertices: CuboidVertices, step: Double) {
    val (bottom, top) = vertices

    fun line(from: Vector3, to: Vector3) {
        particleLine(effect, from, to, step)
    }

    fun vertical(arr: Array<Vector3>) {
        line(arr[0], arr[1])
        line(arr[1], arr[2])
        line(arr[2], arr[3])
        line(arr[3], arr[0])
    }

    vertical(bottom)
    vertical(top)

    (0 until 4).forEach { line(bottom[it], top[it]) }
}

fun cuboidVertices(min: Vector3, max: Vector3): CuboidVertices {
    fun vertical(y: Double) = arrayOf(
        Vector3(min.x, y, min.z), Vector3(max.x, y, min.z),
        Vector3(max.x, y, max.z), Vector3(min.x, y, max.z),
    )

    return CuboidVertices(vertical(min.y), vertical(max.y))
}

fun cuboidVertices(min: Vector3, max: Vector3, origin: Vector3, rotation: Quaternion): CuboidVertices {
    val (bottom, top) = cuboidVertices(min, max)

    fun Array<Vector3>.rotate() = map { (rotation * (it - origin)) + origin }.toTypedArray()

    return CuboidVertices(bottom.rotate(), top.rotate())
}

val Box.vertices get() = cuboidVertices(min, max, origin, rotation)

fun Effector.particleBound(effect: ParticleEffect, bound: Bound, step: Double) {
    when (bound) {
        is Empty -> {}
        is Compound -> bound.bounds.forEach { particleBound(effect, it, step) }
        is Box -> particleCuboid(effect, bound.vertices, step)
        is Sphere -> TODO("todo")
    }
}
