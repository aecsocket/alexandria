package io.github.aecsocket.alexandria.fabric

import io.github.aecsocket.alexandria.*
import io.github.aecsocket.alexandria.fabric.extension.*
import io.github.aecsocket.alexandria.fabric.mixin.BroadcastAccess
import io.github.aecsocket.klam.*
import io.netty.buffer.Unpooled
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
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
    fun send(packet: Packet<*>)
}

fun ServerEntity.playerReceivers() = PacketReceiver {
    (this as BroadcastAccess).broadcast.accept(it)
}

fun ServerPlayer.packetReceiver() = PacketReceiver {
    connection.send(it)
}

private fun teleportEntityPacket(
    eid: Int,
    x: Double,
    y: Double,
    z: Double,
): Packet<*> {
    val bytes = FriendlyByteBuf(Unpooled.buffer())
    bytes.writeVarInt(eid)
    bytes.writeDouble(x)
    bytes.writeDouble(y)
    bytes.writeDouble(z)
    bytes.writeByte(0)
    bytes.writeByte(0)
    bytes.writeBoolean(false)
    println("bytes size = ${bytes.capacity()}")
    return ClientboundTeleportEntityPacket(bytes)
}

sealed class DisplayRender(
    val eid: Int,
    var receiver: PacketReceiver,
) : Render {
    protected abstract val entityType: EntityType<*>

    abstract fun withReceiver(receiver: PacketReceiver): DisplayRender

    override fun spawn(position: DVec3) = this.also {
        receiver.send(ClientboundAddEntityPacket(
            eid,
            UUID.randomUUID(),
            position.x, position.y, position.z,
            0.0f,
            0.0f,
            entityType,
            0,
            Vec3.ZERO,
            0.0,
        ))
    }

    override fun despawn() = this.also {
        receiver.send(ClientboundRemoveEntitiesPacket(eid))
    }

    override fun position(value: DVec3) = this.also {
        receiver.send(teleportEntityPacket(eid, value.x, value.y, value.z))
    }

    override fun transform(value: FAffine3) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(TRANSLATION, EntityDataSerializers.VECTOR3, value.translation.toVector3f()),
                SynchedEntityData.DataValue(SCALE, EntityDataSerializers.VECTOR3, value.scale.toVector3f()),
                SynchedEntityData.DataValue(LEFT_ROTATION, EntityDataSerializers.QUATERNION, value.rotation.toQuaternionf()),
            )
        ))
    }

    override fun interpolationDelay(value: Int) = this.also {
        require(value >= 0) { "requires value >= 0" }
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(INTERPOLATION_DELAY, EntityDataSerializers.INT, value),
            )
        ))
    }

    override fun interpolationDuration(value: Int) = this.also {
        require(value >= 0) { "requires value >= 0" }
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(INTERPOLATION_DURATION, EntityDataSerializers.INT, value),
            )
        ))
    }

    override fun billboard(value: Billboard) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(BILLBOARD, EntityDataSerializers.BYTE, when (value) {
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
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(VIEW_RANGE, EntityDataSerializers.FLOAT, value),
            )
        ))
    }

    override fun cullBox(value: FVec2) = this.also {
        require(value.x >= 0.0) { "requires value.x >= 0.0" }
        require(value.y >= 0.0) { "requires value.y >= 0.0" }
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(WIDTH, EntityDataSerializers.FLOAT, value.x),
                SynchedEntityData.DataValue(HEIGHT, EntityDataSerializers.FLOAT, value.y),
            )
        ))
    }

    override fun glowing(value: Boolean) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(ENTITY_FLAGS, EntityDataSerializers.BYTE, (if (value) 0x40 else 0).toByte()),
            )
        ))
    }

    override fun glowColor(value: TextColor) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(GLOW_COLOR_OVERRIDE, EntityDataSerializers.INT, value.value()),
            )
        ))
    }
}

class ItemDisplayRender(
    eid: Int,
    receiver: PacketReceiver,
) : DisplayRender(eid, receiver), ItemRender {
    override val entityType: EntityType<*> get() = EntityType.ITEM_DISPLAY

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
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(ITEM, EntityDataSerializers.ITEM_STACK, value),
            )
        ))
    }
}

class TextDisplayRender(
    eid: Int,
    receiver: PacketReceiver,
    private val audiences: FabricServerAudiences,
) : DisplayRender(eid, receiver), TextRender {
    override val entityType: EntityType<*> get() = EntityType.TEXT_DISPLAY

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

    override fun withReceiver(receiver: PacketReceiver) = TextDisplayRender(eid, receiver, audiences)

    override fun text(value: Component) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(TEXT, EntityDataSerializers.COMPONENT, audiences.toNative(value)),
            )
        ))
    }

    override fun lineWidth(value: Int) = this.also {
        require(value > 0) { "requires value > 0" }
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(LINE_WIDTH, EntityDataSerializers.INT, value),
            )
        ))
    }

    override fun backgroundColor(value: ARGB) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(BACKGROUND_COLOR, EntityDataSerializers.INT, argbToInt(value)),
            )
        ))
    }

    override fun textOpacity(value: Byte) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(TEXT_OPACITY, EntityDataSerializers.BYTE, value),
            )
        ))
    }

    override fun textFlags(value: TextFlags) = this.also {
        receiver.send(ClientboundSetEntityDataPacket(
            eid,
            listOf(
                SynchedEntityData.DataValue(TEXT_FLAGS, EntityDataSerializers.BYTE, (
                    (if (value.hasShadow) 0x1 else 0) or
                    (if (value.isSeeThrough) 0x2 else 0) or
                    (if (value.defaultBackgroundColor) 0x4 else 0) or
                    when (value.alignment) {
                        TextAlignment.CENTER -> 0
                        TextAlignment.LEFT -> 0x8
                        TextAlignment.RIGHT -> 0x10
                    }
                ).toByte()),
            )
        ))
    }
}
