package com.github.aecsocket.alexandria.core.extension

const val TICKS_PER_SECOND = 20L
const val MS_PER_TICK = 1000L / TICKS_PER_SECOND

val Float.secToTicks: Int
    get() = (this * TICKS_PER_SECOND).toInt()

val Double.secToTicks: Long
    get() = (this * TICKS_PER_SECOND).toLong()
