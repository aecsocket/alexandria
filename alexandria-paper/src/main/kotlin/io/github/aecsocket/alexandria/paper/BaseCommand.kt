package io.github.aecsocket.alexandria.paper

import cloud.commandframework.Command
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import io.github.aecsocket.alexandria.LogLevel
import io.github.aecsocket.alexandria.extension.getOr
import io.github.aecsocket.glossa.Message
import io.github.aecsocket.glossa.MessageProxy
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.text
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias Context = CommandContext<CommandSender>

private const val QUERY = "query"

open class BaseCommand(
    private val plugin: AlexandriaPlugin,
    private val messages: MessageProxy<BaseMessages>,
) {
    val manager = PaperCommandManager(
        plugin,
        CommandExecutionCoordinator.simpleCoordinator(),
        { it }, { it }
    )
    val pluginId = plugin.manifest.id
    val root = manager.commandBuilder(pluginId)
    private val help = MinecraftHelp("/$pluginId help", { it }, manager)

    fun Message.sendTo(audience: Audience) {
        forEach { line ->
            audience.sendMessage(plugin.asChat(line))
        }
    }

    fun Audience.locale() = when (this) {
        is Player -> locale()
        else -> plugin.settings.defaultLocale
    }

    fun <T : Any> MessageProxy<T>.forAudience(audience: Audience) = forLocale(audience.locale())

    fun <C> Command.Builder<C>.axPermission(permission: String) = permission("$pluginId.command.$permission")

    fun <C> Command.Builder<C>.axHandler(block: suspend (CommandContext<C>) -> Unit) = handler { ctx ->
        runBlocking {
            block(ctx)
        }
    }

    init {
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER))
            manager.registerBrigadier()

        manager.command(root
            .literal("about")
            .axHandler(::about)
        )
        manager.command(root
            .literal("help")
            .argument(StringArgument.optional(QUERY, StringArgument.StringMode.GREEDY))
            .axHandler(::help)
        )
        manager.command(root
            .literal("reload")
            .axPermission("reload")
            .axHandler(::reload)
        )
    }

    @Suppress("UnstableApiUsage")
    private fun about(ctx: Context) {
        val sender = ctx.sender
        val messages = messages.forAudience(sender)

        val meta = plugin.pluginMeta
        messages.command.about(
            pluginName = text(meta.name, plugin.manifest.accentColor),
            version = meta.version,
            authors = meta.authors.joinToString()
        ).sendTo(sender)
    }

    private fun help(ctx: Context) {
        val sender = ctx.sender
        val query = ctx.getOr(QUERY) ?: ""

        help.queryCommands(
            if (query.startsWith("$pluginId ")) query else "$pluginId $query",
            sender
        )
    }

    private fun reload(ctx: Context) {
        val sender = ctx.sender
        val messages = messages.forAudience(sender)

        messages.command.reload.start().sendTo(sender)
        val log = plugin.reload()
        messages.command.reload.stop(
            numMessages = log.entries.size
        ).sendTo(sender)
        log.entries.forEach { entry ->
            val logMessages = messages.command.reload.log
            when (entry.level) {
                LogLevel.TRACE -> logMessages.trace(entry.message)
                LogLevel.DEBUG -> logMessages.debug(entry.message)
                LogLevel.INFO -> logMessages.info(entry.message)
                LogLevel.WARN -> logMessages.warn(entry.message)
                LogLevel.ERROR -> logMessages.error(entry.message)
            }.sendTo(sender)
        }
    }
}
