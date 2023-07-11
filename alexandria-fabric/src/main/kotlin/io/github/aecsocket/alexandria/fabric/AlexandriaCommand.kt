package io.github.aecsocket.alexandria.fabric

import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.fabric.FabricServerCommandManager
import net.minecraft.commands.CommandSourceStack

typealias Context = CommandContext<CommandSourceStack>

fun commandManager() =
    FabricServerCommandManager(
        CommandExecutionCoordinator.simpleCoordinator(),
        { it },
        { it },
    )
