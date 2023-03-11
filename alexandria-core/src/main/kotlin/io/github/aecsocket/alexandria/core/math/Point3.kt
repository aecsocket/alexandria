package io.github.aecsocket.alexandria.core.math

/**
 * A 3-dimensional integer vector.
 * @param x X coordinate.
 * @param y Y coordinate.
 * @param z Z coordinate.
 */
data class Point3(@JvmField val x: Int, @JvmField val y: Int, @JvmField val z: Int) {
    companion object {
        /** The point of (0, 0, 0) */
        val Zero = Point3(0)
    }

    constructor(s: Int) : this(s, s, s)

    constructor(v: Vec3f) : this(v.x.toInt(), v.y.toInt(), v.z.toInt())

    constructor(v: Vec3d) : this(v.x.toInt(), v.y.toInt(), v.z.toInt())

    operator fun plus(p: Point3) = Point3(x+p.x, y+p.y, z+p.z)
    operator fun plus(s: Int) = Point3(x+s, y+s, z+s)

    operator fun minus(p: Point3) = Point3(x-p.x, y-p.y, z-p.z)
    operator fun minus(s: Int) = Point3(x-s, y-s, z-s)

    operator fun times(p: Point3) = Point3(x*p.x, y*p.y, z*p.z)
    operator fun times(s: Int) = Point3(x*s, y*s, z*s)

    operator fun div(p: Point3) = Point3(x/p.x, y/p.y, z/p.z)
    operator fun div(s: Int) = Point3(x/s, y/s, z/s)

    operator fun unaryMinus() = Point3(-x, -y, -z)

    fun toVec3f() = Vec3f(x.toFloat(), y.toFloat(), z.toFloat())
    fun toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

    fun asString(fmt: String = "%d") = "($fmt, $fmt, $fmt)".format(x, y, z)

    override fun toString() = asString(INTEGER_FORMAT)

    override fun equals(other: Any?) = other is Point3 &&
            x.compareTo(other.x) == 0 && y.compareTo(other.y) == 0 && z.compareTo(other.z) == 0

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}
