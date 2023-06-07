package io.github.aecsocket.alexandria.sync

import java.util.function.Consumer

// roughly analogous to the Rust pattern of Mutex<T>
interface Sync<T> {
    fun leak(): T

    fun lock(): T

    fun tryLock(): T?

    fun unlock()

    fun <R> withLock(block: (T) -> R): R

    // Java helper method to not have to return a Unit.INSTANCE
    fun withLock(block: Consumer<T>) = withLock { block.accept(it) }
}
