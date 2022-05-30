package com.github.aecsocket.alexandria.paper.extension

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

fun <Z> PersistentDataContainer.force(key: NamespacedKey, type: PersistentDataType<*, Z>) =
    get(key, type) ?: throw IllegalArgumentException("No value for key $key")

fun <Z> PersistentDataContainer.forEach(type: PersistentDataType<*, Z>, action: (NamespacedKey, Z) -> Unit) {
    keys.forEach { key ->
        action(key, get(key, type)!!)
    }
}
