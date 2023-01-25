package io.gitlab.aecsocket.alexandria.paper.command

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.arguments.standard.EnumArgument.EnumParseException
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import io.gitlab.aecsocket.alexandria.core.extension.EnumParseUtil
import io.gitlab.aecsocket.alexandria.paper.PlayerInventorySlot
import org.bukkit.inventory.EquipmentSlot
import java.util.*

private val EQUIPMENT_SLOT_PARSE = EnumParseUtil(EquipmentSlot::class)

class PlayerInventorySlotParser<C : Any> : ArgumentParser<C, PlayerInventorySlot> {
    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<PlayerInventorySlot> {
        return inputQueue.peek()?.let {
            val input = inputQueue.remove()
            return input.toIntOrNull()?.let {
                ArgumentParseResult.success(PlayerInventorySlot.ByInteger(it))
            } ?: try {
                val slot = EQUIPMENT_SLOT_PARSE.parse(commandContext, input)
                ArgumentParseResult.success(PlayerInventorySlot.ByEquipment(slot))
            } catch (ex: EnumParseException) {
                ArgumentParseResult.failure(ex)
            }
        } ?: ArgumentParseResult.failure(NoInputProvidedException(
            PlayerInventorySlotParser::class.java,
            commandContext
        ))
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
        return EquipmentSlot.values().map { it.name.lowercase() }
    }
}

class PlayerInventorySlotArgument<C : Any>(
    name: String,
    description: ArgumentDescription = ArgumentDescription.of(""),
    required: Boolean = true,
    defaultValue: String = "",
    suggestionsProvider: ((CommandContext<C>, String) -> List<String>)? = null,
) : CommandArgument<C, PlayerInventorySlot>(required, name, PlayerInventorySlotParser(), defaultValue, PlayerInventorySlot::class.java, suggestionsProvider, description)
