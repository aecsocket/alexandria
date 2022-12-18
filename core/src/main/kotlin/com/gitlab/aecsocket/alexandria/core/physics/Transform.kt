package com.gitlab.aecsocket.alexandria.core.physics

import com.gitlab.aecsocket.alexandria.core.extension.matrix
import com.gitlab.aecsocket.alexandria.core.extension.transform
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class Transform(
    val translation: Vector3 = Vector3.Zero,
    val rotation: Quaternion = Quaternion.Identity,
) {
    val inverse: Transform get() {
        val rotInv = rotation.inverse
        return Transform(rotInv * -translation, rotInv)
    }

    operator fun times(t: Transform) = Transform(
        rotation * t.translation + translation,
        rotation * t.rotation,
    )

    // assuming this is an object -> world space transform...
    // · v is an object-space vector
    fun apply(v: Vector3) = rotation * v + translation

    // · v is a world-space vector
    fun invert(v: Vector3) = rotation.inverse * (v - translation)

    fun asString(fmt: String = "%f") = "Transform(${translation.asString(fmt)}, ${rotation.asString(fmt)})"

    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Transform()
    }
}

fun transformDelta(from: Transform, to: Transform) = from.inverse * to
