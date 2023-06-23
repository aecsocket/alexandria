package io.github.aecsocket.alexandria.paper

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
import io.github.aecsocket.alexandria.*
import io.github.aecsocket.alexandria.paper.extension.sendPacket
import io.github.aecsocket.klam.*
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

private const val ENTITY_FLAGS = 0
private const val INTERPOLATION_DELAY = 8
private const val INTERPOLATION_DURATION = 9
private const val TRANSLATION = 10
private const val SCALE = 11
private const val LEFT_ROTATION = 12
private const val BILLBOARD = 14
private const val VIEW_RANGE = 16
private const val WIDTH = 19
private const val HEIGHT = 20
private const val GLOW_COLOR_OVERRIDE = 21

private const val ITEM = 22

private const val TEXT = 22
private const val LINE_WIDTH = 23
private const val BACKGROUND_COLOR = 24
private const val TEXT_OPACITY = 25
private const val TEXT_FLAGS = 26

fun interface PacketReceiver {
    fun send(packet: PacketWrapper<*>)
}

fun Entity.playerReceivers() = PacketReceiver { packet ->
    trackedPlayers.forEach { it.sendPacket(packet) }
}

fun Player.packetReceiver() = PacketReceiver { sendPacket(it) }

sealed class DisplayRender(
    val eid: Int,
    val receiver: PacketReceiver,
) : Render {
    protected abstract val entityType: EntityType

    abstract fun withReceiver(receiver: PacketReceiver): DisplayRender

    override fun position(value: DVec3) = this.also {
        receiver.send(WrapperPlayServerEntityTeleport(
            eid,
            value.run { Vector3d(x, y, z) },
            0.0f,
            0.0f,
            false,
        ))
    }

    override fun transform(value: FAffine3) = this.also {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(TRANSLATION, EntityDataTypes.VECTOR3F, value.translation.run { Vector3f(x, y, z) }),
                EntityData(SCALE, EntityDataTypes.VECTOR3F, value.scale.run { Vector3f(x, y, z) }),
                EntityData(LEFT_ROTATION, EntityDataTypes.QUATERNION, value.rotation.run { Quaternion4f(x, y, z, w) }),
            )
        ))
    }

    override fun interpolationDelay(value: Int) = this.also {
        require(value >= 0) { "requires value >= 0" }
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(INTERPOLATION_DELAY, EntityDataTypes.INT, value),
            )
        ))
    }

    override fun interpolationDuration(value: Int) = this.also {
        require(value >= 0) { "requires value >= 0" }
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(INTERPOLATION_DURATION, EntityDataTypes.INT, value),
            )
        ))
    }

    override fun billboard(value: Billboard) = this.also {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(BILLBOARD, EntityDataTypes.BYTE, when (value) {
                    Billboard.NONE -> 0
                    Billboard.VERTICAL -> 1
                    Billboard.HORIZONTAL -> 2
                    Billboard.ALL -> 3
                }),
            )
        ))
    }

    override fun viewRange(value: Float) = this.also {
        require(value >= 0.0) { "requires value >= 0.0" }
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(VIEW_RANGE, EntityDataTypes.FLOAT, value),
            )
        ))
    }

    override fun cullBox(value: FVec2) = this.also {
        require(value.x >= 0.0) { "requires value.x >= 0.0" }
        require(value.y >= 0.0) { "requires value.y >= 0.0" }
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(WIDTH, EntityDataTypes.FLOAT, value.x),
                EntityData(HEIGHT, EntityDataTypes.FLOAT, value.y),
            )
        ))
    }

    override fun glowing(value: Boolean) = this.also {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(ENTITY_FLAGS, EntityDataTypes.BYTE, (if (value) 0x40 else 0).toByte()),
            )
        ))
    }

    override fun glowColor(value: TextColor) = this.also {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(GLOW_COLOR_OVERRIDE, EntityDataTypes.INT, value.value()),
            )
        ))
    }

    override fun spawn(position: DVec3) = this.also {
        receiver.send(WrapperPlayServerSpawnEntity(
            eid,
            Optional.of(UUID.randomUUID()),
            entityType,
            position.run { Vector3d(x, y, z) },
            0.0f,
            0.0f,
            0.0f,
            0,
            Optional.of(Vector3d(0.0, 0.0, 0.0)),
        ))
    }

    override fun despawn() = this.also {
        receiver.send(WrapperPlayServerDestroyEntities(eid))
    }
}

