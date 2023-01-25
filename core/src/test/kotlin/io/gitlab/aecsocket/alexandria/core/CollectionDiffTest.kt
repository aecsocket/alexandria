package io.gitlab.aecsocket.alexandria.core

import io.gitlab.aecsocket.alexandria.core.extension.CollectionDiff
import io.gitlab.aecsocket.alexandria.core.extension.diff
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CollectionDiffTest {
    @Test
    fun testIdentical() {
        assertEquals(CollectionDiff<Int>(
            emptySet(), emptySet()
        ), diff(
            emptySet<Int>(),
            emptySet()
        ))

        assertEquals(CollectionDiff<Int>(
            emptySet(), emptySet()
        ), diff(
            setOf(1, 2, 3),
            setOf(1, 2, 3)
        ))
    }

    @Test
    fun testAdded() {
        assertEquals(CollectionDiff(
            setOf(3, 4), emptySet()
        ), diff(
            setOf(1, 2),
            setOf(1, 2, 3, 4)
        ))
    }

    @Test
    fun testRemoved() {
        assertEquals(CollectionDiff(
            emptySet(), setOf(3, 4)
        ), diff(
            setOf(1, 2, 3, 4),
            setOf(1, 2)
        ))
    }

    @Test
    fun testAddedAndRemoved() {
        assertEquals(CollectionDiff(
            setOf(20, 21), setOf(10, 11)
        ), diff(
            setOf(1, 2, 3, 10, 11),
            setOf(1, 2, 3, 20, 21)
        ))
    }
}
