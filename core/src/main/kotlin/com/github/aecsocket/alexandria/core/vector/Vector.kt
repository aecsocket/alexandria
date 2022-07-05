package com.github.aecsocket.alexandria.core.vector

import com.github.aecsocket.alexandria.core.extension.clamp
import kotlin.math.*

private fun sqr(v: Double) = v*v

abstract class Vector<T : Vector<T>> {
    abstract fun map(action: (Double) -> Double): T

    abstract fun sum(action: (Double) -> Double = { it }): Double

    val negated by lazy { map { -it } }

    val abs by lazy { map { abs(it) } }

    val reciprocal by lazy { map { 1 / it } }

    val manhattanLength by lazy { sum { abs(it) } }

    val sqrLength by lazy { sum(::sqr) }

    val length by lazy { sqrt(sqrLength) }

    val normalized by lazy { map { it / length } }

    val sign by lazy { map { sign(it) } }

    abstract fun map(other: T, action: (Double, Double) -> Double): T

    operator fun plus(other: T) = map(other) { a, b -> a + b }

    operator fun plus(value: Double) = map { it + value }

    operator fun minus(other: T) = map(other) { a, b -> a - b }

    operator fun minus(value: Double) = map { it - value }

    operator fun times(other: T) = map(other) { a, b -> a * b }

    operator fun times(value: Double) = map { it * value }

    operator fun div(other: T) = map(other) { a, b -> a / b }

    operator fun div(value: Double) = map { it / value }

    operator fun unaryMinus() = negated

    fun sqrDistance(other: T) = map(other) { a, b -> sqr(a - b) }.sum()

    fun distance(other: T) = sqrt(sqrDistance(other))

    fun dot(other: T) = map(other) { a, b -> a * b }.sum()

    fun midpoint(to: T) = map(to) { a, b -> (a + b) / 2 }

    fun angle(to: T) = acos(clamp(dot(to) / (length * to.length), -1.0, 1.0))

    fun step(other: T) = map(other) { a, b -> if (b < a) 0.0 else 1.0 }

    companion object {
        fun <V : Vector<V>> lerp(from: V, to: V, factor: Double) = from.map(to) { a, b -> a + (b - a) * factor }

        fun <V : Vector<V>> min(one: V, two: V) = one.map(two) { a, b -> min(a, b) }

        fun <V : Vector<V>> max(one: V, two: V) = one.map(two) { a, b -> max(a, b) }
    }
}

data class Vector2(val x: Double, val y: Double) : Vector<Vector2>() {
    constructor(v: Double) : this(v, v)

    override fun map(action: (Double) -> Double) = Vector2(action(x), action(y))

    override fun sum(action: (Double) -> Double) = action(x) + action(y)

    override fun map(other: Vector2, action: (Double, Double) -> Double) =
        Vector2(action(x, other.x), action(y, other.y))

    val maxComponent by lazy { max(x, y) }

    val minComponent by lazy { min(x, y) }

    val angle by lazy { atan2(y, x) }

    val polar by lazy { polar(length) }

    val point by lazy { Point2(x.toInt(), y.toInt()) }

    fun x(x: Double) = Vector2(x, y)

    fun y(y: Double) = Vector2(x, y)

    fun polar(radius: Double) = Polar2(radius, angle)

    override fun toString() = "($x, $y)"

    companion object {
        val ZERO = Vector2(0.0)
        val ONE = Vector2(1.0)

        fun lerp(from: Vector2, to: Vector2, factor: Double) = Vector.lerp(from, to, factor)

        fun min(one: Vector2, two: Vector2) = Vector.min(one, two)

        fun max(one: Vector2, two: Vector2) = Vector.max(one, two)
    }
}

data class Vector3(val x: Double, val y: Double, val z: Double) : Vector<Vector3>() {
    constructor(v: Double) : this(v, v, v)

    val xy by lazy { Vector2(x, y) }
    val xz by lazy { Vector2(x, z) }

    val yx by lazy { Vector2(y, x) }
    val yz by lazy { Vector2(y, z) }

    val zx by lazy { Vector2(z, x) }
    val zy by lazy { Vector2(z, y) }

    val xzy by lazy { Vector3(x, z, y) }

    val yxz by lazy { Vector3(y, x, z) }
    val yzx by lazy { Vector3(y, z, x) }

    val zxy by lazy { Vector3(z, x, y) }
    val zyx by lazy { Vector3(z, y, x) }

    val r = x
    val g = y
    val b = z

    val ir by lazy { (r * 255).toInt() }
    val ig by lazy { (g * 255).toInt() }
    val ib by lazy { (b * 255).toInt() }

    val rgb by lazy { (ir and 0xff shl 16) or
        (ig and 0xff shl 8) or
        (ib and 0xff) }

    override fun map(action: (Double) -> Double) = Vector3(action(x), action(y), action(z))

    override fun sum(action: (Double) -> Double) = action(x) + action(y) + action(z)

    override fun map(other: Vector3, action: (Double, Double) -> Double) =
        Vector3(action(x, other.x), action(y, other.y), action(z, other.z))

    val maxComponent by lazy { max(x, max(y, z)) }

    val minComponent by lazy { min(x, min(y, z)) }

    val yaw by lazy { atan2(-x, z) + PI * 2 }

    val pitch by lazy { atan(-y / sqrt(sqr(x) + sqr(z))) }

    val polar by lazy { polar(length) }

    val point by lazy { Point3(x.toInt(), y.toInt(), z.toInt()) }

    fun x(x: Double) = Vector3(x, y, z)

    fun y(y: Double) = Vector3(x, y, z)

    fun z(z: Double) = Vector3(x, y, z)

    fun polar(radius: Double) = Polar3(radius, yaw, pitch)

    fun cross(other: Vector3) = Vector3(
        y * other.z - other.y * z,
        z * other.x - other.z * x,
        x * other.y - other.x * y
    )

    fun rotateX(angle: Double) = Vector3(
        x,
        cos(angle) * y - sin(angle) * z,
        sin(angle) * y + cos(angle) * z
    )

    fun rotateY(angle: Double) = Vector3(
        cos(angle) * x + sin(angle) * z,
        y,
        -sin(angle) * x + cos(angle) * z
    )

    fun rotateZ(angle: Double) = Vector3(
        cos(angle) * x - sin(angle) * y,
        sin(angle) * x + cos(angle) * y,
        z
    )

    fun rotate(axis: Vector3, angle: Double): Vector3 {
        val cos = cos(angle)
        val sin = sin(angle)
        val dot = dot(axis)
        return Vector3(
            axis.x * dot * (1 - cos) + x * cos + (-axis.z * y + axis.y * z) * sin,
            axis.y * dot * (1 - cos) + y * cos + (axis.z * x - axis.x * z) * sin,
            axis.z * dot * (1 - cos) + z * cos + (-axis.y * x + axis.x * y) * sin
        )
    }

    override fun toString() = "($x, $y, $z)"

    companion object {
        val Zero = Vector3(0.0)
        val One = Vector3(1.0)

        fun lerp(from: Vector3, to: Vector3, factor: Double) = Vector.lerp(from, to, factor)

        fun min(one: Vector3, two: Vector3) = Vector.min(one, two)

        fun max(one: Vector3, two: Vector3) = Vector.max(one, two)

        fun reflect(vector: Vector3, normal: Vector3) = vector - normal * (2 * vector.dot(normal))
    }
}
