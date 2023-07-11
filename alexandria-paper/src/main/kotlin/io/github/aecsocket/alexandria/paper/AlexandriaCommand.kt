package io.github.aecsocket.alexandria.paper

import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import org.bukkit.command.CommandSender

typealias Context = CommandContext<CommandSender>

fun commandManager(hook: AlexandriaPlugin<*>) =
    PaperCommandManager(
            hook,
            CommandExecutionCoordinator.simpleCoordinator(),
            { it },
            { it },
        )
        .apply { if (hasCapability(CloudBukkitCapabilities.BRIGADIER)) registerBrigadier() }
