package com.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import com.gitlab.aecsocket.alexandria.core.extension.clamp
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*
import kotlin.collections.HashMap

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

    internal fun join() {
        AlexandriaTeams.ColorToTeam.forEach { (color, teamName) ->
            handle.sendPacket(WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.CREATE,
                Optional.of(WrapperPlayServerTeams.ScoreBoardTeamInfo(
                    Component.empty(), null, null,
                    WrapperPlayServerTeams.NameTagVisibility.ALWAYS, WrapperPlayServerTeams.CollisionRule.ALWAYS,
                    color, WrapperPlayServerTeams.OptionData.NONE
                ))
            ))
        }
    }

    internal fun dispose() {
        features.forEach { (_, data) -> data.dispose() }
    }

    internal fun update() {
        features.forEach { (_, data) -> data.update() }
    }

    internal fun onPacketSend(event: PacketSendEvent) {
        features.forEach { (_, data) -> data.onPacketSend(event) }
    }

    internal fun onPacketReceive(event: PacketReceiveEvent) {
        features.forEach { (_, data) -> data.onPacketReceive(event) }
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
    get() = AlexandriaAPI.playerFor(this)
