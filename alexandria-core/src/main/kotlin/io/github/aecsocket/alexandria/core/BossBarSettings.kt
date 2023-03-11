package io.github.aecsocket.alexandria.core

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class BossBarSettings(
    val progress: Float = 1f,
    val color: BossBar.Color = BossBar.Color.WHITE,
    val overlay: BossBar.Overlay = BossBar.Overlay.PROGRESS,
) {
    fun create(name: Component) = BossBar.bossBar(name, progress, color, overlay)
}
