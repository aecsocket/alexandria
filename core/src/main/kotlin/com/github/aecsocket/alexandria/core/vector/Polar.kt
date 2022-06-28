package com.github.aecsocket.alexandria.core.vector

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun deg(rad: Double) = rad * (180 / PI)

data class Polar2(val radius: Double, val angle: Double) {
    val x by lazy { radius * cos(angle) }

    val y by lazy { radius * sin(angle) }

    val cartesian by lazy { Vector2(x, y) }


    fun radius(radius: Double) = Polar2(radius, angle)

    fun angle(angle: Double) = Polar2(radius, angle)

    operator fun plus(other: Polar2) = Polar2(radius, angle + other.angle)
    operator fun plus(other: Double) = Polar2(radius, angle + other)

    operator fun minus(other: Polar2) = Polar2(radius, angle - other.angle)
    operator fun minus(other: Double) = Polar2(radius, angle - other)

    operator fun times(other: Polar2) = Polar2(radius, angle * other.angle)
    operator fun times(other: Double) = Polar2(radius, angle * other)

    operator fun div(other: Polar2) = Polar2(radius, angle / other.angle)
    operator fun div(other: Double) = Polar2(radius, angle / other)

    override fun toString() = "($radius, ${deg(angle)}°)"

    companion object {
        val ZERO = Polar2(1.0, 0.0)
    }
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

    fun radius(radius: Double) = Polar3(radius, yaw, pitch)

    fun yaw(yaw: Double) = Polar3(radius, yaw, pitch)

    fun pitch(pitch: Double) = Polar3(radius, yaw, pitch)


    operator fun plus(other: Polar3) = Polar3(radius, yaw + other.yaw, pitch + other.pitch)
    operator fun plus(other: Double) = Polar3(radius, yaw + other, pitch + other)

    operator fun minus(other: Polar3) = Polar3(radius, yaw - other.yaw, pitch - other.pitch)
    operator fun minus(other: Double) = Polar3(radius, yaw - other, pitch - other)

    operator fun times(other: Polar3) = Polar3(radius, yaw * other.yaw, pitch * other.pitch)
    operator fun times(other: Double) = Polar3(radius, yaw * other, pitch * other)

    operator fun div(other: Polar3) = Polar3(radius, yaw / other.yaw, pitch / other.pitch)
    operator fun div(other: Double) = Polar3(radius, yaw / other, pitch / other)

    override fun toString() = "($radius, ${deg(yaw)}°, ${deg(pitch)}°)"

    companion object {
        val ZERO = Polar3(1.0, 0.0, 0.0)
    }
}
