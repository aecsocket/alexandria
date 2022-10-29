package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.potion.PotionType
import com.github.retrooper.packetevents.protocol.potion.PotionTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect
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
import java.util.*
import java.util.concurrent.atomic.AtomicLong

internal const val NO_SPRINT_FOOD = 6

// note: you are NOT ALLOWED to add another lock when acquiring a lock
interface PlayerLock {
    data class OnAcquire(
        val player: Player,
        val acquire: Boolean,
    )

    val name: String

    fun acquire(ctx: OnAcquire): OnRelease

    fun interface OnRelease {
        fun release()
    }

    companion object {
        private fun healthOf(player: Player): Float {
            return player.health.toFloat()
        }

        val Sprint = playerLockOf(namespaced("sprint")) { (player, acquire) ->
            if (acquire) {
                player.sendPacket(WrapperPlayServerUpdateHealth(healthOf(player), NO_SPRINT_FOOD, 5f))
            }
            OnRelease {
                player.sendPacket(WrapperPlayServerUpdateHealth(healthOf(player), player.foodLevel, player.saturation))
            }
        }

        val Jump = playerLockOf(namespaced("jump")) { (player) ->
            OnRelease {
                player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.JUMP_BOOST))
            }
        }

        private val MoveModifier = AttributeModifier(
            UUID(399141689, 766420721),
            "alexandria.move", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val Move = playerLockOf(namespaced("move")) { (player, acquire) ->
            val attr = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
            if (acquire) {
                attr?.forceModifier(MoveModifier)
            }
            OnRelease {
                attr?.removeModifier(MoveModifier)
            }
        }

        private val AttackModifier = AttributeModifier(
            UUID(156548602, 506633689),
            "alexandria.attack", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val Attack = playerLockOf(namespaced("attack")) { (player, acquire) ->
            val attr = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            if (acquire) {
                attr?.forceModifier(AttackModifier)
            }
            OnRelease {
                attr?.removeModifier(AttackModifier)
            }
        }

        val Interact = playerLockOf(namespaced("interact")) {
            OnRelease {}
        }

        val Dig = playerLockOf(namespaced("dig")) { (player) ->
            OnRelease {
                player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.MINING_FATIGUE))
                player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.HASTE))
            }
        }

        val Place = playerLockOf(namespaced("place")) { (player) ->
            OnRelease {
                player.sendPacket(WrapperPlayServerRemoveEntityEffect(player.entityId, PotionTypes.HASTE))
            }
        }

        val Inventory = playerLockOf(namespaced("inventory")) {
            OnRelease {}
        }

        private val RaiseHandModifier = AttributeModifier(
            UUID(832329339, 657562654),
            "alexandria.raise_hand", -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1)

        val RaiseHand = playerLockOf(namespaced("raise_hand")) { (player, acquire) ->
            val attr = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)
            if (acquire) {
                attr?.forceModifier(RaiseHandModifier)
            }
            OnRelease {
                attr?.removeModifier(RaiseHandModifier)
            }
        }

        val UseAction = playerLockOf(namespaced("use_action")) {
            OnRelease {}
        }


        val AllMovement = listOf(Sprint, Jump, Move)

        val AllInteraction = listOf(Attack, Interact, Dig, Place)

        val All = AllMovement + AllInteraction + Inventory + RaiseHand + UseAction
    }
}

fun playerLockOf(name: String, acquire: (PlayerLock.OnAcquire) -> OnRelease) = object : PlayerLock {
    override val name get() = name

    override fun acquire(ctx: PlayerLock.OnAcquire): OnRelease {
        return acquire(ctx)
    }
}

data class PlayerLockInstance(
    val id: Long,
    val type: PlayerLock,
    val onRelease: OnRelease,
    val threadName: String,
    val stackTrace: List<StackTraceElement>,
) {
    override fun toString() = "PlayerLockInstance(${type.name}, id=$id)"
}

