package com.gitlab.aecsocket.alexandria.paper

import cloud.commandframework.arguments.standard.EnumArgument
import cloud.commandframework.arguments.standard.LongArgument
import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument
import com.gitlab.aecsocket.alexandria.core.extension.flagged
import com.gitlab.aecsocket.alexandria.core.extension.render
import com.gitlab.aecsocket.glossa.core.I18N
import io.papermc.paper.util.StacktraceDeobfuscator
import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.command.CommandSender
import kotlin.collections.component1
import kotlin.collections.component2

internal class AlexandriaCommand(
    override val plugin: Alexandria
) : BaseCommand(plugin) {
    enum class PlayerLockType(val backing: PlayerLock) {
        SPRINT      (PlayerLock.Sprint),
        JUMP        (PlayerLock.Jump),
        MOVE        (PlayerLock.Move),

        ATTACK      (PlayerLock.Attack),
        INTERACT    (PlayerLock.Interact),
        DIG         (PlayerLock.Dig),
        PLACE       (PlayerLock.Place),

        INVENTORY   (PlayerLock.Inventory),
        RAISE_HAND  (PlayerLock.RaiseHand),
        USE_ACTION  (PlayerLock.UseAction),
    }

    init {
        val locks = root.literal("locks", desc("Manage temporary locks on player actions."))
        manager.command(locks
            .literal("list", desc("List all locked player actions."))
            .argument(SinglePlayerSelectorArgument.optional("target"), desc("Player to target."))
            .permission(perm("player-locks.list"))
            .handler { handle(it, ::playerLocksList) })
        manager.command(locks
            .literal("acquire", desc("Lock a player action."))
            .argument(EnumArgument.of(PlayerLockType::class.java, "lock"), desc("Lock type."))
            .argument(SinglePlayerSelectorArgument.optional("target"), desc("Player to target."))
            .permission(perm("player-locks.acquire"))
            .handler { handle(it, ::playerLocksAcquire) })
        manager.command(locks
            .literal("release", desc("Unlock a player action by a previously acquired lock."))
            .argument(LongArgument.of("lock-id"), desc("Previously acquired lock ID."))
            .argument(SinglePlayerSelectorArgument.optional("target"), desc("Player to target."))
            .permission(perm("player-locks.release"))
            .handler { handle(it, ::playerLocksRelease) })

        val actions = root.literal("actions", desc("Manage player action states."))
        manager.command(actions
            .literal("stop", desc("Instantly stop an action."))
            .argument(SinglePlayerSelectorArgument.optional("target"), desc("Player to target."))
            .flag(manager.flagBuilder("success")
                .withAliases("s")
                .withDescription(desc("The action should be ended successfully rather than cancelled.")))
            .permission(perm("player-actions.stop"))
            .handler { handle(it, ::playerActionsStop) })
    }

    fun playerLocksList(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val target = ctx.player("target", sender, i18n).alexandria

        val byType = target.locks.byType
        if (byType.isEmpty()) {
            plugin.sendMessage(sender, i18n.csafe("player_locks.list.none") {
                subst("target", target.handle.displayName())
            })
            return
        }

        byType.forEach { (type, instances) ->
            plugin.sendMessage(sender, i18n.csafe("player_locks.list.type") {
                icu("name", type.name)
                icu("instances", instances.size)
            })
            instances.forEach { instance ->
                val traceArray = instance.stackTrace.toTypedArray()
                StacktraceDeobfuscator.INSTANCE.deobfuscateStacktrace(traceArray)
                val stackTrace = traceArray.toList()
                val hover = formatAsStackTrace(stackTrace.toList().render(false), stackTrace.render(true), i18n)
                plugin.sendMessage(sender, i18n.csafe("player_locks.list.instance") {
                    subst("id", text(instance.id))
                    icu("thread_name", instance.threadName)
                }.map { it.hoverEvent(hover) })
            }
        }
    }

    fun playerLocksAcquire(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val target = ctx.player("target", sender, i18n)
        val lock = ctx.get<PlayerLockType>("lock")

        val (lockId) = target.alexandria.acquireLock(lock.backing)

        plugin.sendMessage(sender, i18n.csafe("player_locks.acquire") {
            subst("target", target.displayName())
            icu("lock_type", lock.name)
            subst("lock_id", text(lockId))
        })
    }

    fun playerLocksRelease(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val target = ctx.player("target", sender, i18n).alexandria
        val lockId = ctx.get<Long>("lock-id")

        if (!target.hasLockById(lockId))
            error(i18n.safe("error.invalid_lock_id") {
                subst("lock_id", text(lockId))
            })

        target.releaseLock(lockId)
        plugin.sendMessage(sender, i18n.csafe("player_locks.release") {
            subst("target", target.handle.displayName())
            subst("lock_id", text(lockId))
        })
    }

    fun playerActionsStop(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val target = ctx.player("target", sender, i18n).alexandria
        val success = ctx.flagged("success")

        target.action?.let {
            target.stopAction(success)
            plugin.sendMessage(sender, i18n.csafe("player_actions.stop") {
                subst("target", target.handle.displayName())
            })
        } ?: error(i18n.safe("error.no_active_action"))
    }
}
