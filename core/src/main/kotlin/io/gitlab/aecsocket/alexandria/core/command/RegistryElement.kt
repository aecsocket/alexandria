package io.gitlab.aecsocket.alexandria.core.command

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.captions.Caption
import cloud.commandframework.captions.CaptionVariable
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import cloud.commandframework.exceptions.parsing.ParserException
import io.gitlab.aecsocket.alexandria.core.keyed.Keyed
import io.gitlab.aecsocket.alexandria.core.keyed.Registry
import java.util.*

class RegistryElementException(
    context: CommandContext<*>,
    caption: Caption,
    input: String,
) : ParserException(
    RegistryElementParser::class.java, context, caption,
    CaptionVariable.of("input", input)
)

class RegistryElementParser<C : Any, T : Keyed>(
    private val registry: Registry<T>,
    private val failCaption: Caption,
) : ArgumentParser<C, T> {
    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<T> {
        return inputQueue.peek()?.let { input ->
            inputQueue.remove()
            registry[input]?.let {
                ArgumentParseResult.success(it)
            } ?: ArgumentParseResult.failure(RegistryElementException(
                commandContext, failCaption, input
            ))
        } ?: ArgumentParseResult.failure(NoInputProvidedException(
            RegistryElementParser::class.java,
            commandContext
        ))
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String) = registry.entries.keys.toList()
}

open class RegistryElementArgument<C : Any, T : Keyed>(
    name: String,
    registry: Registry<T>,
    failCaption: Caption,
    elementType: Class<T>,
    description: ArgumentDescription = ArgumentDescription.of(""),
    required: Boolean = true,
    defaultValue: String = "",
    suggestionsProvider: ((CommandContext<C>, String) -> List<String>)? = null,
) : CommandArgument<C, T>(required, name, RegistryElementParser(registry, failCaption), defaultValue, elementType, suggestionsProvider, description)
