package io.github.aecsocket.alexandria

val kebabCasePattern = Regex("([a-z0-9-])+")
val snakeCasePattern = Regex("([a-z0-9_])+")

class KeyValidationException(
    key: String,
    pattern: Regex
) : RuntimeException("Invalid key '$key', must match ${pattern.pattern}")

fun validateKey(key: String, pattern: Regex): String {
    if (!pattern.matches(key))
        throw KeyValidationException(key, pattern)
    return key
}
