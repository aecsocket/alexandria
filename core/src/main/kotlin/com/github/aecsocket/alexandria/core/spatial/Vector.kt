package com.github.aecsocket.alexandria.core.spatial

import com.github.aecsocket.alexandria.core.extension.clamp
import kotlin.math.*

private fun sqr(v: Double) = v*v

data class Vector2(val x: Double = 0.0, val y: Double = 0.0) {
    constructor(s: Double) : this(s, s)

    inline fun x(map: (Double) -> Double) = Vector2(map(x), y)
    fun x(x: Double) = Vector2(x, y)

    inline fun y(map: (Double) -> Double) = Vector2(x, map(y))
    fun y(y: Double) = Vector2(x, y)

    inline fun map(map: (Double) -> Double) = Vector2(map(x), map(y))

    operator fun plus(p: Vector2) = Vector2(x+p.x, y+p.y)
    operator fun plus(s: Double) = Vector2(x+s, y+s)

    operator fun minus(p: Vector2) = Vector2(x-p.x, y-p.y)
    operator fun minus(s: Double) = Vector2(x-s, y-s)

    operator fun times(p: Vector2) = Vector2(x*p.x, y*p.y)
    operator fun times(s: Double) = Vector2(x*s, y*s)

    operator fun div(p: Vector2) = Vector2(x/p.x, y/p.y)
    operator fun div(s: Double) = Vector2(x/s, y/s)

    operator fun unaryMinus() = Vector2(-x, -y)

    operator fun get(i: Int) = when (i) {
        0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException()
    }

    fun dot(p: Vector2) = x*p.x + y*p.y

    fun sqrDistance(p: Vector2) = sqr(p.x-x) + sqr(p.y-y)

    fun distance(p: Vector2) = sqrt(sqrDistance(p))

    fun midpoint(p: Vector2) = Vector2((p.x+x)/2, (p.y+y)/2)

    fun angle(p: Vector2) = acos(clamp(dot(p) / (length * p.length), -1.0, 1.0))

    val abs: Vector2 get() = Vector2(abs(x), abs(y))

    val inv: Vector2 get() = Vector2(1/x, 1/y)

    val manhattanLength: Double get() = abs(x) + abs(y)

    val sqrLength: Double get() = x*x + y*y

    val length: Double get() = sqrt(sqrLength)

    val normalized: Vector2 get() = Vector2(x/length, y/length)

    val sign: Vector2 get() = Vector2(sign(x), sign(y))

    val min: Double get() = min(x, y)

    val max: Double get() = max(x, y)

    /* Swizzling
    val xx get() = Vector2(x, x); val xy get() = Vector2(x, y)
    val yx get() = Vector2(y, x); val yy get() = Vector2(y, y)*/

    fun asString(fmt: String = "%f") = "($fmt, $fmt)".format(x, y)
    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Zero = Vector2(0.0)
        val X = Vector2(1.0, 0.0)
        val Y = Vector2(0.0, 1.0)
        val One = Vector2(1.0)
    }
}

data class Vector3(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0) {
    constructor(s: Double) : this(s, s, s)

    inline fun x(map: (Double) -> Double) = Vector3(map(x), y, z)
    fun x(x: Double) = Vector3(x, y, z)

    inline fun y(map: (Double) -> Double) = Vector3(x, map(y), z)
    fun y(y: Double) = Vector3(x, y, z)

    inline fun z(map: (Double) -> Double) = Vector3(x, y, map(z))
    fun z(z: Double) = Vector3(x, y, z)

    inline fun map(map: (Double) -> Double) = Vector3(map(x), map(y), map(z))

    operator fun plus(p: Vector3) = Vector3(x+p.x, y+p.y, z+p.z)
    operator fun plus(s: Double) = Vector3(x+s, y+s, z+s)

    operator fun minus(p: Vector3) = Vector3(x-p.x, y-p.y, z-p.z)
    operator fun minus(s: Double) = Vector3(x-s, y-s, z-s)

    operator fun times(p: Vector3) = Vector3(x*p.x, y*p.y, z*p.z)
    operator fun times(s: Double) = Vector3(x*s, y*s, z*s)

    operator fun div(p: Vector3) = Vector3(x/p.x, y/p.y, z/p.z)
    operator fun div(s: Double) = Vector3(x/s, y/s, z/s)

    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun get(i: Int) = when (i) {
        0 -> x; 1 -> y; 2 -> z; else -> throw IndexOutOfBoundsException()
    }

    fun dot(p: Vector3) = x*p.x + y*p.y + z*p.z

    fun sqrDistance(p: Vector3) = sqr(p.x-x) + sqr(p.y-y) + sqr(p.z-z)

    fun distance(p: Vector3) = sqrt(sqrDistance(p))

