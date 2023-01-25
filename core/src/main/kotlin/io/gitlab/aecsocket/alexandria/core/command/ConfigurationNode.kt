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
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationNode
import java.util.*

class ConfigurationNodeParser<C : Any>(
    private val loader: (String) -> ConfigurationNode
) : ArgumentParser<C, ConfigurationNode> {
    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<ConfigurationNode> {
        return inputQueue.peek()?.let {
            val input = inputQueue.joinToString(" ")
            inputQueue.clear()
            try {
                ArgumentParseResult.success(loader(input))
            } catch (ex: ConfigurateException) {
                ArgumentParseResult.failure(ex)
            }
        } ?: ArgumentParseResult.failure(NoInputProvidedException(
            ConfigurationNodeParser::class.java,
            commandContext
        ))
    }
}

class ConfigurationNodeArgument<C : Any>(
    name: String,
    loader: (String) -> ConfigurationNode,
    description: ArgumentDescription = ArgumentDescription.of(""),
    required: Boolean = true,
    defaultValue: String = "",
    suggestionsProvider: ((CommandContext<C>, String) -> List<String>)? = null,
) : CommandArgument<C, ConfigurationNode>(required, name, ConfigurationNodeParser(loader), defaultValue, ConfigurationNode::class.java, suggestionsProvider, description)