class PlayerLocks internal constructor(
    private val alexandria: Alexandria
) : PlayerFeature<PlayerLocks.PlayerData> {
    inner class PlayerData internal constructor(
        private val player: AlexandriaPlayer
    ) : PlayerFeature.PlayerData {
        private val nextLockId = AtomicLong()

        internal val _locks = HashMap<Long, PlayerLockInstance>()
        val locks: Map<Long, PlayerLockInstance> get() = _locks

        internal val _byType = HashMap<PlayerLock, MutableSet<PlayerLockInstance>>()
        val byType: Map<PlayerLock, Set<PlayerLockInstance>> get() = _byType

        fun nextLockId() = nextLockId.getAndIncrement()

        override fun dispose() {
            releaseAll(player)
        }

        override fun update() {
            fun effect(type: PotionType, amplifier: Int) {
                player.handle.sendPacket(WrapperPlayServerEntityEffect(player.handle.entityId, type, amplifier, 1, 0))
            }

            if (hasByType(player, PlayerLock.Jump)) {
                effect(PotionTypes.JUMP_BOOST, -127)
            }
            if (hasByType(player, PlayerLock.Interact)) {
                effect(PotionTypes.HASTE, -127)
            }
            if (hasByType(player, PlayerLock.Dig)) {
                effect(PotionTypes.MINING_FATIGUE, 127)
                effect(PotionTypes.HASTE, -127)
            }
        }

        override fun onPacketSend(event: PacketSendEvent) {
            when (event.packetType) {
                PacketType.Play.Server.UPDATE_HEALTH -> {
                    val packet = WrapperPlayServerUpdateHealth(event)
                    if (hasByType(player, PlayerLock.Sprint)) {
                        packet.food = NO_SPRINT_FOOD
                    }
                }
            }
        }
    }

    data class OnLockAcquire(
        val player: AlexandriaPlayer,
        val lock: PlayerLockInstance,
    )

    data class OnLockRelease(
        val player: AlexandriaPlayer,
        val lock: PlayerLockInstance,
    )

    private val onLockAcquire = ArrayList<(OnLockAcquire) -> Unit>()
    private val onLockRelease = ArrayList<(OnLockRelease) -> Unit>()

    override fun createFor(player: AlexandriaPlayer) = PlayerData(player)

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            fun cancelIfLock(event: Cancellable, player: Player, type: PlayerLock) {
                if (hasByType(alexandria.playerFor(player), type)) {
                    event.isCancelled = true
                }
            }

            @EventHandler(priority = EventPriority.LOW)
            fun on(event: EntityDamageByEntityEvent) {
                val damager = event.damager
                if (damager is Player) cancelIfLock(event, damager, PlayerLock.Attack)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun on(event: BlockBreakEvent) {
                cancelIfLock(event, event.player, PlayerLock.Dig)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun on(event: BlockPlaceEvent) {
                cancelIfLock(event, event.player, PlayerLock.Place)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun on(event: PlayerInteractEvent) {
                cancelIfLock(event, event.player, PlayerLock.Interact)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun on(event: InventoryClickEvent) {
                val player = event.whoClicked
                if (player is Player) cancelIfLock(event, player, PlayerLock.Inventory)
            }

            @EventHandler(priority = EventPriority.LOW)
            fun on(event: InventoryDragEvent) {
                val player = event.whoClicked
                if (player is Player) cancelIfLock(event, player, PlayerLock.Inventory)
            }
        })
    }

    fun locksOf(player: AlexandriaPlayer) = player.featureData(this).locks

    fun hasById(player: AlexandriaPlayer, lockId: Long): Boolean {
        return player.featureData(this)._locks.contains(lockId)
    }

    fun hasByType(player: AlexandriaPlayer, lock: PlayerLock): Boolean {
        return player.featureData(this)._byType[lock]?.isNotEmpty() == true
    }

    fun acquire(player: AlexandriaPlayer, lock: PlayerLock): PlayerLockInstance {
        val data = player.featureData(this)
        val byType = data._byType.computeIfAbsent(lock) { HashSet() }
        val acquire = byType.isEmpty()

        val id = data.nextLockId()
        val onRelease = lock.acquire(PlayerLock.OnAcquire(player.handle, acquire))
        val thread = Thread.currentThread()

        return PlayerLockInstance(id, lock, onRelease, thread.name, thread.stackTrace.toList()).also {
            data._locks[id] = it
            byType.add(it)

            if (acquire) {
                val event = OnLockAcquire(player, it)
                onLockAcquire.forEach { it(event) }
            }
        }
    }

    fun release(player: AlexandriaPlayer, lockId: Long) {
        val data = player.featureData(this)
        data._locks.remove(lockId)?.let { lock ->
            val byType = data._byType[lock.type]
                ?: throw IllegalStateException("No byType entry for $lock")
            byType.remove(lock)

            if (byType.isEmpty()) {
                lock.onRelease.release()
                val event = OnLockRelease(player, lock)
                onLockRelease.forEach { it(event) }
            }
        } ?: alexandria.log.line(LogLevel.Warning) { "Attempted to release non-lock with ID $lockId for ${player.handle.name}" }
    }

    fun releaseAll(player: AlexandriaPlayer) {
        player.featureData(this)._locks.toMap().forEach { (id) ->
            release(player, id)
        }
    }

    fun onLockAcquire(listener: (OnLockAcquire) -> Unit) {
        onLockAcquire.add(listener)
    }

    fun onLockRelease(listener: (OnLockRelease) -> Unit) {
        onLockRelease.add(listener)
    }
}

val AlexandriaPlayer.locks get() = featureData(AlexandriaAPI.playerLocks)

fun AlexandriaPlayer.hasLockById(lockId: Long) = AlexandriaAPI.playerLocks.hasById(this, lockId)

fun AlexandriaPlayer.hasLockByType(lock: PlayerLock) = AlexandriaAPI.playerLocks.hasByType(this, lock)

fun AlexandriaPlayer.acquireLock(lock: PlayerLock) = AlexandriaAPI.playerLocks.acquire(this, lock)

fun AlexandriaPlayer.acquireLocks(locks: Iterable<PlayerLock>) = locks.map { acquireLock(it) }

fun AlexandriaPlayer.releaseLock(lock: Long) = AlexandriaAPI.playerLocks.release(this, lock)

fun AlexandriaPlayer.releaseLock(lock: PlayerLockInstance) = releaseLock(lock.id)

fun AlexandriaPlayer.releaseLocks(locks: Iterable<PlayerLockInstance>) = locks.map { releaseLock(it) }
