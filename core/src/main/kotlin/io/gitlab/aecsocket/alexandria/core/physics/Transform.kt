package io.gitlab.aecsocket.alexandria.core.physics

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class Transform(
    val position: Vector3 = Vector3.Zero,
    val rotation: Quaternion = Quaternion.Identity,
) {
    val inverse: Transform get() {
        val rotInv = rotation.inverse
        return Transform(rotInv * -position, rotInv)
    }

    val up get() = rotation * Vector3.Up
    val down get() = rotation * Vector3.Down
    val forward get() = rotation * Vector3.Forward
    val backward get() = rotation * Vector3.Backward
    val left get() = rotation * Vector3.Left
    val right get() = rotation * Vector3.Right

    operator fun times(t: Transform) = Transform(
        rotation * t.position + position,
        rotation * t.rotation,
    )

    // assuming this is an object -> world space transform...
    // · v is an object-space vector
    fun apply(v: Vector3) = rotation * v + position

    // · v is a world-space vector
    fun invert(v: Vector3) = rotation.inverse * (v - position)

    fun asString(fmt: String = "%f") = "Transform(${position.asString(fmt)}, ${rotation.asString(fmt)})"

    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Transform()
    }
}

fun transformDelta(from: Transform, to: Transform) = from.inverse * to
