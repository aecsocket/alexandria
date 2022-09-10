package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.protocol.potion.PotionTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth
import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.alexandria.paper.PlayerLock.OnRelease
import com.gitlab.aecsocket.alexandria.paper.extension.forceModifier
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.atomic.AtomicLong

internal const val NO_SPRINT_FOOD = 6

interface PlayerLock {
    val name: String

    fun acquire(player: Player, acquire: Boolean, locks: PlayerLocks): OnRelease

    fun interface OnRelease {
        fun release(release: Boolean)
    }

    companion object {
        private fun healthOf(player: Player): Float {
            return player.health.toFloat()
        }

        val Sprint = playerLockOf(Alexandria.namespaced("sprint")) { player, acquire, _ ->
            if (acquire) {
                player.sendPacket(WrapperPlayServerUpdateHealth(healthOf(player), NO_SPRINT_FOOD, 5f))
            }
            OnRelease { release ->
                if (release) {
                    player.sendPacket(WrapperPlayServerUpdateHealth(healthOf(player), player.foodLevel, player.saturation))
                }
            }
        }

        val Jump = playerLockOf(Alexandria.namespaced("jump")) { player, _, _ ->
            OnRelease { release ->
                if (release) {
                    player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.JUMP_BOOST))
                }
            }
        }

        private val MoveModifier = AttributeModifier(
            UUID(399141689, 766420721),
            "alexandria.move", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val Move = playerLockOf(Alexandria.namespaced("move")) { player, acquire, locks ->
            val attr = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
            val (sprintLock) = locks.acquire(player, Sprint)
            val (jumpLock) = locks.acquire(player, Jump)
            if (acquire) {
                attr?.forceModifier(MoveModifier)
            }
            OnRelease { release ->
                locks.release(player, sprintLock)
                locks.release(player, jumpLock)
                if (release) {
                    attr?.removeModifier(MoveModifier)
                }
            }
        }

        private val AttackModifier = AttributeModifier(
            UUID(156548602, 506633689),
            "alexandria.attack", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val Attack = playerLockOf(Alexandria.namespaced("attack")) { player, acquire, _ ->
            val attr = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)
            if (acquire) {
                attr?.forceModifier(AttackModifier)
            }
            OnRelease { release ->
                if (release) {
                    attr?.removeModifier(AttackModifier)
                }
            }
        }
    }
}

fun playerLockOf(name: String, acquire: (Player, Boolean, PlayerLocks) -> OnRelease) = object : PlayerLock {
    override val name get() = name

    override fun acquire(player: Player, acquire: Boolean, locks: PlayerLocks): OnRelease {
        return acquire(player, acquire, locks)
    }
}

data class PlayerLockInstance(
    val id: Long,
    val type: PlayerLock,
    val onRelease: OnRelease,
    val threadName: String,
    val stackTrace: List<StackTraceElement>,
)

class PlayerLocks internal constructor(private val alexandria: Alexandria) {
    inner class ForPlayer {
        private val nextLockId = AtomicLong()

        internal val _locks = HashMap<Long, PlayerLockInstance>()
        val locks: Map<Long, PlayerLockInstance> get() = _locks

        internal val _byType = HashMap<PlayerLock, MutableSet<PlayerLockInstance>>()
        val byType: Map<PlayerLock, Set<PlayerLockInstance>> get() = _byType

        fun nextLockId() = nextLockId.getAndIncrement()
    }

    private val _players = HashMap<Player, ForPlayer>()
    val players: Map<Player, ForPlayer> get() = _players

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                releaseAll(player)
            }
        })
    }

    operator fun get(player: Player) = _players[player]

    fun hasById(player: Player, lockId: Long): Boolean {
        return _players[player]?._locks?.contains(lockId) == true
    }

    fun hasByType(player: Player, lock: PlayerLock): Boolean {
        return _players[player]?._byType?.get(lock)?.isNotEmpty() == true
    }

    fun acquire(player: Player, lock: PlayerLock): PlayerLockInstance {
        val forPlayer = _players.computeIfAbsent(player) { ForPlayer() }
        val byType = forPlayer._byType.computeIfAbsent(lock) { HashSet() }

        val id = forPlayer.nextLockId()
        val onRelease = lock.acquire(player, byType.isEmpty(), this)
        val thread = Thread.currentThread()

        return PlayerLockInstance(id, lock, onRelease, thread.name, thread.stackTrace.toList()).also {
            forPlayer._locks[id] = it
            byType.add(it)
        }
    }

    fun release(player: Player, lockId: Long) {
        _players[player]?.let { forPlayer ->
            forPlayer._locks.remove(lockId)?.let { instance ->
                val byType = forPlayer._byType[instance.type]
                    ?: throw IllegalStateException("No byType entry for $instance")
                byType.remove(instance)
                instance.onRelease.release(byType.isEmpty())
            } ?: alexandria.log.line(LogLevel.Warning) { "Attempted to release lock with ID $lockId for ${player.name}, which was not held" }
        }
    }

    fun releaseAll(player: Player) {
        _players[player]?.let { forPlayer ->
            forPlayer._locks.forEach { (id) ->
                release(player, id)
            }
        }
        _players.remove(player)
    }

    fun releaseAll() {
        _players.forEach { (player) -> releaseAll(player) }
    }
}

val Player.locks get() = AlexandriaAPI.playerLocks[this]

fun Player.hasLockById(lockId: Long) = AlexandriaAPI.playerLocks.hasById(this, lockId)

fun Player.hasLockByType(lock: PlayerLock) = AlexandriaAPI.playerLocks.hasByType(this, lock)

fun Player.acquireLock(lock: PlayerLock) = AlexandriaAPI.playerLocks.acquire(this, lock)

fun Player.releaseLock(lock: Long) = AlexandriaAPI.playerLocks.release(this, lock)
