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
import kotlin.jvm.optionals.getOrElse
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component.text

private const val QUERY = "query"

/**
 * Base implementation for an [AlexandriaHook]'s command, used to control aspects of the hook.
 *
 * This uses the Cloud command framework, and defines certain inbuilt commands which are common
 * across all Alexandria hooks. Some utility methods for working with audiences are also provided.
 */
abstract class AlexandriaCommand<C : Audience>(
    private val hook: AlexandriaHook<*>,
    val manager: CommandManager<C>,
) {
  val pluginId = hook.manifest.id
  val root = manager.commandBuilder(pluginId)
  private val messages = hook.glossa.messageProxy<AlexandriaMessages>()
  private val help = MinecraftHelp("/$pluginId help", { it }, manager)

  init {
    manager.command(root.literal("about").axHandler(::about))
    manager.command(
        root
            .literal("help")
            .argument(StringArgument.optional("help", StringArgument.StringMode.GREEDY))
            .axHandler(::help))
    manager.command(root.literal("reload").axPermission("reload").axHandler(::reload))
  }

  fun <T : Any> MessageProxy<T>.forAudience(sender: Audience): T {
    val locale = sender.get(Identity.LOCALE).getOrElse { hook.settings.defaultLocale }
    return forLocale(locale)
  }

  private fun about(ctx: CommandContext<C>) {
    val sender = ctx.sender
    val messages = messages.forAudience(sender)

    val meta = hook.meta
    messages.command
        .about(
            pluginName = text(meta.name, hook.manifest.accentColor),
            version = meta.version,
            authors = meta.authors.joinToString())
        .sendTo(sender)
  }

  private fun help(ctx: CommandContext<C>) {
    val sender = ctx.sender
    val query = ctx.getOr(QUERY) ?: ""

    help.queryCommands(if (query.startsWith("$pluginId ")) query else "$pluginId $query", sender)
  }

  private fun reload(ctx: CommandContext<C>) {
    val sender = ctx.sender
    val messages = messages.forAudience(sender)

    hook.reload()
    messages.command.reload().sendTo(sender)
  }

  fun Message.sendTo(audience: Audience) {
    forEach { line -> audience.sendMessage(hook.asChat(line)) }
  }

  fun Command.Builder<C>.axPermission(permission: String) =
      permission("$pluginId.command.$permission")

  fun Command.Builder<C>.axHandler(block: (CommandContext<C>) -> Unit) = handler { ctx ->
    try {
      block(ctx)
    } catch (ex: CommandException) {
      ex.text.sendTo(ctx.sender)
    }
  }

  private class CommandException(val text: Message) : RuntimeException()

  fun error(text: Message): Nothing {
    throw CommandException(text)
  }

  fun mustBePlayer(sender: C): Nothing {
    error(messages.forAudience(sender).error.sender.mustBePlayer())
  }
}
