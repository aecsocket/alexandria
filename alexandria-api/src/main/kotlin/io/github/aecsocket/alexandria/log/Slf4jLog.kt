package io.github.aecsocket.alexandria.log

import org.slf4j.Logger
import org.slf4j.event.Level

class Slf4jLog(
    val backing: Logger,
) : Log {
    override fun log(entry: LogEntry) {
        when (entry.level) {
            Level.TRACE -> backing.trace(entry.message, entry.cause)
            Level.DEBUG -> backing.debug(entry.message, entry.cause)
            Level.INFO -> backing.info(entry.message, entry.cause)
            Level.WARN -> backing.warn(entry.message, entry.cause)
            Level.ERROR -> backing.error(entry.message, entry.cause)
        }
    }
}