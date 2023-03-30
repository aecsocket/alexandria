package io.github.aecsocket.alexandria

private fun error(prop: String, message: String): Nothing =
    throw IllegalArgumentException("($prop) assertion failed: $message")

fun <T : Comparable<T>> assertGt(prop: String, expected: T, value: T) {
    if (value <= expected) error(prop, "$value > $expected")
}

fun <T : Comparable<T>> assertGtEq(prop: String, expected: T, value: T) {
    if (value < expected) error(prop, "$value >= $expected")
}

fun <T : Comparable<T>> assertLt(prop: String, expected: T, value: T) {
    if (value >= expected) error(prop, "$value < $expected")
}

fun <T : Comparable<T>> assertLtEq(prop: String, expected: T, value: T) {
    if (value > expected) error(prop, "$value <= $expected")
}
