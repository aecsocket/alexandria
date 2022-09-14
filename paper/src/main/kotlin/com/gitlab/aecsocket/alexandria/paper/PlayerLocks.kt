package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.protocol.potion.PotionTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth
import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.alexandria.paper.Alexandria.Companion.namespaced
import com.gitlab.aecsocket.alexandria.paper.PlayerLock.OnRelease
import com.gitlab.aecsocket.alexandria.paper.extension.forceModifier
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.atomic.AtomicLong

internal const val NO_SPRINT_FOOD = 6

// note: you are NOT ALLOWED to add another lock when acquiring a lock
interface PlayerLock {
    val name: String

    fun acquire(player: Player, acquire: Boolean): OnRelease

    fun interface OnRelease {
        fun release(release: Boolean)
    }

    companion object {
        private fun healthOf(player: Player): Float {
            return player.health.toFloat()
        }

        val Sprint = playerLockOf(namespaced("sprint")) { player, acquire ->
            if (acquire) {
                player.sendPacket(WrapperPlayServerUpdateHealth(healthOf(player), NO_SPRINT_FOOD, 5f))
            }
            OnRelease { release ->
                if (release) {
                    player.sendPacket(WrapperPlayServerUpdateHealth(healthOf(player), player.foodLevel, player.saturation))
                }
            }
        }

        val Jump = playerLockOf(namespaced("jump")) { player, _ ->
            OnRelease { release ->
                if (release) {
                    player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.JUMP_BOOST))
                }
            }
        }

        private val MoveModifier = AttributeModifier(
            UUID(399141689, 766420721),
            "alexandria.move", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val Move = playerLockOf(namespaced("move")) { player, acquire ->
            val attr = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
            if (acquire) {
                attr?.forceModifier(MoveModifier)
            }
            OnRelease { release ->
                if (release) {
                    attr?.removeModifier(MoveModifier)
                }
            }
        }

        private val AttackModifier = AttributeModifier(
            UUID(156548602, 506633689),
            "alexandria.attack", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val Attack = playerLockOf(namespaced("attack")) { player, acquire ->
            val attr = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            if (acquire) {
                attr?.forceModifier(AttackModifier)
            }
            OnRelease { release ->
                if (release) {
                    attr?.removeModifier(AttackModifier)
                }
            }
        }

        val Interact = playerLockOf(namespaced("interact")) { _, _ ->
            OnRelease {}
        }

        val Dig = playerLockOf(namespaced("dig")) { player, _ ->
            OnRelease { release ->
                if (release) {
                    player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.MINING_FATIGUE))
                    player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.HASTE))
                }
            }
        }

        val Place = playerLockOf(namespaced("place")) { player, _ ->
            OnRelease { release ->
                if (release) {
                    player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.HASTE))
                }
            }
        }

        val Inventory = playerLockOf(namespaced("inventory")) { _, _ ->
            OnRelease {}
        }

        private val RaiseHandModifier = AttributeModifier(
            UUID(832329339, 657562654),
            "alexandria.raise_hand", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val RaiseHand = playerLockOf(namespaced("raise_hand")) { player, acquire ->
            val attr = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)
            if (acquire) {
                attr?.forceModifier(RaiseHandModifier)
            }
            OnRelease { release ->
                if (release) {
                    attr?.removeModifier(RaiseHandModifier)
                }
            }
        }

        val UseAction = playerLockOf(namespaced("use_action")) { _, _ ->
            OnRelease {}
        }


        val AllMovement = listOf(Sprint, Jump, Move)

        val AllInteraction = listOf(Attack, Interact, Dig, Place)

        val All = AllMovement + AllInteraction + Inventory + RaiseHand + UseAction
    }
}

fun playerLockOf(name: String, acquire: (Player, Boolean) -> OnRelease) = object : PlayerLock {
    override val name get() = name

    override fun acquire(player: Player, acquire: Boolean): OnRelease {
        return acquire(player, acquire)
    }
}

data class PlayerLockInstance(
    val id: Long,
    val type: PlayerLock,
    val onRelease: OnRelease,
    val threadName: String,
    val stackTrace: List<StackTraceElement>,
)

