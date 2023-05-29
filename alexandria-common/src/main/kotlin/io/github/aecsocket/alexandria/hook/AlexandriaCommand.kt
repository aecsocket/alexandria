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
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component.text
import org.slf4j.event.Level
import kotlin.jvm.optionals.getOrElse

private const val QUERY = "query"

abstract class AlexandriaCommand<C : Audience>(
    private val hook: AlexandriaHook,
    val manager: CommandManager<C>,
) {
    val pluginId = hook.manifest.id
    val root = manager.commandBuilder(pluginId)
    private val messages = hook.glossa.messageProxy<AlexandriaMessages>()
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

    fun <T : Any> MessageProxy<T>.forAudience(sender: Audience): T {
        val locale = sender.get(Identity.LOCALE).getOrElse { hook.settings.defaultLocale }
        return forLocale(locale)
    }

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
            numMessages = log.entries.size,
        ).sendTo(sender)
        log.entries.forEach { entry ->
            val logMessages = messages.command.reload.log
            when (entry.level) {
                Level.TRACE -> logMessages.trace(entry.message)
                Level.DEBUG -> logMessages.debug(entry.message)
                Level.INFO -> logMessages.info(entry.message)
                Level.WARN -> logMessages.warn(entry.message)
                Level.ERROR -> logMessages.error(entry.message)
            }.sendTo(sender)
        }
    }

    fun Message.sendTo(audience: Audience) {
        forEach { line ->
            audience.sendMessage(hook.asChat(line))
        }
    }

    fun <C> Command.Builder<C>.axPermission(permission: String) = permission("$pluginId.command.$permission")

    fun <C> Command.Builder<C>.axHandler(block: (CommandContext<C>) -> Unit) = handler { ctx ->
        block(ctx)
    }
}