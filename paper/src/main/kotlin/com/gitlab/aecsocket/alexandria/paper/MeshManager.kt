package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.player.Equipment
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.*
import com.gitlab.aecsocket.alexandria.core.extension.EulerOrder
import com.gitlab.aecsocket.alexandria.core.extension.degrees
import com.gitlab.aecsocket.alexandria.core.extension.euler
import com.gitlab.aecsocket.alexandria.core.physics.Transform
import com.gitlab.aecsocket.alexandria.paper.extension.bukkitNextEntityId
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.atomic.AtomicLong

sealed interface Mesh {
    val id: Long
    var item: ItemStack
    var transform: Transform

    fun trackedPlayers(): Iterable<Player>

    fun spawn(players: Iterable<Player>)

    fun spawn(player: Player) = spawn(setOf(player))

    fun remove(players: Iterable<Player>)

    fun remove(player: Player) = remove(setOf(player))
}

class MeshManager internal constructor(
    private val alexandria: Alexandria,
) : PacketListener {
    private val nextMeshId = AtomicLong()

    private val _meshes = HashMap<Long, Mesh>()
    val meshes: Map<Long, Mesh> get() = _meshes

    fun nextMeshId() = nextMeshId.getAndIncrement()

    operator fun contains(id: Long) = _meshes.contains(id)

    operator fun get(id: Long) = _meshes[id]

    fun create(
        item: ItemStack,
        transform: Transform,
        getTrackedPlayers: () -> Iterable<Player>,
        interpolated: Boolean = true
    ): Mesh {
        val id = nextMeshId()
        return (
            if (interpolated) InterpMesh(id, item, transform, getTrackedPlayers)
            else NonInterpMesh(id, item, transform, getTrackedPlayers)
        ).also {
            _meshes[id] = it
        }
    }

    fun remove(id: Long, update: Boolean = true): Mesh? {
        return _meshes.remove(id)?.also {
            if (update) {
                it.remove(it.trackedPlayers())
            }
        }
    }

    sealed class BaseMesh(
        override val id: Long,
        item: ItemStack,
        private val getTrackedPlayers: () -> Iterable<Player>,
        private val yOffset: Double,
    ) : Mesh {
        val entityId = bukkitNextEntityId

        override var item = item
            set(value) {
                field = value
                update(item())
            }

        override fun trackedPlayers() = getTrackedPlayers()

        protected fun update(packets: Iterable<PacketWrapper<*>>) {
            val players = getTrackedPlayers()
            players.forEach { player ->
                packets.forEach { player.sendPacket(it) }
            }
        }

        private fun item() = listOf(
            WrapperPlayServerEntityEquipment(entityId, listOf(
                Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(item))
            ))
        )

        protected fun position(transform: Transform) = transform.translation.y { it - yOffset }.run {
            Vector3d(x, y, z)
        }

        protected fun headRotation(transform: Transform) = transform.rotation.euler(EulerOrder.ZYX).x { -it }.degrees.run {
            Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
        }

        protected fun transform(positionId: Int, rotationId: Int) = listOf(
            WrapperPlayServerEntityTeleport(positionId,
                position(transform), 0f, 0f, false),
            WrapperPlayServerEntityMetadata(rotationId, listOf(
                EntityData(16, EntityDataTypes.ROTATION, headRotation(transform))
            ))
        )

        protected fun spawnStand(position: Vector3d) = listOf(
            WrapperPlayServerSpawnEntity(entityId,
                Optional.of(UUID.randomUUID()), EntityTypes.ARMOR_STAND,
                position, 0f, 0f, 0f, 0, Optional.empty()
            ),
            WrapperPlayServerEntityMetadata(entityId, listOf(
                EntityData(0, EntityDataTypes.BYTE, (0x20).toByte()),
                EntityData(15, EntityDataTypes.BYTE, (0x10).toByte()),
                EntityData(16, EntityDataTypes.ROTATION, headRotation(transform)),
            ))
        ) + item()

        override fun spawn(players: Iterable<Player>) {
            val position = position(transform)
            val headRotation = headRotation(transform)

            val packets = listOf(
                WrapperPlayServerSpawnEntity(entityId,
                    Optional.of(UUID.randomUUID()), EntityTypes.ARMOR_STAND,
                    position, 0f, 0f, 0f, 0, Optional.empty()
                ),
                WrapperPlayServerEntityMetadata(entityId, listOf(
                    EntityData(0, EntityDataTypes.BYTE, (0x20).toByte()),
                    EntityData(15, EntityDataTypes.BYTE, (0x10).toByte()),
                    EntityData(16, EntityDataTypes.ROTATION, headRotation),
                ))
            ) + item()

            players.forEach { player ->
                packets.forEach { player.sendPacket(it) }
            }
        }
    }

    class InterpMesh internal constructor(
        id: Long,
        item: ItemStack,
        transform: Transform,
        getTrackedPlayers: () -> Iterable<Player>,
    ) : BaseMesh(id, item, getTrackedPlayers, 1.45) {
        override var transform = transform
            set(value) {
                field = value
                update(transform(entityId, entityId))
            }

        override fun spawn(players: Iterable<Player>) {
            val packets = spawnStand(position(transform))
            players.forEach { player ->
                packets.forEach { player.sendPacket(it) }
            }
        }

        override fun remove(players: Iterable<Player>) {
            val packet = WrapperPlayServerDestroyEntities(entityId)
            players.forEach { player ->
                player.sendPacket(packet)
            }
        }
    }

    class NonInterpMesh internal constructor(
        id: Long,
        item: ItemStack,
        transform: Transform,
        getTrackedPlayers: () -> Iterable<Player>,
    ) : BaseMesh(id, item, getTrackedPlayers, 1.85) {
        val vehicleId = bukkitNextEntityId

        override var transform = transform
            set(value) {
                field = value
                update(transform(vehicleId, entityId))
            }

        override fun spawn(players: Iterable<Player>) {
            val position = position(transform)
            val packets = spawnStand(position) + listOf(
                WrapperPlayServerSpawnEntity(vehicleId,
                    Optional.of(UUID.randomUUID()), EntityTypes.AREA_EFFECT_CLOUD,
                    position, 0f, 0f, 0f, 0, Optional.empty()
                ),
            )
            players.forEach { player ->
                packets.forEach { player.sendPacket(it) }
            }
        }

        override fun remove(players: Iterable<Player>) {
            val packet = WrapperPlayServerDestroyEntities(entityId, vehicleId)
            players.forEach { player ->
                player.sendPacket(packet)
            }
        }
    }
}
