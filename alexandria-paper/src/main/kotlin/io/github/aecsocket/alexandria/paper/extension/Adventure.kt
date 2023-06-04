package io.github.aecsocket.alexandria.paper.extension

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.NamespacedKey

fun Key.toNamespaced() = NamespacedKey(namespace(), value())

fun Color.toTextColor() = TextColor.color(asRGB())
fun TextColor.toColor() = Color.fromRGB(value())

