package io.gitlab.aecsocket.alexandria.core.keyed

import net.kyori.adventure.key.InvalidKeyException
import net.kyori.adventure.key.Key
import org.intellij.lang.annotations.Pattern
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import java.lang.reflect.Type

interface Keyed {
    @get:Pattern("[a-z0-9_]")
    val id: String

    companion object {
        const val CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789_"

        fun validate(key: String): String {
            val idx = key.indexOfFirst { !CHARACTERS.contains(it) }
            if (idx != -1)
                throw AlexandriaKeyValidationException(key, idx, key[idx])
            return key
        }
    }
}

class AlexandriaKeyValidationException(key: String, index: Int, char: Char)
    : RuntimeException("Invalid character in '$key' at position ${index+1} '$char', allowed: [${Keyed.CHARACTERS}]")

fun parseNodeAlexandriaKey(type: Type, node: ConfigurationNode): String {
    return try {
        Keyed.validate(node.key().toString())
    } catch (ex: AlexandriaKeyValidationException) {
        throw SerializationException(node, type, "Invalid key", ex)
    }
}

fun parseNodeNamespacedKey(type: Type, node: ConfigurationNode): Key {
    return try {
        Key.key(node.key().toString())
    } catch (ex: InvalidKeyException) {
        throw SerializationException(node, type, "Invalid namespaced key", ex)
    }
}
