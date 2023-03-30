package io.github.aecsocket.alexandria.paper.extension

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey

fun Key.asNamespaced() = NamespacedKey(namespace(), value())

fun Key.with(value: String) = Key.key(namespace(), "${value()}/$value")
