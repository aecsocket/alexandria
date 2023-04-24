package io.github.aecsocket.alexandria.paper.extension

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.NamespacedKey

fun Key.asNamespaced() = NamespacedKey(namespace(), value())

fun Key.with(value: String) = Key.key(namespace(), "${value()}/$value")

private val sanitizeConfigs = listOf(
    TextReplacementConfig.builder()
        .matchLiteral("\u00a0") // nbsp
        .replacement(" ")
        .build(),
    TextReplacementConfig.builder()
        .matchLiteral("\u202f") // nnbsp
        .replacement(" ")
        .build(),
)

fun sanitizeText(text: Component) = sanitizeConfigs.fold(text) { acc, config -> acc.replaceText(config) }