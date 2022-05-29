package com.github.aecsocket.alexandria.core.bound

import com.github.aecsocket.alexandria.core.vector.Vector3

/*
possible optimizations:
- mutable bound classes
- .translated and .oriented don't change internal values e.g. box extent, sphere sqrRadius
- SIMD (if I can figure out how it works)
 */

data class Ray(val origin: Vector3, val direction: Vector3) {
    fun point(t: Double) = origin + direction * t
}

interface Bound {
    data class Intersection(
        val tIn: Double,
        val tOut: Double,
        val normal: Vector3
    )

    fun intersects(ray: Ray): Intersection?

    fun translated(vector: Vector3): Bound

    interface Oriented : Bound {
        val angle: Double

        fun oriented(angle: Double): Oriented
    }
}
