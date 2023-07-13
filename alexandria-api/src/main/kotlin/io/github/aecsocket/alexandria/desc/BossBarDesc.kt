package io.github.aecsocket.alexandria.desc

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.spongepowered.configurate.objectmapping.ConfigSerializable

/**
 * Descriptor from which a [BossBar] can be created.
 *
 * @param progress progress of the bar.
 * @param color the bar color.
 * @param overlay The segment overlay of the bar.
 */
@ConfigSerializable
data class BossBarDesc(
    val progress: Float = 1f,
    val color: BossBar.Color = BossBar.Color.WHITE,
    val overlay: BossBar.Overlay = BossBar.Overlay.PROGRESS,
) {
  /** Creates a boss bar with the given descriptor and name. */
  fun create(name: Component): BossBar {
    return BossBar.bossBar(name, progress, color, overlay)
  }
}
