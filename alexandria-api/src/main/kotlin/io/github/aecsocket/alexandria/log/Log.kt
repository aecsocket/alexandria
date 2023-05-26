package io.github.aecsocket.alexandria.log

import org.slf4j.event.Level

// we roll our own logging interface because I don't like how slf4j interacts with kotlin
// yes, reinventing the wheel is bad, but I prefer having a nice API
// and anyway, our logging is backed by slf4j
data class LogEntry(
    val level: Level,
    val cause: Throwable?,
    private val messageProvider: () -> String,
) {
    val message by lazy(messageProvider)
}

interface Log {
    fun log(entry: LogEntry)
}

fun Log.log(level: Level, cause: Throwable? = null, message: () -> String) {
    log(LogEntry(
        level = level,
        cause = cause,
        messageProvider = message,
    ))
}

fun Log.trace(cause: Throwable? = null, message: () -> String) =
    log(Level.TRACE, cause, message)

fun Log.debug(cause: Throwable? = null, message: () -> String) =
    log(Level.DEBUG, cause, message)

fun Log.info(cause: Throwable? = null, message: () -> String) =
    log(Level.INFO, cause, message)

fun Log.warn(cause: Throwable? = null, message: () -> String) =
    log(Level.WARN, cause, message)

fun Log.error(cause: Throwable? = null, message: () -> String) =
    log(Level.ERROR, cause, message)
