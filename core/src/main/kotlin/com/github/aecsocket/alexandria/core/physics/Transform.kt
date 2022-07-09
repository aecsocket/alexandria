package com.github.aecsocket.alexandria.core.physics

// (no: scale, then) rotate, then translate
data class Transform(
    val rot: Quaternion = Quaternion.Identity,
    val tl: Vector3 = Vector3.Zero,
    val invRot: Quaternion = rot.inverse
) {
    operator fun plus(t: Transform) = Transform(
        rot * t.rot,
        rot * t.tl + tl,
    )

    val inverse get() = Transform(invRot, -tl, rot)

    // assuming this is an object -> world space transform...
    // · v is an object-space vector
    fun apply(v: Vector3 = Vector3.Zero) = rot * v + tl

    // · r is an object-space ray
    fun apply(r: Ray) = if (rot == Quaternion.Identity) Ray(apply(r.pos), r.dir, r.invDir)
        else Ray(apply(r.pos), rot * r.dir)

    // · v is a world-space vector
    fun invert(v: Vector3 = Vector3.Zero) = invRot * (v - tl)

    // · r is a world-space ray
    fun invert(r: Ray) = if (rot == Quaternion.Identity) Ray(invert(r.pos), r.dir, r.invDir)
        else Ray(invert(r.pos), invRot * r.dir)

    fun asString(fmt: String = "%f") = """Transform [
  rot = ${rot.asString(fmt)}
  tl = ${tl.asString(fmt)}
]"""

    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Identity = Transform()
    }
}
