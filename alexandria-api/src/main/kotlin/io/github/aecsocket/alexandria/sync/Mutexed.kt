package io.github.aecsocket.alexandria.sync

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// roughly analogous to the Rust pattern of Mutex<T>
class Mutexed<T>(private val value: T) {
    private val mutex = Mutex()

    suspend fun lock(owner: Any? = null): T {
        mutex.lock(owner)
        return value
    }

    fun unlock(owner: Any? = null) {
        mutex.unlock(owner)
    }

    @OptIn(ExperimentalContracts::class)
    suspend fun <R> withLock(owner: Any? = null, block: suspend (T) -> R): R {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        return mutex.withLock(owner) {
            block(value)
        }
    }

    fun leak(): T = value
}

