package com.gitlab.aecsocket.alexandria.core.physics

data class Transform(
    val rotation: Quaternion = Quaternion.Identity,
    val translation: Vector3 = Vector3.Zero,
    val invRotation: Quaternion = rotation.inverse,
) {
    val inverse get() = Transform(invRotation, -translation, rotation)

    operator fun plus(t: Transform) = Transform(
        rotation * t.rotation,
        rotation * t.translation + translation,
    )

    // assuming this is an object -> world space transform...
    // · v is an object-space vector
    fun apply(v: Vector3) = rotation * v + translation

    // · r is an object-space ray
    fun apply(r: Ray) = if (rotation == Quaternion.Identity) Ray(apply(r.pos), r.dir, r.invDir)
        else Ray(apply(r.pos), rotation * r.dir)

    // · v is a world-space vector
    fun invert(v: Vector3) = invRotation * (v - translation)

    // · r is a world-space ray
    fun invert(r: Ray) = if (rotation == Quaternion.Identity) Ray(invert(r.pos), r.dir, r.invDir)
        else Ray(invert(r.pos), invRotation * r.dir)

    fun asString(fmt: String = "%f") = """Transform [
  rot = ${rotation.asString(fmt)}
  tl = ${translation.asString(fmt)}
]"""

    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Transform()
    }
}
