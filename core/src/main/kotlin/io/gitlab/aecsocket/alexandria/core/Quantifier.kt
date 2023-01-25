package io.gitlab.aecsocket.alexandria.core

data class Quantifier<T>(
    val obj: T,
    val amount: Int,
) {
    init {
        check(amount > 0) { "Amount must be greater than 0" }
    }
}

fun Iterable<Quantifier<*>>.sum() = sumOf { it.amount }
