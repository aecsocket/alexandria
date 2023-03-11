package io.github.aecsocket.alexandria.core.math

import io.github.aecsocket.alexandria.core.assertGtEq

/**
 * An axis-aligned bounding box, based on a minimum and maximum [Vec3d].
 */
data class AABB(@JvmField val min: Vec3d, @JvmField val max: Vec3d) {
    companion object {
        /** The box with minimum (0, 0, 0) and maximum (0, 0, 0) */
        val Zero = AABB(Vec3d.Zero, Vec3d.Zero)
        /** The box with minimum (0, 0, 0) and maximum (1, 1, 1) */
        val Unit = AABB(Vec3d.Zero, Vec3d.One)
    }

    init {
        assertGtEq("x", min.x, max.x)
        assertGtEq("y", min.y, max.y)
        assertGtEq("z", min.z, max.z)
    }

    operator fun plus(v: Vec3d) = AABB(min + v, max + v)
    operator fun plus(s: Double) = AABB(min + s, max + s)

    operator fun minus(v: Vec3d) = AABB(min - v, max - v)
    operator fun minus(s: Double) = AABB(min - s, max - s)

    operator fun times(v: Vec3d) = AABB(min * v, max * v)
    operator fun times(s: Double) = AABB(min * s, max * s)

    operator fun div(v: Vec3d) = AABB(min / v, max / v)
    operator fun div(s: Double) = AABB(min / s, max / s)

    fun center() = min.midpoint(max)

    fun extent() = max - min

    fun halfExtent() = (max - min) / 2.0

    fun asString(fmt: String = "%f") = "AABB(${min.asString(fmt)}, ${max.asString(fmt)})"

    override fun toString() = asString(DECIMAL_FORMAT)
}
