package io.github.aecsocket.alexandria

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestGenArena {
    @Test
    fun testArenaKey() {
        val key = ArenaKey(1, 2)
        assertEquals(1, key.index)
        assertEquals(2, key.gen)

        assertEquals(3, key.index(3).index)
        assertEquals(3, key.gen(3).gen)
    }

    @Test
    fun testInsertRemove() {
        val arena = GenArena<Int>()
        assertEquals(0, arena.size)
        assertTrue(arena.isEmpty())
        assertFalse(arena.isNotEmpty())

        val key = arena.insert(5)
        assertEquals(1, arena.size)
        assertEquals(5, arena[key])

        arena.remove(key)
        assertEquals(0, arena.size)
        assertEquals(null, arena[key])
    }

    @Test
    fun testABA() {
        val arena = GenArena<Int>()
        val keyA = arena.insert(5)
        assertEquals(5, arena[keyA])

        arena.remove(keyA)
        assertEquals(null, arena[keyA])

        val keyB = arena.insert(10)
        assertEquals(10, arena[keyB])
        assertEquals(null, arena[keyA])
    }

    @Test
    fun testCapacity() {
        val arena = GenArena<Int>()
        repeat(1000) {
            arena.insert(it)
        }
        assertEquals(1000, arena.size)

        arena.clear()
        assertEquals(0, arena.size)

        repeat(1000) {
            arena.insert(it)
        }
        assertEquals(1000, arena.size)
    }

    @Test
    fun testIterator() {
        val arena = GenArena<Int>()
        repeat(3) {
            arena.insert(it)
        }
        assertEquals(3, arena.size)
        assertEquals(listOf(
            GenArena.Entry(ArenaKey(0, 0), 0),
            GenArena.Entry(ArenaKey(1, 0), 1),
            GenArena.Entry(ArenaKey(2, 0), 2),
        ), arena.toList())

        arena.remove(ArenaKey(1, 0))
        assertEquals(listOf(
            GenArena.Entry(ArenaKey(0, 0), 0),
            GenArena.Entry(ArenaKey(2, 0), 2),
        ), arena.toList())
    }
}
