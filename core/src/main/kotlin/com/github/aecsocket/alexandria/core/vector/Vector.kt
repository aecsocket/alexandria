package com.github.aecsocket.alexandria.core.vector

import com.github.aecsocket.alexandria.core.extension.clamp
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sign
import kotlin.math.sqrt

abstract class Vector<V : Vector<V>> {
    abstract fun map(action: (Double) -> Double): V

    abstract fun sum(action: (Double) -> Double = { it }): Double

    val negated by lazy { map { -it } }

    val abs by lazy { map { abs(it) } }

    val reciprocal by lazy { map { 1 / it } }

    val manhattanLength by lazy { sum { abs(it) } }

    val sqrLength by lazy { sum { it*it } }

    val length by lazy { sqrt(sqrLength) }

    val normalized by lazy { map { it / length } }

    val sign by lazy { map { sign(it) } }

    abstract fun map(other: V, action: (Double, Double) -> Double): V

    operator fun plus(other: V) = map(other) { a, b -> a + b }

    operator fun plus(value: Double) = map { it + value }

    operator fun minus(other: V) = map(other) { a, b -> a - b }

    operator fun minus(value: Double) = map { it - value }

    operator fun times(other: V) = map(other) { a, b -> a * b }

    operator fun times(value: Double) = map { it * value }

    operator fun div(other: V) = map(other) { a, b -> a / b }

    operator fun div(value: Double) = map { it / value }

    fun dot(other: V) = map(other) { a, b -> a * b }.sum()

    fun midpoint(to: V) = map(to) { a, b -> (a + b) / 2 }

    fun angle(to: V) = acos(clamp(dot(to) / (length * to.length), -1.0, 1.0))

    companion object {
        fun <V : Vector<V>> lerp(from: V, to: V, factor: Double) = from.map(to) { a, b -> a + (b - a) * factor }

        fun <V : Vector<V>> min(one: V, two: V) = one.map(two) { a, b -> kotlin.math.min(a, b) }

        fun <V : Vector<V>> max(one: V, two: V) = one.map(two) { a, b -> kotlin.math.max(a, b) }
    }
}

data class Vector2(val x: Double, val y: Double) : Vector<Vector2>() {
    constructor(v: Double) : this(v, v)

    override fun map(action: (Double) -> Double) = Vector2(action(x), action(y))

    override fun sum(action: (Double) -> Double) = action(x) + action(y)

    override fun map(other: Vector2, action: (Double, Double) -> Double) =
        Vector2(action(x, other.x), action(y, other.y))

    override fun toString() = "($x, $y)"

    companion object {
        @JvmStatic val ZERO = Vector2(0.0)
        @JvmStatic val ONE = Vector2(1.0)

        fun lerp(from: Vector2, to: Vector2, factor: Double) = Vector.lerp(from, to, factor)

        fun min(one: Vector2, two: Vector2) = Vector.min(one, two)

        fun max(one: Vector2, two: Vector2) = Vector.max(one, two)
    }
}

data class Vector3(val x: Double, val y: Double, val z: Double) : Vector<Vector3>() {
    constructor(v: Double) : this(v, v, v)

    val xx by lazy { Vector2(x, x) }
    val xy by lazy { Vector2(x, y) }
    val xz by lazy { Vector2(x, z) }

    val yx by lazy { Vector2(y, x) }
    val yy by lazy { Vector2(y, y) }
    val yz by lazy { Vector2(y, z) }

    val zx by lazy { Vector2(z, x) }
    val zy by lazy { Vector2(z, y) }
    val zz by lazy { Vector2(z, z) }

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

    override fun toString() = "($x, $y, $z)"

    companion object {
        @JvmStatic val ZERO = Vector3(0.0)
        @JvmStatic val ONE = Vector3(1.0)

        fun lerp(from: Vector3, to: Vector3, factor: Double) = Vector.lerp(from, to, factor)

        fun min(one: Vector3, two: Vector3) = Vector.min(one, two)

        fun max(one: Vector3, two: Vector3) = Vector.max(one, two)

        fun reflect(vector: Vector3, normal: Vector3) = vector - normal * (2 * vector.dot(normal))
    }
}
