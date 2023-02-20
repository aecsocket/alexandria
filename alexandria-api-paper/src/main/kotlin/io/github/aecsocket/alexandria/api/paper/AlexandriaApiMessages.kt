package io.github.aecsocket.alexandria.api.paper

import io.github.aecsocket.glossa.core.Message
import io.github.aecsocket.glossa.core.MessageKey
import io.github.aecsocket.glossa.core.SectionKey
import net.kyori.adventure.text.Component

interface AlexandriaApiMessages {
    @SectionKey
    val command: Command
    interface Command {
        @MessageKey
        fun about(
            pluginName: Component,
            version: String,
            authors: String,
        ): Message

        @SectionKey
        val reload: Reload
        interface Reload {
            @MessageKey
            fun start(): Message
            @MessageKey
            fun stop(
                numMessages: Int
            ): Message

            @SectionKey
            val log: Log
            interface Log {
                @MessageKey
                fun trace(message: String): Message
                @MessageKey
                fun debug(message: String): Message
                @MessageKey
                fun info(message: String): Message
                @MessageKey
                fun warn(message: String): Message
                @MessageKey
                fun error(message: String): Message
            }
        }
    }
}
