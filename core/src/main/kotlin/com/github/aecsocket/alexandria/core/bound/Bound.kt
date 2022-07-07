package com.github.aecsocket.alexandria.core.bound

import com.github.aecsocket.alexandria.core.spatial.Quaternion
import com.github.aecsocket.alexandria.core.spatial.Vector3

data class Ray(val pos: Vector3, val dir: Vector3) {
    val invDir = dir.inv

    fun point(t: Double) = pos + dir * t
}

data class Collision(
    val tIn: Double,
    val tOut: Double,
    val normal: Vector3
)

fun Iterable<Collision>.closest(): Collision? {
    var res: Collision? = null
    forEach { test ->
        res = res?.let { if (test.tIn < it.tIn) test else it } ?: test
    }
    return res
}

sealed interface Bound {
    fun collides(ray: Ray): Collision?

    fun translated(vector: Vector3): Bound

    fun rotated(rotation: Quaternion): Bound
}
