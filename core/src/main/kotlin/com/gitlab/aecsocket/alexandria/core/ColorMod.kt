package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.alexandria.core.extension.hsb
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import net.kyori.adventure.text.format.TextColor
import org.spongepowered.configurate.objectmapping.ConfigSerializable

sealed interface ColorMod {
    fun apply(color: TextColor): TextColor
}

@ConfigSerializable
data class LerpColorMod(
    val to: TextColor,
    val fac: Float,
) : ColorMod {
    override fun apply(color: TextColor): TextColor {
        return TextColor.lerp(fac, color, to)
    }
}

@ConfigSerializable
data class HSBColorMod(
    val hsb: Vector3
) : ColorMod {
    override fun apply(color: TextColor): TextColor {
        val baseHsb = color.hsb()
        val newHsb = baseHsb + hsb
        return TextColor.color(newHsb.x.toFloat(), newHsb.y.toFloat(), newHsb.z.toFloat())
    }
}
