package io.gitlab.aecsocket.alexandria.paper

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.bossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.Title.title
import org.bukkit.entity.Player

sealed interface TextSlot {
    object InActionBar : TextSlot {
        fun show(player: Player, text: Component) {
            player.sendActionBar(text)
        }
    }

    class InTitle(
        val subtitle: Boolean = false,
        val times: Title.Times? = null,
    ) : TextSlot {
        fun show(player: Player, text: Component) {
            player.showTitle(title(
                if (subtitle) empty() else text,
                if (subtitle) text else empty(),
                times
            ))
        }
    }

    class InBossBar(
        val color: BossBar.Color = BossBar.Color.WHITE,
        val overlay: BossBar.Overlay = BossBar.Overlay.PROGRESS,
    ) : TextSlot {
        fun createBar(
            text: Component = empty(),
            progress: Float = 0f
        ) = bossBar(text, progress, color, overlay)

        fun apply(bar: BossBar, text: Component) {
            bar.name(text)
        }
    }
}
