package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.DVec3
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class GizmoSettings(
    val color: TextColor = NamedTextColor.WHITE,
    val duration: Double = 1.0,
)

interface Gizmos<W> {
    fun line(settings: GizmoSettings, world: W, from: DVec3, to: DVec3)
}
