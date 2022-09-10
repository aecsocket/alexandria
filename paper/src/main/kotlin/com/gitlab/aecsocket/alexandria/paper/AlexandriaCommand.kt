package com.gitlab.aecsocket.alexandria.paper

import cloud.commandframework.arguments.standard.EnumArgument
import cloud.commandframework.arguments.standard.LongArgument
import com.gitlab.aecsocket.alexandria.core.extension.render
import com.gitlab.aecsocket.glossa.core.I18N
import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal class AlexandriaCommand(
    override val plugin: Alexandria
) : BaseCommand(plugin) {
    enum class PlayerLockType(val backing: PlayerLock) {
        JUMP    (PlayerLock.Jump),
        SPRINT  (PlayerLock.Sprint),
        MOVE    (PlayerLock.Move),
        ATTACK  (PlayerLock.Attack),
    }

    init {
        val playerLocks = root.literal("player-locks", desc("Manage temporary locks on player actions."))
        manager.command(playerLocks
            .literal("list", desc("List all locked player actions."))
            .permission(perm("player-locks.list"))
            .handler { handle(it, ::playerLocksList) })
        manager.command(playerLocks
            .literal("acquire", desc("Lock a player action."))
            .argument(EnumArgument.of(PlayerLockType::class.java, "lock"), desc("Lock type."))
            .permission(perm("player-locks.acquire"))
            .handler { handle(it, ::playerLocksAcquire) })
        manager.command(playerLocks
            .literal("release", desc("Unlock a player action by a previously acquired lock."))
            .argument(LongArgument.of("lock-id"), desc("Previously acquired lock ID."))
            .permission(perm("player-locks.release"))
            .handler { handle(it, ::playerLocksRelease) })
    }

    fun playerLocksList(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val player = sender as Player // TODO temp

        val byType = player.locks?.byType ?: emptyMap()
        if (byType.isEmpty()) {
            plugin.sendMessage(sender, i18n.csafe("player_locks.list.none"))
            return
        }

        byType.forEach { (type, instances) ->
            plugin.sendMessage(sender, i18n.csafe("player_locks.list.type") {
                icu("name", type.name)
                icu("instances", instances.size)
            })
            instances.forEach { instance ->
                val hover = formatAsStackTrace(instance.stackTrace.render(false), instance.stackTrace.render(true), i18n)
                    .join(JoinConfiguration.newlines())
                plugin.sendMessage(sender, i18n.csafe("player_locks.list.instance") {
                    subst("id", text(instance.id))
                    icu("thread_name", instance.threadName)
                }.map { it.hoverEvent(hover) })
            }
        }
    }

    fun playerLocksAcquire(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val player = sender as Player // TODO temp
        val lock = ctx.get<PlayerLockType>("lock")

        val (lockId) = player.acquireLock(lock.backing)

        plugin.sendMessage(sender, i18n.csafe("player_locks.acquire") {
            icu("lock_type", lock.name)
            subst("lock_id", text(lockId))
        })
    }

    fun playerLocksRelease(ctx: Context, sender: CommandSender, i18n: I18N<Component>) {
        val player = sender as Player // TODO temp
        val lockId = ctx.get<Long>("lock-id")

        if (!player.hasLockById(lockId))
            error(i18n.safe("error.invalid_lock_id") {
                subst("lock_id", text(lockId))
            })

        plugin.playerLocks.release(player, lockId)
        plugin.sendMessage(sender, i18n.csafe("player_locks.release") {
            subst("lock_id", text(lockId))
        })
    }
}
