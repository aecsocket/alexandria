package io.github.aecsocket.alexandria

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// roughly analogous to the Rust pattern of Mutex<T>
class Synchronized<T>(private val value: T) {
    @OptIn(ExperimentalContracts::class)
    fun <R> synchronized(block: (T) -> R): R {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return synchronized(this) { block(value) }
    }

    fun leak(): T = value
}

