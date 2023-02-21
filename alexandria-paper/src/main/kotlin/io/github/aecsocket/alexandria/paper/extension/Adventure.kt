package io.github.aecsocket.alexandria.paper.extension

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey

fun Key.namespaced() = NamespacedKey(namespace(), value())