class PlayerLocks internal constructor(
    private val alexandria: Alexandria
) {
    data class OnLockAcquire(
        val player: Player,
        val lock: PlayerLockInstance,
    )

    data class OnLockRelease(
        val player: Player,
        val lock: PlayerLockInstance,
    )

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

    val onLockAcquire: MutableList<(OnLockAcquire) -> Unit> = ArrayList()
    val onLockRelease: MutableList<(OnLockRelease) -> Unit> = ArrayList()

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun PlayerQuitEvent.on() {
                releaseAll(player)
            }

            fun Cancellable.cancelIfLock(player: Player, type: PlayerLock) {
                if (player.hasLockByType(type)) {
                    isCancelled = true
                }
            }

            @EventHandler(priority = EventPriority.LOW)
            fun EntityDamageByEntityEvent.on() {
                val damager = damager
                if (damager is Player) cancelIfLock(damager, PlayerLock.Attack)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun BlockBreakEvent.on() {
                cancelIfLock(player, PlayerLock.Dig)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun BlockPlaceEvent.on() {
                cancelIfLock(player, PlayerLock.Place)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun PlayerInteractEvent.on() {
                cancelIfLock(player, PlayerLock.Interact)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun InventoryClickEvent.on() {
                val player = whoClicked
                if (player is Player) {
                    cancelIfLock(player, PlayerLock.Inventory)
                }
            }

            @EventHandler(priority = EventPriority.LOW)
            fun InventoryDragEvent.on() {
                val player = whoClicked
                if (player is Player && player.hasLockByType(PlayerLock.Inventory)) {
                    isCancelled = true
                }
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
        val acquire = byType.isEmpty()

        val id = forPlayer.nextLockId()
        val onRelease = lock.acquire(player, acquire)
        val thread = Thread.currentThread()

        return PlayerLockInstance(id, lock, onRelease, thread.name, thread.stackTrace.toList()).also {
            forPlayer._locks[id] = it
            byType.add(it)

            if (acquire) {
                val event = OnLockAcquire(player, it)
                onLockAcquire.forEach { lst -> lst(event) }
            }
        }
    }

    fun release(player: Player, lockId: Long) {
        _players[player]?.let { forPlayer ->
            forPlayer._locks.remove(lockId)?.let { instance ->
                val byType = forPlayer._byType[instance.type]
                    ?: throw IllegalStateException("No byType entry for $instance")
                byType.remove(instance)

                val release = byType.isEmpty()
                instance.onRelease.release(release)

                if (release) {
                    val event = OnLockRelease(player, instance)
                    onLockRelease.forEach { lst -> lst(event) }
                }
            } ?: alexandria.log.line(LogLevel.Warning) { "Attempted to release lock with ID $lockId for ${player.name}, which was not held" }
        }
    }

    fun releaseAll(player: Player) {
        _players[player]?.let { forPlayer ->
            forPlayer._locks.toMutableMap().forEach { (id) ->
                release(player, id)
            }
        }
        _players.remove(player)
    }

    fun releaseAll() {
        _players.toMutableMap().forEach { (player) -> releaseAll(player) }
    }

    fun onLockAcquire(listener: (OnLockAcquire) -> Unit) {
        onLockAcquire.add(listener)
    }

    fun onLockRelease(listener: (OnLockRelease) -> Unit) {
        onLockRelease.add(listener)
    }
}

val Player.locks get() = AlexandriaAPI.playerLocks[this]

fun Player.hasLockById(lockId: Long) = AlexandriaAPI.playerLocks.hasById(this, lockId)

fun Player.hasLockByType(lock: PlayerLock) = AlexandriaAPI.playerLocks.hasByType(this, lock)

fun Player.acquireLock(lock: PlayerLock) = AlexandriaAPI.playerLocks.acquire(this, lock)

fun Player.acquireLocks(locks: Iterable<PlayerLock>) = locks.map { acquireLock(it) }

fun Player.releaseLock(lock: Long) = AlexandriaAPI.playerLocks.release(this, lock)

fun Player.releaseLock(lock: PlayerLockInstance) = releaseLock(lock.id)

fun Player.releaseLocks(locks: Iterable<PlayerLockInstance>) = locks.map { releaseLock(it) }
