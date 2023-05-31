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
    fun testGenArena() {
        val arena = genArena<Int>()
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
}
