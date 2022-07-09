package com.github.aecsocket.alexandria.core.physics

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Point2(val x: Int = 0, val y: Int = 0) {
    constructor(s: Int) : this(s, s)

    inline fun x(map: (Int) -> Int) = Point2(map(x), y)
    fun x(x: Int) = Point2(x, y)

    inline fun y(map: (Int) -> Int) = Point2(x, map(y))
    fun y(y: Int) = Point2(x, y)

    inline fun map(map: (Int) -> Int) = Point2(map(x), map(y))

    operator fun plus(p: Point2) = Point2(x+p.x, y+p.y)
    operator fun plus(s: Int) = Point2(x+s, y+s)

    operator fun minus(p: Point2) = Point2(x-p.x, y-p.y)
    operator fun minus(s: Int) = Point2(x-s, y-s)

    operator fun times(p: Point2) = Point2(x*p.x, y*p.y)
    operator fun times(s: Int) = Point2(x*s, y*s)

    operator fun div(p: Point2) = Point2(x/p.x, y/p.y)
    operator fun div(s: Int) = Point2(x/s, y/s)

    operator fun unaryMinus() = Point2(-x, -y)

    operator fun get(i: Int) = when (i) {
        0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException()
    }

    fun dot(p: Point2) = x*p.x + y*p.y

    val abs: Point2 get() = Point2(abs(x), abs(y))

    val min: Int get() = min(x, y)

    val max: Int get() = max(x, y)

    /* Swizzling
    val xx get() = Point2(x, x); val xy get() = Point2(x, y)
    val yx get() = Point2(y, x); val yy get() = Point2(y, y)*/

    fun asString(fmt: String = "%d") = "($fmt, $fmt)".format(x, y)
    override fun toString() = asString(INTEGER_FORMAT)

    companion object {
        val Zero = Point2(0)
        val X = Point2(1, 0)
        val Y = Point2(0, 1)
        val One = Point2(1)
    }
}

data class Point3(val x: Int = 0, val y: Int = 0, val z: Int = 0) {
    constructor(s: Int) : this(s, s, s)

    inline fun x(map: (Int) -> Int) = Point3(map(x), y, z)
    fun x(x: Int) = Point3(x, y, z)

    inline fun y(map: (Int) -> Int) = Point3(x, map(y), z)
    fun y(y: Int) = Point3(x, y, z)

    inline fun z(map: (Int) -> Int) = Point3(x, y, map(z))
    fun z(z: Int) = Point3(x, y, z)

    inline fun map(map: (Int) -> Int) = Point3(map(x), map(y), map(z))

    operator fun plus(p: Point3) = Point3(x+p.x, y+p.y, z+p.z)
    operator fun plus(s: Int) = Point3(x+s, y+s, z+s)

    operator fun minus(p: Point3) = Point3(x-p.x, y-p.y, z-p.z)
    operator fun minus(s: Int) = Point3(x-s, y-s, z-s)

    operator fun times(p: Point3) = Point3(x*p.x, y*p.y, z*p.z)
    operator fun times(s: Int) = Point3(x*s, y*s, z*s)

    operator fun div(p: Point3) = Point3(x/p.x, y/p.y, z/p.z)
    operator fun div(s: Int) = Point3(x/s, y/s, z/s)

    operator fun unaryMinus() = Point3(-x, -y, -z)

    operator fun get(i: Int) = when (i) {
        0 -> x; 1 -> y; 2 -> z; else -> throw IndexOutOfBoundsException()
    }

    fun dot(p: Point3) = x*p.x + y*p.y + z*p.z

    val abs: Point3 get() = Point3(abs(x), abs(y), abs(z))

    val min: Int get() = min(x, min(y, z))

    val max: Int get() = max(x, max(y, z))

    /* Swizzling
    val xx get() = Point2(x, x); val xy get() = Point2(x, y); val xz get() = Point2(x, z)
    val yx get() = Point2(y, x); val yy get() = Point2(y, y); val yz get() = Point2(y, z)
    val zx get() = Point2(z, x); val zy get() = Point2(z, y); val zz get() = Point2(z, z)

    val xxx get() = Point3(x, x, x); val xxy get() = Point3(x, x, y); val xxz get() = Point3(x, x, z)
    val xyx get() = Point3(x, y, x); val xyy get() = Point3(x, y, y); val xyz get() = Point3(x, y, z)
    val xzx get() = Point3(x, z, x); val xzy get() = Point3(x, z, y); val xzz get() = Point3(x, z, z)

    val yxx get() = Point3(y, x, x); val yxy get() = Point3(y, x, y); val yxz get() = Point3(y, x, z)
    val yyx get() = Point3(y, y, x); val yyy get() = Point3(y, y, y); val yyz get() = Point3(y, y, z)
    val yzx get() = Point3(y, z, x); val yzy get() = Point3(y, z, y); val yzz get() = Point3(y, z, z)

    val zxx get() = Point3(z, x, x); val zxy get() = Point3(z, x, y); val zxz get() = Point3(z, x, z)
    val zyx get() = Point3(z, y, x); val zyy get() = Point3(z, y, y); val zyz get() = Point3(z, y, z)
    val zzx get() = Point3(z, z, x); val zzy get() = Point3(z, z, y); val zzz get() = Point3(z, z, z)*/

    fun asString(fmt: String = "%d") = "($fmt, $fmt, $fmt)".format(x, y, z)
    override fun toString() = asString(INTEGER_FORMAT)

    companion object {
        val Zero = Point3(0)
        val X = Point3(1, 0, 0)
        val Y = Point3(0, 1, 0)
        val Z = Point3(0, 0, 1)
        val One = Point3(1)
    }
}

fun min(p1: Point2, p2: Point2) = Point2(min(p1.x, p2.x), min(p1.y, p2.y))
fun max(p1: Point2, p2: Point2) = Point2(max(p1.x, p2.x), max(p1.y, p2.y))

fun min(p1: Point3, p2: Point3) = Point3(min(p1.x, p2.x), min(p1.y, p2.y), min(p1.z, p2.z))
fun max(p1: Point3, p2: Point3) = Point3(max(p1.x, p2.x), max(p1.y, p2.y), max(p1.z, p2.z))
