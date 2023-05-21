package io.github.aecsocket.alexandria.hook

import cloud.commandframework.Command
import cloud.commandframework.CommandManager
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.context.CommandContext
import cloud.commandframework.minecraft.extras.MinecraftHelp
import io.github.aecsocket.alexandria.extension.getOr
import io.github.aecsocket.glossa.Message
import io.github.aecsocket.glossa.MessageProxy
import io.github.aecsocket.glossa.messageProxy
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.text
import java.util.logging.Level

private const val QUERY = "query"

abstract class HookCommand<C : Audience>(
    private val hook: AlexandriaHook,
    val manager: CommandManager<C>,
) {
    val pluginId = hook.manifest.id
    val root = manager.commandBuilder(pluginId)
    private val messages = hook.glossa.messageProxy<HookMessages>()
    private val help = MinecraftHelp("/$pluginId help", { it }, manager)

    init {
        manager.command(root
            .literal("about")
            .axHandler(::about)
        )
        manager.command(root
            .literal("help")
            .argument(StringArgument.optional("help", StringArgument.StringMode.GREEDY))
            .axHandler(::help)
        )
        manager.command(root
            .literal("reload")
            .axPermission("reload")
            .axHandler(::reload)
        )
    }

    protected abstract fun <T : Any> MessageProxy<T>.forAudience(sender: C): T

    private fun about(ctx: CommandContext<C>) {
        val sender = ctx.sender
        val messages = messages.forAudience(sender)

        messages.command.about(
            pluginName = text(hook.hookName, hook.manifest.accentColor),
            version = hook.version,
            authors = hook.authors.joinToString()
        ).sendTo(sender)
    }

    private fun help(ctx: CommandContext<C>) {
        val sender = ctx.sender
        val query = ctx.getOr(QUERY) ?: ""

        help.queryCommands(
            if (query.startsWith("$pluginId ")) query else "$pluginId $query",
            sender
        )
    }

    private fun reload(ctx: CommandContext<C>) {
        val sender = ctx.sender
        val messages = messages.forAudience(sender)

        messages.command.reload.start().sendTo(sender)
        val log = hook.reload()
        messages.command.reload.stop(
            numMessages = log.records.size,
        ).sendTo(sender)
        log.records.forEach { entry ->
            val logMessages = messages.command.reload.log
            when (entry.level) {
                Level.FINER, Level.FINEST -> logMessages.debug(entry.message)
                Level.CONFIG, Level.FINE -> logMessages.trace(entry.message)
                Level.WARNING -> logMessages.warn(entry.message)
                Level.SEVERE -> logMessages.error(entry.message)
                else -> logMessages.info(entry.message)
            }.sendTo(sender)
        }
    }

    fun Message.sendTo(audience: Audience) {
        forEach { line ->
            audience.sendMessage(hook.asChat(line))
        }
    }

    fun <C> Command.Builder<C>.axPermission(permission: String) = permission("$pluginId.command.$permission")

    fun <C> Command.Builder<C>.axHandler(block: suspend (CommandContext<C>) -> Unit) = handler { ctx ->
        runBlocking {
            block(ctx)
        }
    }
}