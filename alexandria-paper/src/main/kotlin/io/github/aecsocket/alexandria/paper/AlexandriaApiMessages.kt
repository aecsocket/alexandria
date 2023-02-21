package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.glossa.core.Message
import net.kyori.adventure.text.Component

interface AlexandriaApiMessages {
    val command: Command
    interface Command {
        fun about(
            pluginName: Component,
            version: String,
            authors: String,
        ): Message

        val reload: Reload
        interface Reload {
            fun start(): Message
            fun stop(
                numMessages: Int
            ): Message

            val log: Log
            interface Log {
                fun trace(message: String): Message
                fun debug(message: String): Message
                fun info(message: String): Message
                fun warn(message: String): Message
                fun error(message: String): Message
            }
        }
    }
}
