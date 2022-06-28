package com.github.aecsocket.alexandria.core

import org.spongepowered.configurate.objectmapping.ConfigSerializable

sealed interface DoubleMod {
    fun apply(value: Double): Double
}

data class SetDoubleMod(val value: Double) : DoubleMod {
    override fun apply(value: Double) = value
}

@ConfigSerializable
data class OffsetDoubleMod(
    val factor: Double,
    val offset: Double
) : DoubleMod {
    override fun apply(value: Double) = (value * factor) + offset
}


sealed interface IntMod {
    fun apply(value: Int): Int
}

data class SetIntMod(val value: Int) : IntMod {
    override fun apply(value: Int) = value
}

@ConfigSerializable
data class OffsetIntMod(val offset: Int) : IntMod {
    override fun apply(value: Int) = value + offset
}
