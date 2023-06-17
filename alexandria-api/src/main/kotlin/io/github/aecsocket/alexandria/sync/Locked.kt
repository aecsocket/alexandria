package io.github.aecsocket.alexandria.sync

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class Locked<T>(
    private val value: T,
    private val lock: Lock = ReentrantLock(),
): Sync<T> {
    override fun leak(): T = value

    override fun lock(): T {
        lock.lock()
        return value
    }

    override fun tryLock(): T? {
        return if (lock.tryLock()) value else null
    }

    override fun tryLock(time: Long, unit: TimeUnit): T? {
        return if (lock.tryLock(time, unit)) value else null
    }

    override fun unlock() {
        lock.unlock()
    }

    override fun <R> withLock(block: (T) -> R): R {
        lock()
        val res = try {
            block(value)
        } finally {
            unlock()
        }
        return res
    }
}
