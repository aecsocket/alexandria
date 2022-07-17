package com.gitlab.aecsocket.alexandria.core

import kotlin.test.Test
import kotlin.test.assertEquals

class LoggingTest {
    @Test
    fun testLevels() {
        val ex = IllegalStateException("Exception message", IllegalStateException("Cause")).apply {
            addSuppressed(IllegalStateException("Suppressed"))
        }
        val log = Logging({ println(it) })
        LogLevel.Values.forEach { (name, level) ->
            assertEquals(name, level.name)
            log.line(level, ex) { "Level $name" }
            println()
        }
    }
}