    fun midpoint(p: Vector3) = Vector3((p.x+x)/2, (p.y+y)/2, (p.z+z)/2)

    fun angle(p: Vector3) = acos(clamp(dot(p) / (length * p.length), -1.0, 1.0))

    fun cross(v: Vector3) = Vector3(
        y*v.z - v.y*z,
        z*v.x - v.z*x,
        x*v.y - v.x*y,
    )

    fun rotate(v: Vector3, a: Double): Vector3 {
        val cos = cos(a)
        val sin = sin(a)
        val dot = dot(v)
        return Vector3(
            v.x*dot*(1-cos) + x*cos+(-v.z*y + v.y*z) * sin,
            v.y*dot*(1-cos) + y*cos + (v.z*x - v.x*z) * sin,
            v.z*dot*(1-cos) + z*cos + (-v.y*x + v.x*y) * sin
        )
    }

    val abs: Vector3 get() = Vector3(abs(x), abs(y), abs(z))

    val inv: Vector3 get() = Vector3(1/x, 1/y, 1/z)

    val manhattanLength: Double get() = abs(x) + abs(y) + abs(z)

    val sqrLength: Double get() = x*x + y*y + z*z

    val length: Double get() = sqrt(sqrLength)

    val normalized: Vector3 get() = Vector3(x/length, y/length, z/length)

    val sign: Vector3 get() = Vector3(sign(x), sign(y), sign(z))

    val min: Double get() = min(x, min(y, z))

    val max: Double get() = max(x, max(y, z))

    /* Swizzling
    val xx get() = Vector2(x, x); val xy get() = Vector2(x, y); val xz get() = Vector2(x, z)
    val yx get() = Vector2(y, x); val yy get() = Vector2(y, y); val yz get() = Vector2(y, z)
    val zx get() = Vector2(z, x); val zy get() = Vector2(z, y); val zz get() = Vector2(z, z)

    val xxx get() = Vector3(x, x, x); val xxy get() = Vector3(x, x, y); val xxz get() = Vector3(x, x, z)
    val xyx get() = Vector3(x, y, x); val xyy get() = Vector3(x, y, y); val xyz get() = Vector3(x, y, z)
    val xzx get() = Vector3(x, z, x); val xzy get() = Vector3(x, z, y); val xzz get() = Vector3(x, z, z)

    val yxx get() = Vector3(y, x, x); val yxy get() = Vector3(y, x, y); val yxz get() = Vector3(y, x, z)
    val yyx get() = Vector3(y, y, x); val yyy get() = Vector3(y, y, y); val yyz get() = Vector3(y, y, z)
    val yzx get() = Vector3(y, z, x); val yzy get() = Vector3(y, z, y); val yzz get() = Vector3(y, z, z)

    val zxx get() = Vector3(z, x, x); val zxy get() = Vector3(z, x, y); val zxz get() = Vector3(z, x, z)
    val zyx get() = Vector3(z, y, x); val zyy get() = Vector3(z, y, y); val zyz get() = Vector3(z, y, z)
    val zzx get() = Vector3(z, z, x); val zzy get() = Vector3(z, z, y); val zzz get() = Vector3(z, z, z)*/

    fun asString(fmt: String = "%f") = "($fmt, $fmt, $fmt)".format(x, y, z)
    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Zero = Vector3(0.0)
        val X = Vector3(1.0, 0.0, 0.0)
        val Y = Vector3(0.0, 1.0, 0.0)
        val Z = Vector3(0.0, 0.0, 1.0)
        val One = Vector3(1.0)
    }
}

data class Vector4(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0, val w: Double = 0.0) {
    constructor(s: Double) : this(s, s, s, s)

    inline fun x(map: (Double) -> Double) = Vector4(map(x), y, z, w)
    fun x(x: Double) = Vector4(x, y, z, w)

    inline fun y(map: (Double) -> Double) = Vector4(x, map(y), z, w)
    fun y(y: Double) = Vector4(x, y, z, w)

    inline fun z(map: (Double) -> Double) = Vector4(x, y, map(z), w)
    fun z(z: Double) = Vector4(x, y, z, w)

    inline fun w(map: (Double) -> Double) = Vector4(x, y, z, map(w))
    fun w(w: Double) = Vector4(x, y, z, w)

    inline fun map(map: (Double) -> Double) = Vector4(map(x), map(y), map(z), map(w))

    operator fun plus(p: Vector4) = Vector4(x+p.x, y+p.y, z+p.z, w+p.z)
    operator fun plus(s: Double) = Vector4(x+s, y+s, z+s, w+s)

    operator fun minus(p: Vector4) = Vector4(x-p.x, y-p.y, z-p.z, w-p.w)
    operator fun minus(s: Double) = Vector4(x-s, y-s, z-s, w-s)

