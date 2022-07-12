package com.github.aecsocket.alexandria.core.extension

import com.github.aecsocket.alexandria.core.effect.Effector
import com.github.aecsocket.alexandria.core.effect.ParticleEffect
import com.github.aecsocket.alexandria.core.physics.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun Effector.showLine(effect: ParticleEffect, from: Vector3, to: Vector3, step: Double) {
    val delta = to - from
    val stepVec = delta.normalized * step
    repeat((delta.length / step).toInt()) {
        showParticle(effect, from + (stepVec * it.toDouble()))
    }
}

data class CuboidVertices(
    val b0: Vector3, val b1: Vector3, val b2: Vector3, val b3: Vector3,
    val t0: Vector3, val t1: Vector3, val t2: Vector3, val t3: Vector3,
) {
    fun map(map: (Vector3) -> Vector3) = CuboidVertices(
        map(b0), map(b1), map(b2), map(b3),
        map(t0), map(t1), map(t2), map(t3),
    )
}

fun Effector.showCuboid(
    effect: ParticleEffect,
    vertices: CuboidVertices,
    step: Double
) {
    val (b0, b1, b2, b3, t0, t1, t2, t3) = vertices

    fun line(from: Vector3, to: Vector3) {
        showLine(effect, from, to, step)
    }

    line(b0, b1)
    line(b1, b2)
    line(b2, b3)
    line(b3, b0)

    line(t0, t1)
    line(t1, t2)
    line(t2, t3)
    line(t3, t0)

    line(b0, t0)
    line(b1, t1)
    line(b2, t2)
    line(b3, t3)
}

fun boxVertices(v0: Vector3, v1: Vector3) = CuboidVertices(
    v0,                        Vector3(v1.x, v0.y, v0.z), Vector3(v1.x, v0.y, v1.z), Vector3(v0.x, v0.y, v1.z),
    Vector3(v0.x, v1.y, v0.z), Vector3(v1.x, v1.y, v0.z), v1,                        Vector3(v0.x, v1.y, v1.z)
)

fun boxVertices(box: Box) = boxVertices(-box.halfExtent, box.halfExtent)

// TODO this is more like "2 circles vertices"
fun sphereVertices(sphere: Sphere, step: Double): Array<Vector3> {
    val circ = 2 * PI * sphere.radius
    val divisions = (circ / step).toInt()
    val angStep = (2 * PI) / divisions

    return Array(divisions * 2) {
        val ang = angStep * it
        val a = cos(ang) * sphere.radius
        val b = sin(ang) * sphere.radius
        if (it <= divisions) Vector3(a, 0.0, b)
        else Vector3(a, b, 0.0)
    }
}

fun Effector.showShape(
    effect: ParticleEffect,
    shape: Shape,
    transform: Transform,
    step: Double,
) {
    when (shape) {
        is Empty -> {}
        is Box -> {
            showCuboid(effect, boxVertices(shape).map { transform.apply(it) }, step)
        }
        is Sphere -> {
            sphereVertices(shape, step).map { transform.apply(it) }.forEach {
                showParticle(effect, it)
            }
        }
    }
}
