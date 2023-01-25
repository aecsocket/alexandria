package io.gitlab.aecsocket.alexandria.core

import io.gitlab.aecsocket.alexandria.core.extension.clamp

data class RangeMapFloat(
    val fromA: Float,
    val fromB: Float,
    val toA: Float,
    val toB: Float,
    val toMin: Float = Float.NEGATIVE_INFINITY,
    val toMax: Float = Float.POSITIVE_INFINITY,
    val reciprocal: Boolean = false,
) {
    private val inRange = fromB - fromA
    private val outRange = toB - toA

    fun map(value: Float): Float {
        val x = if (reciprocal) 1 / value else value
        return clamp(toA + ((x - fromA) / inRange) * outRange, toMin, toMax)
    }

    companion object {
        val Identity = RangeMapFloat(0f, 1f, 0f, 1f)
        val Zero = RangeMapFloat(0f, 1f, 0f, 0f)
    }
}

data class RangeMapDouble(
    val fromA: Double,
    val fromB: Double,
    val toA: Double,
    val toB: Double,
    val toMin: Double = Double.NEGATIVE_INFINITY,
    val toMax: Double = Double.POSITIVE_INFINITY,
    val reciprocal: Boolean = false,
) {
    private val inRange = fromB - fromA
    private val outRange = toB - toA

    fun map(value: Double): Double {
        val x = if (reciprocal) 1 / value else value
        return clamp(toA + ((x - fromA) / inRange) * outRange, toMin, toMax)
    }

    companion object {
        val Identity = RangeMapDouble(0.0, 1.0, 0.0, 1.0)
        val Zero = RangeMapDouble(0.0, 1.0, 0.0, 0.0)
    }
}
