package com.github.aecsocket.alexandria.core.vector

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun deg(rad: Double) = rad * (180 / PI)

data class Polar2(val radius: Double, val angle: Double) {
    val x by lazy { radius * cos(angle) }

    val y by lazy { radius * sin(angle) }

    val cartesian by lazy { Vector2(x, y) }

    override fun toString() = "($radius, ${deg(angle)}°)"
}

data class Polar3(val radius: Double, val yaw: Double, val pitch: Double) {
    val x by lazy { radius * -cos(pitch) * sin(yaw) }

    val y by lazy { radius * -sin(pitch) }

    val z by lazy { radius * cos(pitch) * cos(yaw) }

    val cartesian by lazy {
        val xz = cos(pitch)
        Vector3(
            -xz * sin(yaw),
            -sin(pitch),
            xz * cos(yaw)
        ) * radius
    }

    override fun toString() = "($radius, ${deg(yaw)}°, ${deg(pitch)}°)"
}