    operator fun times(p: Vector4) = Vector4(x*p.x, y*p.y, z*p.z, w*p.w)
    operator fun times(s: Double) = Vector4(x*s, y*s, z*s, w*s)

    operator fun div(p: Vector4) = Vector4(x/p.x, y/p.y, z/p.z, w/p.w)
    operator fun div(s: Double) = Vector4(x/s, y/s, z/s, w/s)

    operator fun unaryMinus() = Vector4(-x, -y, -z, -w)

    operator fun get(i: Int) = when (i) {
        0 -> x; 1 -> y; 2 -> z; 3 -> w; else -> throw IndexOutOfBoundsException()
    }

    fun dot(p: Vector4) = x*p.x + y*p.y + z*p.z + w*p.w

    fun sqrDistance(p: Vector4) = sqr(p.x-x) + sqr(p.y-y) + sqr(p.z-z) + sqr(p.w-w)

    fun distance(p: Vector4) = sqrt(sqrDistance(p))

    fun midpoint(p: Vector4) = Vector4((p.x+x)/2, (p.y+y)/2, (p.z+z)/2, (p.w+w)/2)

    fun angle(p: Vector4) = acos(clamp(dot(p) / (length * p.length), -1.0, 1.0))

    val abs: Vector4 get() = Vector4(abs(x), abs(y), abs(z), abs(w))

    val inv: Vector4 get() = Vector4(1/x, 1/y, 1/z, 1/w)

    val manhattanLength: Double get() = abs(x) + abs(y) + abs(z) + abs(w)

    val sqrLength: Double get() = x*x + y*y + z*z + w*w

    val length: Double get() = sqrt(sqrLength)

    val normalized: Vector4 get() = Vector4(x/length, y/length, z/length, w/length)

    val sign: Vector4 get() = Vector4(sign(x), sign(y), sign(z), sign(w))

    val min: Double get() = min(x, min(y, min(z, w)))

    val max: Double get() = max(x, max(y, max(z, w)))

    fun asString(fmt: String = "%f") = "($fmt, $fmt, $fmt, $fmt)".format(x, y, z, w)
    override fun toString() = asString(DECIMAL_FORMAT)

    companion object {
        val Zero = Vector4(0.0)
        val X = Vector4(1.0, 0.0, 0.0, 0.0)
        val Y = Vector4(0.0, 1.0, 0.0, 0.0)
        val Z = Vector4(0.0, 0.0, 1.0, 0.0)
        val W = Vector4(0.0, 0.0, 0.0, 1.0)
        val One = Vector4(1.0)
    }
}

fun lerp(v1: Vector2, v2: Vector2, t: Double) = Vector2(
    v1.x + t * (v2.x - v1.x),
    v1.y + t * (v2.y - v1.y)
)
fun min(v1: Vector2, v2: Vector2) = Vector2(min(v1.x, v2.x), min(v1.y, v2.y))
fun max(v1: Vector2, v2: Vector2) = Vector2(max(v1.x, v2.x), max(v1.y, v2.y))
fun step(edge: Vector2, v: Vector2) = Vector2(
    if (v.x < edge.x) 0.0 else 1.0,
    if (v.y < edge.y) 0.0 else 1.0
)

fun lerp(v1: Vector3, v2: Vector3, t: Double) = Vector3(
    v1.x + t * (v2.x - v1.x),
    v1.y + t * (v2.y - v1.y),
    v1.z + t * (v2.z - v1.z)
)
fun min(v1: Vector3, v2: Vector3) = Vector3(min(v1.x, v2.x), min(v1.y, v2.y), min(v1.z, v2.z))
fun max(v1: Vector3, v2: Vector3) = Vector3(max(v1.x, v2.x), max(v1.y, v2.y), max(v1.z, v2.z))
fun step(edge: Vector3, v: Vector3) = Vector3(
    if (v.x < edge.x) 0.0 else 1.0,
    if (v.y < edge.y) 0.0 else 1.0,
    if (v.z < edge.z) 0.0 else 1.0
)
fun reflect(v: Vector3, n: Vector3) = v - n * (2 * v.dot(n))

fun lerp(v1: Vector4, v2: Vector4, t: Double) = Vector4(
    v1.x + t * (v2.x - v1.x),
    v1.y + t * (v2.y - v1.y),
    v1.z + t * (v2.z - v1.z),
    v1.w + t * (v2.w - v1.w)
)
fun min(v1: Vector4, v2: Vector4) = Vector4(min(v1.x, v2.x), min(v1.y, v2.y), min(v1.z, v2.z), min(v1.w, v2.w))
fun max(v1: Vector4, v2: Vector4) = Vector4(max(v1.x, v2.x), max(v1.y, v2.y), max(v1.z, v2.z), max(v1.w, v2.w))
