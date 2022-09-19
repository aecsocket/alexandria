package com.gitlab.aecsocket.alexandria.paper

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.packettype.PacketType
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
import com.gitlab.aecsocket.alexandria.paper.extension.registerEvents
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*

class MeshManager internal constructor(
    private val alexandria: Alexandria,
): PacketListener {
    private val _meshes = HashMap<Entity, Instance>()
    val meshes: Map<Entity, Instance> get() = _meshes

    operator fun contains(entity: Entity) = _meshes.contains(entity)

    operator fun get(entity: Entity) = _meshes[entity]

    internal fun enable() {
        alexandria.registerEvents(object : Listener {
            @EventHandler
            fun EntityRemoveFromWorldEvent.on() {
                remove(entity)
            }
        })
        PacketEvents.getAPI().eventManager.registerListener(this, PacketListenerPriority.NORMAL)
    }

    fun create(entity: Entity, transform: Transform): Instance {
        if (_meshes.contains(entity))
            throw IllegalArgumentException("Mesh already exists for $entity (${entity.uniqueId})")
        return Instance(entity, transform).also {
            _meshes[entity] = it
        }
    }

    fun remove(entity: Entity) {
        _meshes.remove(entity)?.let { it.send(it.destroy()) }
    }

    override fun onPacketSend(event: PacketSendEvent) {
        val player = event.player as? Player ?: return
        when (event.packetType) {
            PacketType.Play.Server.SPAWN_ENTITY -> {
                val packet = WrapperPlayServerSpawnEntity(event)
                SpigotReflectionUtil.getEntityById(packet.entityId)?.let { entity ->
                    _meshes[entity]?.let { mesh ->
                        event.isCancelled = true
                        mesh.spawn().forEach { player.sendPacket(it) }
                    }
                }
            }
            PacketType.Play.Server.DESTROY_ENTITIES -> {
                val packet = WrapperPlayServerDestroyEntities(event)

                val entityIds = packet.entityIds.toMutableList()
                entityIds.removeIf { entityId ->
                    var remove = false
                    SpigotReflectionUtil.getEntityById(entityId)?.let { entity ->
                        _meshes[entity]?.let { mesh ->
                            mesh.destroy().forEach { player.sendPacket(it) }
                            remove = true
                        }
                    }
                    remove
                }
                packet.entityIds = entityIds.toIntArray()
            }
        }
    }

    inner class Instance internal constructor(
        val entity: Entity,
        meshTransform: Transform,
        parts: List<Part> = emptyList(),
    ) {
        var meshTransform = meshTransform
            set(value) {
                field = value
                send(transform())
            }

        private val _parts = parts.toMutableList()
        val parts: List<Part> get() = _parts

        fun addPart(part: Part, send: Boolean = true) {
            _parts.add(part)
            if (send)
                send(part.spawn())
        }

        fun removePart(part: Part, send: Boolean = true) {
            _parts.remove(part)
            if (send)
                send(setOf(WrapperPlayServerDestroyEntities(part.entityId)))
        }

        internal fun send(packets: Iterable<PacketWrapper<*>>) {
            entity.trackedPlayers.forEach { player ->
                packets.forEach { player.sendPacket(it) }
            }
        }

        internal fun transform(): List<PacketWrapper<*>> {
            return _parts.flatMap { it.partTransform() }
        }

        internal fun spawn(): List<PacketWrapper<*>> {
            return _parts.flatMap { it.spawn() }
        }

        internal fun destroy(): List<PacketWrapper<*>> {
            val entityIds = _parts.map { it.entityId }.toIntArray()
            return listOf(WrapperPlayServerDestroyEntities(*entityIds))
        }

        inner class Part(
            val entityId: Int,
            partTransform: Transform,
            item: ItemStack
        ) {
            var partTransform = partTransform
                set(value) {
                    field = value
                    send(transform())
                }

            var item = item
                set(value) {
                    field = value
                    send(item())
                }

            private fun position(transform: Transform) = transform.translation.y { it - 1.45 }.run {
                Vector3d(x, y, z)
            }

            private fun headRotation(transform: Transform) = transform.rotation.euler(EulerOrder.ZYX).x { -it }.degrees.run {
                Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
            }

            internal fun spawn(parent: Transform = meshTransform): List<PacketWrapper<*>> {
                val partTransform = parent + partTransform
                val position = position(partTransform)
                val headRotation = headRotation(partTransform)

                return listOf(
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
            }

            internal fun partTransform(parent: Transform = meshTransform): List<PacketWrapper<*>> {
                val transform = parent + partTransform
                val position = position(transform)
                val headRotation = headRotation(transform)

                return listOf(
                    WrapperPlayServerEntityTeleport(entityId,
                        position, 0f, 0f, false),
                    WrapperPlayServerEntityMetadata(entityId, listOf(
                        EntityData(16, EntityDataTypes.ROTATION, headRotation)
                    ))
                )
            }

            internal fun item(): List<PacketWrapper<*>> {
                return listOf(
                    WrapperPlayServerEntityEquipment(entityId, listOf(
                        Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(item))
                    ))
                )
            }
        }
    }
}
