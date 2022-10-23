package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.potion.PotionType
import com.github.retrooper.packetevents.protocol.potion.PotionTypes
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth
import com.gitlab.aecsocket.alexandria.core.extension.clamp
import org.bukkit.entity.Player
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SteeringInputs(
    val forwards: Float = 0f,
    val sideways: Float = 0f,
    val jump: Boolean = false,
    val dismount: Boolean = false,
)

class AlexandriaPlayer internal constructor(
    private val alexandria: Alexandria,
    val handle: Player
) {
    var steering: SteeringInputs = SteeringInputs()
        private set

    private val features = HashMap<PlayerFeature<*>, PlayerFeature.PlayerData>()

    @Suppress("UNCHECKED_CAST")
    fun <P : PlayerFeature.PlayerData> featureData(feature: PlayerFeature<P>): P =
        (features.computeIfAbsent(feature) { feature.createFor(this) }) as P

    internal fun dispose() {
        features.forEach { (_, data) -> data.dispose() }
    }

    internal fun update() {
        fun effect(type: PotionType, amplifier: Int) {
            handle.sendPacket(WrapperPlayServerEntityEffect(handle.entityId, type, amplifier, 1, 0))
        }

        if (hasLockByType(PlayerLock.Jump)) {
            effect(PotionTypes.JUMP_BOOST, -127)
        }
        if (hasLockByType(PlayerLock.Interact)) {
            effect(PotionTypes.HASTE, -127)
        }
        if (hasLockByType(PlayerLock.Dig)) {
            effect(PotionTypes.MINING_FATIGUE, 127)
            effect(PotionTypes.HASTE, -127)
        }
    }

    internal fun onPacketSend(event: PacketSendEvent) {
        when (event.packetType) {
            PacketType.Play.Server.UPDATE_HEALTH -> {
                val packet = WrapperPlayServerUpdateHealth(event)
                if (hasLockByType(PlayerLock.Sprint)) {
                    packet.food = NO_SPRINT_FOOD
                }
            }
        }
    }

    internal fun onPacketReceive(event: PacketReceiveEvent) {
        when (event.packetType) {
            PacketType.Play.Client.STEER_VEHICLE -> {
                val packet = WrapperPlayClientSteerVehicle(event)
                steering = SteeringInputs(
                    clamp(packet.forward, -1f, 1f),
                    clamp(packet.sideways, -1f, 1f),
                    packet.isJump,
                    packet.isUnmount
                )
            }
        }
    }
}

val Player.alexandria: AlexandriaPlayer
    get() = AlexandriaAPI.playerOf(this)
