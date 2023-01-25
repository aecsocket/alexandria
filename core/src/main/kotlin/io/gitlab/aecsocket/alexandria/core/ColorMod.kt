package io.gitlab.aecsocket.alexandria.core

import io.gitlab.aecsocket.alexandria.core.extension.hsb
import io.gitlab.aecsocket.alexandria.core.physics.Vector3
import net.kyori.adventure.text.format.TextColor
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

sealed interface ColorMod {
    fun apply(color: TextColor): TextColor
}

@ConfigSerializable
data class LerpColorMod(
    @Required val to: TextColor,
    @Required val fac: Float,
) : ColorMod {
    override fun apply(color: TextColor): TextColor {
        return TextColor.lerp(fac, color, to)
    }
}

@ConfigSerializable
data class HSBColorMod(
    @Required val hsb: Vector3
) : ColorMod {
    override fun apply(color: TextColor): TextColor {
        val baseHsb = color.hsb()
        val newHsb = baseHsb + hsb
        return TextColor.color(newHsb.x.toFloat(), newHsb.y.toFloat(), newHsb.z.toFloat())
    }
}
