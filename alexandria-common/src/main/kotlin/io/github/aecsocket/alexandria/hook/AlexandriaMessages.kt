package io.github.aecsocket.alexandria.hook

import io.github.aecsocket.glossa.Message
import net.kyori.adventure.text.Component

interface AlexandriaMessages {
    val error: Error
    interface Error {
        val sender: Sender
        interface Sender {
            fun mustBePlayer(): Message
        }
    }

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
