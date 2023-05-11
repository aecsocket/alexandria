package io.github.aecsocket.alexandria.paper.render

import com.github.retrooper.packetevents.PacketEventsAPI
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityType
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.util.Quaternion4f
import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import io.github.aecsocket.alexandria.Billboard
import io.github.aecsocket.alexandria.TextAlignment
import io.github.aecsocket.alexandria.paper.extension.nextEntityId
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
import io.github.aecsocket.klam.asARGB
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import java.util.*

class DisplayRenders(private val packets: PacketEventsAPI<*>) : Renders {
    override fun createModel(descriptor: ModelDescriptor, basePosition: DVec3, transform: FAffine3): ModelRender {
        return DisplayModelRender(basePosition, transform, nextEntityId(), descriptor)
    }

    override fun createText(descriptor: TextDescriptor, basePosition: DVec3, transform: FAffine3): TextRender {
        return DisplayTextRender(basePosition, transform, nextEntityId(), descriptor)
    }

    private abstract inner class DisplayRender(
        basePosition: DVec3,
        transform: FAffine3,
        val protocolId: Int,
        descriptor: RenderDescriptor,
    ) : PaperRender {
        override var tracker = descriptor.tracker
        val billboard = descriptor.billboard
        val viewRange = descriptor.viewRange
        val interpolationDelay = descriptor.interpolationDelay
        val interpolationDuration = descriptor.interpolationDuration

        override var basePosition = basePosition
            get() = field
            set(value) {
                field = value
                val packet = WrapperPlayServerEntityTeleport(
                    protocolId,
                    value.run { Vector3d(x, y, z) },
                    0f,
                    0f,
                    false,
                )
                trackedPlayers().forEach { player ->
                    player.sendPacket(packet)
                }
            }

        override var transform = transform
            get() = field
            set(value) {
                field = value
                val packet = WrapperPlayServerEntityMetadata(protocolId, listOf(
                    metadataInterpolationDelay(),
                    metadataInterpolationDuration(),
                    metadataTranslation(),
                    metadataScale(),
                    metadataRotation(),
                    metadataViewRange(),
                ))
                trackedPlayers().forEach { player ->
                    player.sendPacket(packet)
                }
            }

        private fun metadataInterpolationDelay() =
            // 8: Display/Interpolation delay
            EntityData(8, EntityDataTypes.INT, interpolationDelay)

        private fun metadataInterpolationDuration() =
            // 9: Display/Interpolation duration
            EntityData(9, EntityDataTypes.INT, interpolationDuration)

        private fun metadataTranslation() =
            // 10: Display/Translation
            EntityData(10, EntityDataTypes.VECTOR3F, transform.position.run { Vector3f(x, y, z) })

        private fun metadataScale() =
            // 11: Display/Scale
            EntityData(11, EntityDataTypes.VECTOR3F, transform.scale.run { Vector3f(x, y, z) })

        private fun metadataRotation() =
            // 12: Display/Rotation left
            EntityData(12, EntityDataTypes.QUATERNION, transform.rotation.run { Quaternion4f(x, y, z, w) })

        private fun metadataBillboarding() =
            // 14: Display/Billboard constraints
            EntityData(14, EntityDataTypes.BYTE, when (billboard) {
                Billboard.NONE -> 0
                Billboard.VERTICAL -> 1
                Billboard.HORIZONTAL -> 2
                Billboard.ALL -> 3
            }.toByte())

        private fun metadataViewRange() =
            // 16: Display/View range
            EntityData(16, EntityDataTypes.FLOAT, viewRange)

        protected abstract fun entityType(): EntityType

        protected abstract fun metadata(): List<EntityData>

        protected fun Player.sendPacket(packet: PacketWrapper<*>) =
            packets.playerManager.sendPacket(this, packet)

        override fun spawn(players: Iterable<Player>) {
            val packets = listOf(
                WrapperPlayServerSpawnEntity(
                    protocolId,
                    Optional.of(UUID.randomUUID()),
                    entityType(),
                    basePosition.run { Vector3d(x, y, z) },
                    0f,
                    0f,
                    0f,
                    0,
                    Optional.empty(),
                ),
                WrapperPlayServerEntityMetadata(protocolId, listOf(
                    metadataInterpolationDelay(),
                    metadataInterpolationDuration(),
                    metadataTranslation(),
                    metadataScale(),
                    metadataRotation(),
                    metadataViewRange(),
                ) + metadata()),
            )
            players.forEach { player ->
                packets.forEach { player.sendPacket(it) }
            }
        }

        override fun despawn(players: Iterable<Player>) {
            val packet = WrapperPlayServerDestroyEntities(protocolId)
            players.forEach { player ->
                player.sendPacket(packet)
            }
        }
    }

    private inner class DisplayModelRender(
        basePosition: DVec3,
        transform: FAffine3,
        netId: Int,
        descriptor: ModelDescriptor,
    ) : DisplayRender(basePosition, transform, netId, descriptor), ModelRender {
        override var item = descriptor.item
            set(value) {
                field = value
                val packet = WrapperPlayServerEntityMetadata(protocolId, listOf(
                    metadataItem(),
                ))
                trackedPlayers().forEach { player ->
                    player.sendPacket(packet)
                }
            }

        override fun entityType(): EntityType = EntityTypes.ITEM_DISPLAY

        private fun metadataItem() =
            // 22: Item Display/Displayed item
            EntityData(22, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(item))

        override fun metadata() = listOf(
            metadataItem(),
        )
    }

    private inner class DisplayTextRender(
        basePosition: DVec3,
        transform: FAffine3,
        netId: Int,
        descriptor: TextDescriptor,
    ) : DisplayRender(basePosition, transform, netId, descriptor), TextRender {
        val lineWidth = descriptor.lineWidth
        val backgroundColor = asARGB(descriptor.backgroundColor)
        val hasShadow = descriptor.hasShadow
        val isSeeThrough = descriptor.isSeeThrough
        val alignment = descriptor.alignment
        override var text = descriptor.text
            set(value) {
                field = value
                val packet = WrapperPlayServerEntityMetadata(protocolId, listOf(
                    metadataText(),
                ))
                trackedPlayers().forEach { player ->
                    player.sendPacket(packet)
                }
            }

        override fun entityType(): EntityType = EntityTypes.TEXT_DISPLAY

        private fun metadataText() =
            // 22: Text Display/Text
            EntityData(22, EntityDataTypes.COMPONENT, GsonComponentSerializer.gson().serialize(text))

        override fun metadata() = listOf(
            metadataText(),
            // 23: Text Display/Line width
            EntityData(23, EntityDataTypes.INT, lineWidth),
            // 24: Text Display/Background color
            EntityData(24, EntityDataTypes.INT, backgroundColor),
            // 26: Text Display/Bitfield
            EntityData(26, EntityDataTypes.BYTE, (
                (if (hasShadow) 0x1 else 0) or
                (if (isSeeThrough) 0x2 else 0) or
                when (alignment) {
                    TextAlignment.CENTER -> 0
                    TextAlignment.LEFT -> 0x8
                    TextAlignment.RIGHT -> 0x10
                }).toByte()
            )
        )
    }
}
