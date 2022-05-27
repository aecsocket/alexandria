package com.github.aecsocket.alexandria.paper.plugin

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.Command
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.captions.Caption
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry
import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import java.util.Locale

fun desc(string: String) = ArgumentDescription.of(string)

open class CloudCommand<P : BasePlugin>(
    protected val plugin: P,
    val rootName: String,
    rootFactory: (PaperCommandManager<CommandSender>, String) -> Command.Builder<CommandSender>
) {
    protected val manager: PaperCommandManager<CommandSender> = PaperCommandManager(plugin,
        CommandExecutionCoordinator.simpleCoordinator(),
        { it }, { it })
    protected val captionLocalizer: (Caption, CommandSender) -> String
    protected val captions: FactoryDelegatingCaptionRegistry<CommandSender>?
    protected val root: Command.Builder<CommandSender>

    init {
        if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
            manager.registerAsynchronousCompletions()
        if (manager.queryCapability(CloudBukkitCapabilities.BRIGADIER))
            manager.registerBrigadier()

        val help = MinecraftHelp("/$rootName help", { it }, manager)
        captionLocalizer = { caption, sender ->
            PlainTextComponentSerializer.plainText().serialize(
                plugin.i18n.safe(plugin.locale(sender), "error.caption.${caption.key}").join(JoinConfiguration.newlines()))
        }

        val captions = manager.captionRegistry
        if (captions is FactoryDelegatingCaptionRegistry<CommandSender>) {
            this.captions = captions
        } else
            this.captions = null

        MinecraftExceptionHandler<CommandSender>()
            .withArgumentParsingHandler()
            .withInvalidSenderHandler()
            .withInvalidSyntaxHandler()
            .withNoPermissionHandler()
            .withCommandExecutionHandler()
            .withDecorator { msg -> plugin.i18n.safe(plugin.defaultLocale(), "error.command") {
                sub("message") { listOf(msg)}
            }.join(JoinConfiguration.newlines()) }
            .apply(manager) { it }

        root = rootFactory(manager, rootName)
        manager.command(root
            .literal("help", desc("Lists help information."))
            .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
            .handler { ctx ->
                val query = ctx["query", ""]
                help.queryCommands(if (query.startsWith("$rootName ")) query else "$rootName $query", ctx.sender)
            }
        )
        manager.command(root
            .literal("version", desc("Gets version information."))
            .handler { handle(it, ::version) })
        manager.command(root
            .literal("reload", desc("Reloads all plugin data."))
            .permission(perm("reload"))
            .handler { handle(it, ::reload) })
    }

    protected fun perm(vararg string: String) = "$rootName.command.${string.joinToString(".")}"

    protected fun handle(
        ctx: CommandContext<CommandSender>,
        handler: (CommandContext<CommandSender>, CommandSender, Locale) -> Unit
    ) {
        val sender = ctx.sender
        val locale = plugin.locale(sender)
        try {
            handler(ctx, sender, locale)
        } catch (ex: CommandException) {
            // todo
        }
    }

    class CommandException(cause: Throwable?) : RuntimeException(cause)

    fun version(ctx: CommandContext<CommandSender>, sender: CommandSender, locale: Locale) {
        val desc = plugin.description
        plugin.send(sender) { safe(locale, "command.version") {
            raw("name") { desc.name }
            raw("version") { desc.version }
            list("authors") { desc.authors.forEach {
                raw(it)
            } }
        } }
    }

    fun reload(ctx: CommandContext<CommandSender>, sender: CommandSender, locale: Locale) {
        plugin.send(sender) { safe(locale, "command.reload.pre") }

        val (log, _) = plugin.load()
        log.forEach { plugin.log.record(it) }

        plugin.send(sender) { safe(locale, "command.reload.post") {
            raw("qt_entries") { log.size }
            list("entries") { log.map { record ->
                sub(safe("command.reload.entry.${record.level.name}") {
                    list("lines") { record.lines().map {
                        raw(it)
                    } }
                })
            } }
        } }
    }
}

@Suppress("UNCHECKED_CAST") // cloud is stupid in this regard anyway
operator fun <V> CommandContext<*>.get(key: String, default: V): V = getOrDefault(key, default) as V
