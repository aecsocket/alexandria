package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle
import com.gitlab.aecsocket.alexandria.core.extension.clamp
import com.gitlab.aecsocket.alexandria.paper.extension.scheduleRepeating
import org.bukkit.entity.Player
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SteeringInputs(
    val forwards: Float,
    val sideways: Float,
    val jump: Boolean,
    val dismount: Boolean,
)

class PlayerTracking internal constructor(
    private val alexandria: Alexandria
) : PacketListenerAbstract() {
    private val _steering = HashMap<Player, SteeringInputs>()
    val steering: Map<Player, SteeringInputs> get() = _steering

    internal fun enable() {
        PacketEvents.getAPI().eventManager.registerListener(this)
        alexandria.scheduleRepeating {
            _steering.clear()
        }
    }

    fun steering(player: Player) = _steering[player]

    override fun onPacketReceive(event: PacketReceiveEvent) {
        val player = event.player as? Player ?: return
        when (event.packetType) {
            PacketType.Play.Client.STEER_VEHICLE -> {
                val packet = WrapperPlayClientSteerVehicle(event)
                _steering[player] = SteeringInputs(
                    clamp(packet.forward, -1f, 1f),
                    clamp(packet.sideways, -1f, 1f),
                    packet.isJump,
                    packet.isUnmount
                )
            }
        }
    }
}
