package io.github.aecsocket.alexandria.sync

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// roughly analogous to the Rust pattern of Mutex<T>
interface Sync<T> {
    fun leak(): T

    fun lock(): T

    fun tryLock(): T?

    fun unlock()

    fun <R> withLock(block: (T) -> R): R
}
