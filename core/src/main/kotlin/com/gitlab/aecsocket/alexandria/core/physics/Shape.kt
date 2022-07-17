package com.gitlab.aecsocket.alexandria.core.physics

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

sealed interface Shape {
    fun collides(ray: Ray): Collision?
}
