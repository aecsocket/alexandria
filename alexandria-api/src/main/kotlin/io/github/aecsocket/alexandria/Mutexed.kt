package io.github.aecsocket.alexandria

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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

    suspend fun <R> withLock(block: (T) -> R): R {
        return mutex.withLock {
            block(value)
        }
    }

    fun leak(): T = value
}

