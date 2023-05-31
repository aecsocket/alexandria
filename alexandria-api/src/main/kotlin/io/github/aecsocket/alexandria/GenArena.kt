package io.github.aecsocket.alexandria

import kotlin.math.max

/**
 * A key used in a [genArena], which is a wrapper around a [Long].
 *
 * The upper 32 bits represent the [index], and the lower 32 bits represent the [gen].
 */
@JvmInline
value class ArenaKey(val key: Long) {
    constructor(index: Int, gen: Int) : this((index.toLong() shl 32) or gen.toLong())

    val index: Int
        get() = (key shr 32).toInt()

    val gen: Int
        get() = key.toInt()

    fun index(index: Int) = ArenaKey((key and 0x00000000_ffffffffu.toLong()) or (index.toLong() shl 32))

    fun gen(gen: Int) = ArenaKey((key and 0xffffffff_00000000u.toLong()) or gen.toLong())

    override fun toString() = "$index/$gen"
}

/**
 * A generational arena implementation, where elements are indexed by an index + generation pair.
 *
 * This is effectively a port of Rapier's Rust implementation of Arena -
 * see [arena.rs](https://github.com/dimforge/rapier/blob/master/src/data/arena.rs).
 */
interface GenArena<out E> : Iterable<GenArena.Entry<out E>> {
    data class Entry<E>(
        val key: ArenaKey,
        val value: E,
    )

    val size: Int

    fun isEmpty(): Boolean

    operator fun contains(key: ArenaKey): Boolean

    operator fun get(key: ArenaKey): E?
}

fun <E> GenArena<E>.isNotEmpty() = !isEmpty()

interface MutableGenArena<E> : GenArena<E> {
    fun insert(value: E): ArenaKey

    fun remove(key: ArenaKey): E?

    fun clear()
}

fun <E> genArena(capacity: Int = 4): MutableGenArena<E> = GenArenaImpl(capacity)

class GenArenaImpl<E> internal constructor(capacity: Int) : MutableGenArena<E> {
    private sealed interface Item<T> {
        data class Free<T>(val nextFree: Int?) : Item<T>

        data class Occupied<T>(
            val gen: Int,
            val value: T,
        ) : Item<T>
    }

    private val items = ArrayList<Item<E>>()
    private var freeListHead: Int? = null
    private var gen = 0
    override var size = 0

    init {
        reserve(capacity)
    }

    override fun isEmpty() = size == 0

    override fun contains(key: ArenaKey) = get(key) != null

    override fun get(key: ArenaKey): E? {
        when (val item = items[key.index]) {
            is Item.Free -> return null
            is Item.Occupied -> {
                if (key.gen != item.gen) return null
                return item.value
            }
        }
    }

    override fun iterator() = object : Iterator<GenArena.Entry<out E>> {
        private val iter = items.iterator().withIndex()
        private var next: GenArena.Entry<out E>? = compute()

        fun compute(): GenArena.Entry<out E>? {
            if (!iter.hasNext()) return null

            while (true) {
                val (index, entry) = iter.next()
                when (entry) {
                    is Item.Free -> continue
                    is Item.Occupied -> {
                        return GenArena.Entry(
                            key = ArenaKey(index, entry.gen),
                            value = entry.value,
                        )
                    }
                }
            }
        }

        override fun hasNext() = next != null

        override fun next(): GenArena.Entry<out E> {
            val next = next ?: throw NoSuchElementException()
            this.next = compute()
            return next
        }
    }

    override fun insert(value: E): ArenaKey {
        return tryInsert(value) ?: insertSlowPath(value)
    }

    override fun remove(key: ArenaKey): E? {
        if (key.index > items.size) return null

        when (val item = items[key.index]) {
            is Item.Free -> return null
            is Item.Occupied -> {
                if (key.gen != item.gen) return null
                val value = item.value
                items[key.index] = Item.Free(
                    nextFree = freeListHead,
                )
                gen += 1
                freeListHead = key.index
                size -= 1
                return value
            }
        }
    }

    override fun clear() {
        val end = items.size
        (0 until end).forEach { i ->
            items[i] = Item.Free(
                nextFree = if (i == end - 1) null else i + 1,
            )
        }
        freeListHead = 0
        size = 0
    }

    fun reserve(upTo: Int) {
        val start = items.size
        val end = max(start, upTo)
        val oldHead = freeListHead
        items.ensureCapacity(upTo)
        (0 until end).map { i ->
            items.add(Item.Free(
                nextFree = if (i == end - 1) oldHead else i + 1,
            ))
        }
        freeListHead = start
    }

    fun tryInsert(value: E): ArenaKey? {
        return tryAllocNextIndex()?.let { index ->
            items[index.index] = Item.Occupied(
                gen = gen,
                value = value,
            )
            index
        }
    }

    private fun insertSlowPath(value: E): ArenaKey {
        items.ensureCapacity(size * 2)
        return tryInsert(value)
            ?: throw IllegalStateException("Add will always succeed after reserving additional space")
    }

    private fun tryAllocNextIndex(): ArenaKey? {
        val freeIndex = freeListHead ?: return null
        when (val item = items[freeIndex]) {
            is Item.Occupied -> throw IllegalStateException("Corrupt free list")
            is Item.Free -> {
                freeListHead = item.nextFree
                size += 1
                return ArenaKey(freeIndex, gen)
            }
        }
    }
}