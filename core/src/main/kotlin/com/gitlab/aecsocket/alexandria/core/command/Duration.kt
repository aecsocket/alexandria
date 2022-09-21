package com.gitlab.aecsocket.alexandria.core.command

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.captions.Caption
import cloud.commandframework.captions.CaptionVariable
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import cloud.commandframework.exceptions.parsing.ParserException
import com.gitlab.aecsocket.alexandria.core.serializer.WDuration
import com.gitlab.aecsocket.alexandria.core.serializer.wrap
import java.util.*
import kotlin.time.Duration

class DurationFormatException(
    context: CommandContext<*>,
    input: String,
    error: Throwable,
) : ParserException(
    DurationParser::class.java, context, DurationParser.ARGUMENT_PARSE_FAILURE_DURATION,
    CaptionVariable.of("input", input),
    CaptionVariable.of("error", error.message ?: "(no message)")
)

class DurationParser<C : Any> : ArgumentParser<C, WDuration> {
    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<WDuration> {
        return inputQueue.peek()?.let { input ->
            inputQueue.remove()
            try {
                ArgumentParseResult.success(Duration.parse(input).wrap())
            } catch (ex: IllegalArgumentException) {
                ArgumentParseResult.failure(DurationFormatException(
                    commandContext, input, ex
                ))
            }
        } ?: ArgumentParseResult.failure(NoInputProvidedException(
            DurationParser::class.java,
            commandContext
        ))
    }

    companion object {
        val ARGUMENT_PARSE_FAILURE_DURATION = Caption.of("argument.parse.failure.duration")
    }
}

class DurationArgument<C : Any>(
    name: String,
    description: ArgumentDescription = ArgumentDescription.of(""),
    required: Boolean = true,
    defaultValue: String = "",
    suggestionsProvider: ((CommandContext<C>, String) -> List<String>)? = null,
) : CommandArgument<C, WDuration>(required, name, DurationParser(), defaultValue, WDuration::class.java, suggestionsProvider, description)
