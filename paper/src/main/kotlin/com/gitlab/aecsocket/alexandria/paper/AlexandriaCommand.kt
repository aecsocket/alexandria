package com.gitlab.aecsocket.alexandria.paper

import cloud.commandframework.arguments.standard.BooleanArgument
import cloud.commandframework.arguments.standard.FloatArgument
import com.gitlab.aecsocket.alexandria.core.extension.get
import com.gitlab.aecsocket.glossa.core.I18N
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

internal class AlexandriaCommand(
    override val plugin: Alexandria
) : BaseCommand(plugin) {
    init {
        val physics = root
            .literal("physics", desc("Control the physics spaces."))
        manager.command(physics
            .literal("update", desc("Control updating of physics spaces."))
            .argument(BooleanArgument.optional("enabled"), desc("Whether physics spaces should update or not."))
            .permission(perm("physics.update"))
            .handler { handle(it, ::physicsUpdate) })
        manager.command(physics
            .literal("time-interval", desc("Sets the time interval of physics updates."))
            .argument(FloatArgument.optional("interval"), desc("Time interval in seconds."))
            .permission(perm("physics.time-interval"))
            .handler { handle(it, ::physicsTimeInterval) })
    }

    private fun physicsUpdate(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val enabled = ctx.get("enabled") { !plugin.physics.enabled }

        plugin.physics.enabled = enabled
        plugin.sendMessage(sender, i18n.safe("command.alexandria.physics.update.${if (enabled) "enabled" else "disabled"}"))
    }

    private fun physicsTimeInterval(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val interval = ctx.get("interval") { TIME_INTERVAL }

        plugin.physics.timeInterval = interval
        plugin.sendMessage(sender, i18n.safe("command.alexandria.physics.time_interval") {
            icu("interval", interval)
        })
    }
}
