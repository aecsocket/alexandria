package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.alexandria.core.extension.render
import com.gitlab.aecsocket.glossa.adventure.AnsiComponentRenderer
import net.kyori.adventure.text.Component
import java.util.logging.Level
import java.util.logging.Logger

private const val RESET = "\u001b[0m"

data class LogLevel(
    val name: String,
    val value: Int,
    val prefix: String
) {
    companion object {
        private fun esc(text: String) = "\u001b[${text}m"

        private fun prefix(symbol: String, background: Int, post: Int? = null, foreground: Int = 0) =
            esc("38;5;${foreground};48;5;${background}") + " $symbol " + esc("0${if (post == null) "" else ";38;5;$post"}")

        val Verbose =  LogLevel("verbose", -1, prefix("V", 4, 8))
        val Info =  LogLevel( "info", 0, prefix("I", 2))
        val Warning =  LogLevel( "warning", 1, prefix("W", 3, 3))
        val Error = LogLevel( "error", 2, prefix("E", 1, 1))

        val Values = mapOf(
            Verbose.name to Verbose,
            Info.name to Info,
            Warning.name to Warning,
            Error.name to Error
        )

        fun valueOf(name: String) = Values[name]
            ?: throw IllegalArgumentException("Invalid log level '$name', allowed: ${Values.keys}")
    }
}

data class LogRecord(val level: LogLevel, val ex: Throwable? = null, val lines: () -> List<String>)

interface LogAcceptor {
    fun record(record: LogRecord)

    fun lines(level: LogLevel, ex: Throwable? = null, lines: () -> List<String>) =
        record(LogRecord(level, ex, lines))

    fun line(level: LogLevel, ex: Throwable? = null, line: () -> String) =
        lines(level, ex) { listOf(line()) }

    fun comps(level: LogLevel, ex: Throwable? = null, lines: () -> List<Component>) =
        record(LogRecord(level, ex) { lines().map { AnsiComponentRenderer.render(it) } })

    fun comp(level: LogLevel, ex: Throwable? = null, line: () -> Component) =
        comps(level, ex) { listOf(line()) }
}

private const val THREAD_NAME_SIZE = 10

class Logging(
    val logger: (String) -> Unit,
    var level: LogLevel = LogLevel.Verbose,
    var longStackTraces: Boolean = true,
) : LogAcceptor {
    override fun record(record: LogRecord) {
        if (record.level.value >= this.level.value) {
            (
                record.lines() +
                (record.ex?.render(longStackTraces)?.map { "  ${AnsiComponentRenderer.render(it)}" } ?: emptyList())
            )
                .map { "${record.level.prefix} $it$RESET" }
                .forEach { logger(it) }
        }
    }
}

fun loggingOf(logger: Logger) = Logging({ logger.log(Level.INFO, it) })

class LogList : ArrayList<LogRecord>(), LogAcceptor {
    override fun record(record: LogRecord) {
        add(record)
    }
}
