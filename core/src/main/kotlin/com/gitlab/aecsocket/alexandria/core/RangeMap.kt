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
    }
}
