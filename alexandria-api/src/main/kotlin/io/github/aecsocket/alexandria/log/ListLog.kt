package io.github.aecsocket.alexandria.log

class ListLog : Log {
    val entries = ArrayList<LogEntry>()

    override fun log(entry: LogEntry) {
        entries += entry
    }

    fun logTo(dst: Log) {
        entries.forEach { entry ->
            dst.log(entry)
        }
    }
}
