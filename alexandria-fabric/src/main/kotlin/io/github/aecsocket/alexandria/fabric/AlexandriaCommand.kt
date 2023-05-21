package io.github.aecsocket.alexandria.fabric

import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.fabric.FabricServerCommandManager
import io.github.aecsocket.alexandria.hook.AlexandriaHook
import io.github.aecsocket.alexandria.hook.HookCommand
import io.github.aecsocket.glossa.MessageProxy
import net.minecraft.commands.CommandSourceStack

typealias Context = CommandContext<CommandSourceStack>

class AlexandriaCommand(
    private val hook: AlexandriaHook,
    manager: FabricServerCommandManager<CommandSourceStack> = FabricServerCommandManager(
        CommandExecutionCoordinator.simpleCoordinator(),
        { it }, { it },
    ),
) : HookCommand<CommandSourceStack>(hook, manager) {
    override fun <T : Any> MessageProxy<T>.forAudience(sender: CommandSourceStack): T {
        // TODO per-player locale
        return forLocale(hook.settings.defaultLocale)
    }
}
