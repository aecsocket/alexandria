package io.github.aecsocket.alexandria.api.paper.extension

import net.kyori.adventure.audience.Audience
import org.bukkit.entity.Player
import java.util.Locale

fun Audience.locale(defaultLocale: Locale) = when (this) {
    is Player -> locale()
    else -> defaultLocale
}
