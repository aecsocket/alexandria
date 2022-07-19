package com.gitlab.aecsocket.alexandria.core.keyed

import org.intellij.lang.annotations.Pattern

interface Keyed {
    @get:Pattern("[a-z0-9_]")
    val id: String

    class ValidationException(key: String, index: Int, char: Char)
        : RuntimeException("Invalid character in '$key' at position ${index+1} '$char', allowed: [$CHARACTERS]")

    companion object {
        const val CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789_"

        fun validate(key: String): String {
            val idx = key.indexOfFirst { !CHARACTERS.contains(it) }
            if (idx != -1)
                throw ValidationException(key, idx, key[idx])
            return key
        }
    }
}

fun <K> Map<String, *>.by(keyed: Keyed): K? {
    return get(keyed.id)?.let {
        @Suppress("UNCHECKED_CAST")
        it as K
    }
}
