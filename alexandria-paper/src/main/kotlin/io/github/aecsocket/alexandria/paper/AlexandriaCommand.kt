package io.github.aecsocket.alexandria.paper

import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import io.github.aecsocket.alexandria.hook.HookCommand
import io.github.aecsocket.glossa.MessageProxy
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias Context = CommandContext<CommandSender>

abstract class AlexandriaCommand(
    private val hook: AlexandriaPlugin<*>,
    manager: PaperCommandManager<CommandSender> = PaperCommandManager(
        hook,
        CommandExecutionCoordinator.simpleCoordinator(),
        { it }, { it },
    ).apply {
        if (hasCapability(CloudBukkitCapabilities.BRIGADIER))
            registerBrigadier()
    },
) : HookCommand<CommandSender>(hook, manager) {
    override fun <T : Any> MessageProxy<T>.forAudience(sender: CommandSender): T {
        return forLocale(when (sender) {
            is Player -> sender.locale()
            else -> hook.settings.defaultLocale
        })
    }
}
