package com.gitlab.aecsocket.alexandria.core.extension

import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

const val EPSILON = 0.000001

fun clamp(value: Double, min: Double, max: Double) = min(max, max(min, value))

fun clamp(value: Float, min: Float, max: Float) = min(max, max(min, value))

fun clamp(value: Int, min: Int, max: Int) = min(max, max(min, value))

fun clamp(value: Long, min: Long, max: Long) = min(max, max(min, value))

fun clamp01(value: Double) = clamp(value, 0.0, 1.0)

fun clamp01(value: Float) = clamp(value, 0f, 1f)

val Int.radians get() = this * (PI / 180)
val Long.radians get() = this * (PI / 180)
val Float.radians get() = this * (PI / 180).toFloat()
val Double.radians get() = this * (PI / 180)

val Int.degrees get() = this * (180 / PI)
val Long.degrees get() = this * (180 / PI)
val Float.degrees get() = this * (180 / PI).toFloat()
val Double.degrees get() = this * (180 / PI)
