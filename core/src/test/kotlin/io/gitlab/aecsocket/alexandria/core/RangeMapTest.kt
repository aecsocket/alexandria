package io.gitlab.aecsocket.alexandria.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RangeMapTest {
    @Test
    fun testMap() {
        val map1 = RangeMapFloat(0f, 1f, 2f, 3f)
        assertEquals(2.5f, map1.map(0.5f))

        val map2 = RangeMapFloat(0f, 1f, 0f, -1f)
        assertEquals(-0.25f, map2.map(0.25f))
    }
}
