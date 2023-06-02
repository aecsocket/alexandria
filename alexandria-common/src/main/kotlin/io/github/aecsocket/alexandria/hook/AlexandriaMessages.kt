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

        fun reload(): Message
    }
}
