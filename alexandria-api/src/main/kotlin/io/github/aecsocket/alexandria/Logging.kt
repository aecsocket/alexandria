package io.github.aecsocket.alexandria

import java.util.logging.Level
import java.util.logging.Logger

enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}

data class LogEntry(
    val level: LogLevel,
    val message: String,
    val cause: Throwable?,
)

fun interface Logging {
    fun log(record: LogEntry)
}

fun Logging.log(level: LogLevel, message: String, cause: Throwable? = null) = log(LogEntry(level, message, cause))

fun Logging.trace(message: String, cause: Throwable? = null) = log(LogLevel.TRACE, message, cause)

fun Logging.debug(message: String, cause: Throwable? = null) = log(LogLevel.DEBUG, message, cause)

fun Logging.info(message: String, cause: Throwable? = null) = log(LogLevel.INFO, message, cause)

fun Logging.warn(message: String, cause: Throwable? = null) = log(LogLevel.WARN, message, cause)

fun Logging.error(message: String, cause: Throwable? = null) = log(LogLevel.ERROR, message, cause)

class LoggingList : Logging {
    val entries: MutableList<LogEntry> = ArrayList()

    override fun log(record: LogEntry) {
        entries += record
    }

    fun logTo(logger: Logger) {
        entries.forEach { entry ->
            logger.log(when (entry.level) {
                LogLevel.TRACE -> Level.FINER
                LogLevel.DEBUG -> Level.FINE
                LogLevel.INFO -> Level.INFO
                LogLevel.WARN -> Level.WARNING
                LogLevel.ERROR -> Level.SEVERE
            }, entry.cause) { entry.message }
        }
    }
}
