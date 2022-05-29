package com.github.aecsocket.alexandria.core.vector

import com.github.aecsocket.alexandria.core.extension.clamp
import kotlin.math.*

abstract class Point<T : Point<T>> {
    abstract fun map(action: (Int) -> Int): T

    abstract fun sum(action: (Int) -> Int = { it }): Int

    val negated by lazy { map { -it } }

    val abs by lazy { map { abs(it) } }

    abstract fun map(other: T, action: (Int, Int) -> Int): T

    operator fun plus(other: T) = map(other) { a, b -> a + b }

    operator fun plus(value: Int) = map { it + value }

    operator fun minus(other: T) = map(other) { a, b -> a - b }

    operator fun minus(value: Int) = map { it - value }

    operator fun times(other: T) = map(other) { a, b -> a * b }

    operator fun times(value: Int) = map { it * value }

    operator fun div(other: T) = map(other) { a, b -> a / b }

    operator fun div(value: Int) = map { it / value }

    operator fun unaryMinus() = negated

    fun dot(other: T) = map(other) { a, b -> a * b }.sum()

    companion object {
        fun <V : Point<V>> min(one: V, two: V) = one.map(two) { a, b -> min(a, b) }

        fun <V : Point<V>> max(one: V, two: V) = one.map(two) { a, b -> max(a, b) }
    }
}

data class Point2(val x: Int, val y: Int) : Point<Point2>() {
    constructor(v: Int) : this(v, v)

    override fun map(action: (Int) -> Int) = Point2(action(x), action(y))

    override fun sum(action: (Int) -> Int) = action(x) + action(y)

    override fun map(other: Point2, action: (Int, Int) -> Int) =
        Point2(action(x, other.x), action(y, other.y))

    val maxComponent by lazy { max(x, y) }

    val minComponent by lazy { min(x, y) }

    val vector by lazy { Vector2(x.toDouble(), y.toDouble()) }

    fun x(x: Int) = Point2(x, y)

    fun y(y: Int) = Point2(x, y)

    override fun toString() = "($x, $y)"

    companion object {
        val ZERO = Point2(0)
        val ONE = Point2(1)

        fun min(one: Point2, two: Point2) = Point.min(one, two)

        fun max(one: Point2, two: Point2) = Point.max(one, two)
    }
}

data class Point3(val x: Int, val y: Int, val z: Int) : Point<Point3>() {
    constructor(v: Int) : this(v, v, v)

    val xy by lazy { Point2(x, y) }
    val xz by lazy { Point2(x, z) }

    val yx by lazy { Point2(y, x) }
    val yz by lazy { Point2(y, z) }

    val zx by lazy { Point2(z, x) }
    val zy by lazy { Point2(z, y) }

    val xzy by lazy { Point3(x, z, y) }

    val yxz by lazy { Point3(y, x, z) }
    val yzx by lazy { Point3(y, z, x) }

    val zxy by lazy { Point3(z, x, y) }
    val zyx by lazy { Point3(z, y, x) }

    override fun map(action: (Int) -> Int) = Point3(action(x), action(y), action(z))

    override fun sum(action: (Int) -> Int) = action(x) + action(y) + action(z)

    override fun map(other: Point3, action: (Int, Int) -> Int) =
        Point3(action(x, other.x), action(y, other.y), action(z, other.z))

    val maxComponent by lazy { max(x, max(y, z)) }

    val minComponent by lazy { min(x, min(y, z)) }

    val vector by lazy { Vector3(x.toDouble(), y.toDouble(), z.toDouble()) }

    fun x(x: Int) = Point3(x, y, z)

    fun y(y: Int) = Point3(x, y, z)

    fun z(z: Int) = Point3(x, y, z)

    override fun toString() = "($x, $y, $z)"

    companion object {
        val ZERO = Point3(0)
        val ONE = Point3(1)

        fun min(one: Point3, two: Point3) = Point.min(one, two)

        fun max(one: Point3, two: Point3) = Point.max(one, two)
    }
}
