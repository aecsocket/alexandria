package com.gitlab.aecsocket.alexandria.paper

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.Command
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.bukkit.arguments.selector.EntitySelector
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector
import cloud.commandframework.captions.Caption
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.InvalidCommandSenderException
import cloud.commandframework.exceptions.InvalidSyntaxException
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.alexandria.core.extension.render
import com.gitlab.aecsocket.alexandria.core.extension.simpleTrace
import com.gitlab.aecsocket.alexandria.core.extension.value
import com.gitlab.aecsocket.glossa.core.I18N
import com.gitlab.aecsocket.glossa.core.I18NArgs
import io.papermc.paper.util.StacktraceDeobfuscator
import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

fun desc(content: String) = ArgumentDescription.of(content)

typealias Context = CommandContext<CommandSender>

class CommandException(
    cause: Throwable? = null,
    val lines: List<Component>,
) : RuntimeException(cause)

abstract class BaseCommand(
    plugin: BasePlugin,
) {
    abstract val plugin: BasePlugin

    val pluginName = plugin.manifest.name
    val rootName = plugin.manifest.chatName
    protected val manager = PaperCommandManager(
        plugin,
        CommandExecutionCoordinator.simpleCoordinator(),
        { it }, { it }
    )
    protected val captionLocalizer: (Caption, CommandSender) -> String
    protected val captions: FactoryDelegatingCaptionRegistry<CommandSender>?
    protected val root: Command.Builder<CommandSender>

    protected fun perm(key: String) = "$rootName.command.$key"

    protected fun i18nKey(key: String) = "command.$pluginName.$key"

    protected fun I18N<Component>.cmake(key: String, args: I18NArgs<Component>) =
        make(i18nKey(key), args)

    protected fun I18N<Component>.cmake(key: String, args: I18NArgs.Scope<Component>.() -> Unit = {}) =
        make(i18nKey(key), args)

    protected fun I18N<Component>.csafe(key: String, args: I18NArgs<Component>) =
        safe(i18nKey(key), args)

    protected fun I18N<Component>.csafe(key: String, args: I18NArgs.Scope<Component>.() -> Unit = {}) =
        safe(i18nKey(key), args)

    private interface HandlerContext {
        val ex: Throwable
        val i18n: I18N<Component>

        fun args(args: I18NArgs.Scope<Component>.() -> Unit)

        fun messageFormat(formatter: (List<Component>) -> List<Component>)
    }

    init {
        // will error if attempting to async getEntities with an *EntitySelector
        //if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
        //    manager.registerAsynchronousCompletions()
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER))
            manager.registerBrigadier()

        val help = MinecraftHelp("/$rootName help", { it }, manager)
        captionLocalizer = { caption, sender ->
            PlainTextComponentSerializer.plainText().serialize(
                AlexandriaAPI.i18nFor(sender).run {
                    safeOne("error.caption.${caption.key}", newline)
                }
            )
        }

        captions = manager.captionRegistry() as? FactoryDelegatingCaptionRegistry<CommandSender>

        fun handlerOf(
            key: String,
            handler: HandlerContext.() -> Unit = {},
        ): (CommandSender, Exception) -> Component = { sender, rawEx ->
            val i18n = AlexandriaAPI.i18nFor(sender)
            val ex = rawEx.cause ?: rawEx
            StacktraceDeobfuscator.INSTANCE.deobfuscateThrowable(ex)

            var mArgs: I18NArgs.Scope<Component>.() -> Unit = {}
            var mFormatter: (List<Component>) -> List<Component> = { it }

            val ctx = object : HandlerContext {
                override val ex get() = ex
                override val i18n get() = i18n

                override fun args(args: I18NArgs.Scope<Component>.() -> Unit) {
                    mArgs = args
                }

                override fun messageFormat(formatter: (List<Component>) -> List<Component>) {
                    mFormatter = formatter
                }
            }

            handler(ctx)

            var lines = i18n.safe("error.caption.$key", mArgs)
            lines = plugin.chatMessages(mFormatter(lines))

            lines.join(JoinConfiguration.newlines())
        }

        fun errorStackTrace(ex: Throwable, i18n: I18N<Component>): List<Component> {
            return stackTrace(ex, i18n).map { message ->
                text("  ")
                    .append(i18n.safeOne("error.stack_trace") {
                        subst("message", message)
                    })
            }
        }

        MinecraftExceptionHandler<CommandSender>()
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, handlerOf("invalid_sender") {
                args {
                    icu("sender_type", (ex as InvalidCommandSenderException).requiredSender.simpleName)
                }
            })
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, handlerOf("invalid_sender") {
                args {
                    icu("sender_type", (ex as InvalidCommandSenderException).requiredSender.simpleName)
                }
            })
            .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX, handlerOf("invalid_syntax") {
                args {
                    icu("correct_syntax", (ex as InvalidSyntaxException).correctSyntax)
                }
            })
            .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, handlerOf("no_permission"))
            .withHandler(MinecraftExceptionHandler.ExceptionType.ARGUMENT_PARSING, handlerOf("argument_parsing") {
                messageFormat { lines ->
                    lines + errorStackTrace(ex, i18n)
                }
            })
            .withHandler(MinecraftExceptionHandler.ExceptionType.COMMAND_EXECUTION, handlerOf("command_execution") {
                plugin.log.line(LogLevel.Error, ex) { "Could not run command" }

                messageFormat { lines ->
                    lines + errorStackTrace(ex, i18n)
                }
            })
            .apply(manager) { it }

        root = manager.commandBuilder(rootName, desc("Core command plugin."))
        manager.command(root
            .literal("help", desc("Lists help information."))
            .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
            .handler { ctx ->
                val query = ctx.value("query") { "" }
                help.queryCommands(
                    if (query.startsWith("$rootName ")) query else "$rootName $query",
                    ctx.sender
                )
            })
        manager.command(root
            .literal("about", desc("Gets general information about the plugin."))
            .handler { handle(it, ::about) })
        manager.command(root
            .literal("reload", desc("Reloads all plugin data."))
            .permission(perm("reload"))
            .handler { handle(it, ::reload) })
    }

    protected fun error(
        message: List<Component>,
        cause: Throwable? = null,
    ): Nothing {
        throw CommandException(cause, message)
    }

    protected fun formatAsStackTrace(
        userTrace: List<Component>,
        longTrace: List<Component>,
        i18n: I18N<Component>
    ): List<Component> {
        val lines = (userTrace + newline() + i18n.safe("click_to_copy"))
        val click = ClickEvent.copyToClipboard(longTrace.joinToString("\n") {
            PlainTextComponentSerializer.plainText().serialize(it)
        })
        return lines.map { it.clickEvent(click) }
    }

    protected fun stackTrace(ex: Throwable, i18n: I18N<Component>): List<Component> {
        StacktraceDeobfuscator.INSTANCE.deobfuscateThrowable(ex)
        val userStackTrace = ex.render(false)
        val longStackTrace = ex.render(true)
        val hover = formatAsStackTrace(userStackTrace, longStackTrace, i18n).join(JoinConfiguration.newlines())
        return ex.simpleTrace().map {
            text(it).hoverEvent(hover)
        }
    }

    private fun errorNoTargets(arg: String, i18n: I18N<Component>): Nothing =
        error(i18n.safe("error.no_targets") {
            icu("argument", arg)
        })

    protected fun <S : EntitySelector> S.assertTargets(arg: String, i18n: I18N<Component>): S {
        if (entities.isEmpty())
            errorNoTargets(arg, i18n)
        return this
    }

    private fun asPlayer(arg: String, sender: CommandSender, i18n: I18N<Component>) =
        if (sender is Player) sender else errorNoTargets(arg, i18n)

    protected fun EntitySelector?.orSender(arg: String, sender: CommandSender, i18n: I18N<Component>): List<Entity> {
        return this?.assertTargets(arg, i18n)?.entities
            ?: if (sender is Entity) listOf(sender) else errorNoTargets(arg, i18n)
    }

    protected fun CommandContext<*>.entities(arg: String, sender: CommandSender, i18n: I18N<Component>) =
        value<EntitySelector?>(arg) { null }.orSender(arg, sender, i18n)

    protected fun MultiplePlayerSelector?.orSender(arg: String, sender: CommandSender, i18n: I18N<Component>): List<Player> {
        return this?.assertTargets(arg, i18n)?.players ?: listOf(asPlayer(arg, sender, i18n))
    }

    protected fun CommandContext<*>.players(arg: String, sender: CommandSender, i18n: I18N<Component>) =
        value<MultiplePlayerSelector?>(arg) { null }.orSender(arg, sender, i18n)

    protected fun SinglePlayerSelector?.orSender(arg: String, sender: CommandSender, i18n: I18N<Component>): Player {
        return this?.assertTargets(arg, i18n)?.player ?: asPlayer(arg, sender, i18n)
    }

    protected fun CommandContext<*>.player(arg: String, sender: CommandSender, i18n: I18N<Component>) =
        value<SinglePlayerSelector?>(arg) { null }.orSender(arg, sender, i18n)

    protected fun handle(
        ctx: Context,
        handler: (Context, CommandSender, I18N<Component>) -> Unit
    ) {
        val sender = ctx.sender
        val i18n = AlexandriaAPI.i18nFor(sender)
        try {
            handler(ctx, sender, i18n)
        } catch (ex: CommandException) {
            ex.cause?.let { cause ->
                val stackTrace = stackTrace(cause, i18n)
                plugin.sendMessage(sender, ex.lines + stackTrace)
            } ?: run {
                plugin.sendMessage(sender, ex.lines)
            }
        }
    }

    fun about(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val desc = plugin.description
        plugin.sendMessage(sender, i18n.safe("command.core.about") {
            icu("name", desc.name)
            icu("version", desc.version)
            list("authors", desc.authors.map { text(it) })
        })
    }

    fun reload(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        plugin.sendMessage(sender, i18n.safe("command.core.reload.pre"))

        val (log) = plugin.load()
        log.forEach { plugin.log.record(it) }

        plugin.sendMessage(sender, i18n.safe("command.core.reload.post") {
            icu("entries", log.size)
        })

        log.forEach { record ->
            val logLevel = record.level.name
            val lines = record.lines().flatMap { message ->
                i18n.safe("command.core.reload.entry.$logLevel") {
                    icu("message", message)
                }
            }

            plugin.sendMessage(sender, lines)
        }
    }
}
