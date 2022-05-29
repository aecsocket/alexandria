package com.github.aecsocket.alexandria.core.extension

import kotlin.math.max
import kotlin.math.min

fun clamp(value: Double, min: Double, max: Double) = min(max, max(min, value))

fun clamp(value: Float, min: Float, max: Float) = min(max, max(min, value))

fun clamp(value: Int, min: Int, max: Int) = min(max, max(min, value))

fun clamp(value: Long, min: Long, max: Long) = min(max, max(min, value))

fun clamp01(value: Double) = clamp(value, 0.0, 1.0)

fun clamp01(value: Float) = clamp(value, 0f, 1f)