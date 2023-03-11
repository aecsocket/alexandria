package io.github.aecsocket.alexandria.core.math

data class Ray(
    @JvmField val origin: Vec3d,
    @JvmField val direction: Vec3f
) {
    val directionInv = direction.inverse()

    fun at(t: Float) = origin + Vec3d(direction * t)

    fun asString(fmt: String = "%f") = "Ray(${origin.asString(fmt)}, ${direction.asString(fmt)})"

    override fun toString() = asString(DECIMAL_FORMAT)
}
