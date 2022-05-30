package com.github.aecsocket.alexandria.core

const val PRIORITY_EARLIEST = -200
const val PRIORITY_EARLY = -100
const val PRIORITY_NORMAL = 0
const val PRIORITY_LATE = 100
const val PRIORITY_LATEST = 200
const val PRIORITY_MONITOR = 1000

interface Cancellable {
    var cancelled: Boolean

    fun cancel() {
        cancelled = true
    }
}

class EventDispatcher<E> private constructor(
    private val listeners: List<Listener<E>>
) {
    private data class Listener<E>(
        val priority: Int,
        val runOnCancelled: Boolean,
        val callback: (E) -> Unit
    )

    class Builder<E> {
        private val listeners = ArrayList<Listener<E>>()

        fun addListener(
            priority: Int = PRIORITY_NORMAL,
            runOnCancelled: Boolean = false,
            callback: (E) -> Unit
        ): Builder<E> {
            listeners.add(Listener(priority, runOnCancelled, callback))
            return this
        }

        fun build() = EventDispatcher(listeners.sortedWith(Comparator.comparingInt { it.priority }))
    }

    val size: Int
        get() = listeners.size

    fun <F : E> call(event: F): F {
        listeners.forEach {
            if (event !is Cancellable || !event.cancelled) {
                it.callback(event)
            }
        }
        return event
    }

    companion object {
        fun <E> builder() = Builder<E>()
    }
}