class ItemDisplayRender(
    eid: Int,
    receiver: PacketReceiver,
) : DisplayRender(eid, receiver), ItemRender {
    override val entityType: EntityType get() = EntityTypes.ITEM_DISPLAY

    override fun position(value: DVec3) = this.also { super.position(value) }
    override fun transform(value: FAffine3) = this.also { super.transform(value) }
    override fun interpolationDelay(value: Int) = this.also { super.interpolationDelay(value) }
    override fun interpolationDuration(value: Int) = this.also { super.interpolationDuration(value) }
    override fun billboard(value: Billboard) = this.also { super.billboard(value) }
    override fun viewRange(value: Float) = this.also { super.viewRange(value) }
    override fun cullBox(value: FVec2) = this.also { super.cullBox(value) }
    override fun glowing(value: Boolean) = this.also { super.glowing(value) }
    override fun glowColor(value: TextColor) = this.also { super.glowColor(value) }
    override fun spawn(position: DVec3) = this.also { super.spawn(position) }
    override fun despawn() = this.also { super.despawn() }

    override fun withReceiver(receiver: PacketReceiver) = ItemDisplayRender(eid, receiver)

    fun item(value: ItemStack) = this.also {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(ITEM, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(value)),
            )
        ))
    }
}

class TextDisplayRender(
    eid: Int,
    receiver: PacketReceiver,
) : DisplayRender(eid, receiver), TextRender {
    override val entityType: EntityType get() = EntityTypes.TEXT_DISPLAY

    override fun position(value: DVec3) = this.also { super.position(value) }
    override fun transform(value: FAffine3) = this.also { super.transform(value) }
    override fun interpolationDelay(value: Int) = this.also { super.interpolationDelay(value) }
    override fun interpolationDuration(value: Int) = this.also { super.interpolationDuration(value) }
    override fun billboard(value: Billboard) = this.also { super.billboard(value) }
    override fun viewRange(value: Float) = this.also { super.viewRange(value) }
    override fun cullBox(value: FVec2) = this.also { super.cullBox(value) }
    override fun glowing(value: Boolean) = this.also { super.glowing(value) }
    override fun glowColor(value: TextColor) = this.also { super.glowColor(value) }
    override fun spawn(position: DVec3) = this.also { super.spawn(position) }
    override fun despawn() = this.also { super.despawn() }

    override fun withReceiver(receiver: PacketReceiver) = TextDisplayRender(eid, receiver)

    override fun text(value: Component) {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(TEXT, EntityDataTypes.COMPONENT, GsonComponentSerializer.gson().serialize(value)),
            )
        ))
    }

    override fun lineWidth(value: Int) {
        require(value > 0) { "requires value > 0" }
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(LINE_WIDTH, EntityDataTypes.INT, value),
            )
        ))
    }

    override fun backgroundColor(value: ARGB) {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(BACKGROUND_COLOR, EntityDataTypes.INT, argbToInt(value)),
            )
        ))
    }

    override fun textOpacity(value: Byte) {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(TEXT_OPACITY, EntityDataTypes.BYTE, value),
            )
        ))
    }

    override fun textFlags(value: TextFlags) {
        receiver.send(WrapperPlayServerEntityMetadata(
            eid,
            listOf(
                EntityData(TEXT_FLAGS, EntityDataTypes.BYTE, (
                    (if (value.hasShadow) 0x1 else 0) or
                    (if (value.isSeeThrough) 0x2 else 0) or
                    (if (value.defaultBackgroundColor) 0x4 else 0) or
                    when (value.alignment) {
                        TextAlignment.CENTER -> 0
                        TextAlignment.LEFT -> 0x8
                        TextAlignment.RIGHT -> 0x10
                    }
                ).toByte())
            )
        ))
    }
}
