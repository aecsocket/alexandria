package com.gitlab.aecsocket.alexandria.paper.extension

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

fun <Z> PersistentDataContainer.force(key: NamespacedKey, type: PersistentDataType<*, Z>) =
    get(key, type) ?: throw IllegalArgumentException("No value for key $key")

fun <Z> PersistentDataContainer.forEach(type: PersistentDataType<*, Z>, action: (NamespacedKey, Z) -> Unit) {
    keys.forEach { key ->
        action(key, get(key, type)!!)
    }
}

fun PersistentDataContainer.setBoolean(key: NamespacedKey, value: Boolean) {
    set(key, PersistentDataType.BYTE, if (value) 1.toByte() else 0.toByte())
}

fun PersistentDataContainer.getBoolean(key: NamespacedKey) =
    get(key, PersistentDataType.BYTE)?.let { it == 1.toByte() } ?: false
