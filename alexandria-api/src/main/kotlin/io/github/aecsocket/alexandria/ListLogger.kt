package io.github.aecsocket.alexandria

import java.util.logging.LogRecord
import java.util.logging.Logger

class ListLogger : Logger(null, null) {
    val records = ArrayList<LogRecord>()

    override fun log(record: LogRecord) {
        if (!isLoggable(record.level))
            return
        records += record
    }

    fun logTo(logger: Logger) {
        records.forEach { record ->
            logger.log(record)
        }
    }
}
