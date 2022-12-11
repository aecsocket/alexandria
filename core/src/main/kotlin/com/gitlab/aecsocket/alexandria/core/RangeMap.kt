package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.alexandria.core.extension.clamp

data class RangeMapFloat(
    val inFrom: Float,
    val inTo: Float,
    val outFrom: Float,
    val outTo: Float,
    val outMin: Float = Float.NEGATIVE_INFINITY,
    val outMax: Float = Float.POSITIVE_INFINITY,
) {
    private val inRange = inTo - inFrom
    private val outRange = outTo - outFrom

    fun map(value: Float) = clamp(outFrom + ((value - inFrom) / inRange) * outRange, outMin, outMax)

    companion object {
        val Identity = RangeMapFloat(0f, 1f, 0f, 1f)
        val Zero = RangeMapFloat(0f, 1f, 0f, 0f)
    }
}

data class RangeMapDouble(
    val inFrom: Double,
    val inTo: Double,
    val outFrom: Double,
    val outTo: Double,
    val outMin: Double = Double.NEGATIVE_INFINITY,
    val outMax: Double = Double.POSITIVE_INFINITY,
) {
    private val inRange = inTo - inFrom
    private val outRange = outTo - outFrom

    fun map(value: Double) = clamp(outFrom + ((value - inFrom) / inRange) * outRange, outMin, outMax)

    companion object {
        val Identity = RangeMapDouble(0.0, 1.0, 0.0, 1.0)
        val Zero = RangeMapDouble(0.0, 1.0, 0.0, 0.0)
    }
}
