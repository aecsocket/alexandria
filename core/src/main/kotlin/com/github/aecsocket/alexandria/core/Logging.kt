package com.github.aecsocket.alexandria.core

import net.kyori.adventure.text.Component
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Level
import java.util.logging.Logger

private const val RESET = "\u001b[0m"

data class LogLevel(
    val name: String,
    val value: Int,
    val prefix: String
) {
    companion object {
        @JvmStatic
        fun escape(text: String) = "\u001b[${text}m"

        @JvmStatic
        fun prefix(name: String, background: Int, post: Int? = null) =
            escape("30;4${background}") + " $name " + escape("0${if (post == null) "" else ";3$post"}")

        @JvmStatic val VERBOSE =  LogLevel("verbose", -1, prefix("VRB", 4))
        @JvmStatic val INFO =  LogLevel( "info", 0, prefix("INF", 2))
        @JvmStatic val WARNING =  LogLevel( "warning", 1, prefix("WRN", 3, 3))
        @JvmStatic val ERROR = LogLevel( "error", 2, prefix("ERR", 1, 1))

        @JvmStatic val VALUES = mapOf(
            VERBOSE.name to VERBOSE,
            INFO.name to INFO,
            WARNING.name to WARNING,
            ERROR.name to ERROR
        )

        @JvmStatic
        fun valueOf(name: String) = VALUES[name]
            ?: throw IllegalArgumentException("Invalid log level '$name', allowed: ${VALUES.keys}")
    }
}

fun interface ExceptionLogStrategy {
    fun format(ex: Throwable): List<String>

    companion object {
        @JvmStatic
        val COMPACT = ExceptionLogStrategy { ex ->
            ex.message?.let { listOf(it) } ?: emptyList()
        }

        @JvmStatic
        val SIMPLE = ExceptionLogStrategy { ex ->
            fun lines(ex: Throwable): List<String> {
                val type = ex.javaClass.simpleName
                val lines = ex.message?.split('\n') ?: emptyList()
                return (if (lines.isEmpty()) listOf(type) else {
                    val prefix = "$type: "
                    val padding = " ".repeat(prefix.length)
                    lines.mapIndexed { idx, line ->
                        (if (idx == 0) prefix else padding) + line
                    }
                }) +
                    (ex.cause?.let { lines(it) } ?: emptyList())
            }
            lines(ex)
        }

        @JvmStatic
        val FULL = ExceptionLogStrategy { ex ->
            StringWriter().apply { ex.printStackTrace(PrintWriter(this)) }.buffer
                .toString().split('\n').filter { it.isNotEmpty() }
        }

        @JvmStatic val VALUES = mapOf(
            "compact" to COMPACT,
            "simple" to SIMPLE,
            "full" to FULL
        )

        @JvmStatic
        fun valueOf(name: String) = VALUES[name]
            ?: throw IllegalArgumentException("Invalid exception log strategy '$name', allowed: ${VALUES.keys}")
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
        lines(level, ex) { lines().map { AnsiComponentRenderer.render(it) } }

    fun comp(level: LogLevel, ex: Throwable? = null, line: () -> Component) =
        comps(level, ex) { listOf(line()) }
}

class Logging(
    val logger: Logger,
    var level: LogLevel = LogLevel.VERBOSE,
    var exceptionStrategy: ExceptionLogStrategy = ExceptionLogStrategy.FULL
) : LogAcceptor {
    override fun record(record: LogRecord) {
        if (record.level.value >= this.level.value) {
            (record.lines() + (record.ex?.let { exceptionStrategy.format(record.ex).map { "  $it" } } ?: emptyList()))
                .map { "${record.level.prefix} $it$RESET" }
                .forEach { logger.log(Level.INFO, it) }
        }
    }
}

class LogList : ArrayList<LogRecord>(), LogAcceptor {
    override fun record(record: LogRecord) {
        add(record)
    }
}
