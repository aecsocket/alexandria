package com.gitlab.aecsocket.alexandria.core.physics

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class Transform(
    val translation: Vector3 = Vector3.Zero,
    val rotation: Quaternion = Quaternion.Identity,
) {
    val inverse get() = Transform(-translation, rotation.inverse)

    operator fun plus(t: Transform) = Transform(
        rotation * t.translation + translation,
        rotation * t.rotation,
    )

    // assuming this is an object -> world space transform...
    // · v is an object-space vector
    fun apply(v: Vector3) = rotation * v + translation

    // · v is a world-space vector
    fun invert(v: Vector3) = rotation.inverse * (v - translation)

    fun asString(fmt: String = "%f") = "[${translation.asString(fmt)}, ${rotation.asString(fmt)}]"

    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Transform()
    }
}
