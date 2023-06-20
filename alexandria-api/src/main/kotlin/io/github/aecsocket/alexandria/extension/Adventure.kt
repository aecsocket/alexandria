package io.github.aecsocket.alexandria.extension

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig

/**
 * Appends a `/`-separated path to this key's value.
 */
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

/**
 * Removes common characters which are not rendered properly by the game client.
 */
fun sanitizeText(text: Component) = sanitizeConfigs.fold(text) { acc, config -> acc.replaceText(config) }
