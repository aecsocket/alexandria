package io.gitlab.aecsocket.alexandria.paper

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import io.gitlab.aecsocket.alexandria.core.extension.clamp
import io.gitlab.aecsocket.alexandria.core.extension.clamp01
import io.gitlab.aecsocket.alexandria.core.extension.copy
import io.gitlab.aecsocket.alexandria.core.physics.Vector3
import io.gitlab.aecsocket.alexandria.paper.effect.Effector
import io.gitlab.aecsocket.alexandria.paper.effect.ParticleEffect
import io.gitlab.aecsocket.alexandria.paper.effect.SoundEffect
import io.gitlab.aecsocket.alexandria.paper.extension.position
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*
import kotlin.collections.HashMap

private const val CLIENT_STEER_SPEED = 0.98f
private const val CLIENT_STEER_MULT = 1f / CLIENT_STEER_SPEED

@ConfigSerializable
data class SteeringInputs(
    val forward: Float = 0f,
    val left: Float = 0f,
    val jump: Boolean = false,
    val dismount: Boolean = false,
)

class AlexandriaPlayer internal constructor(
    val handle: Player
) {
    internal var lastSwing = 0
    internal var lastClick = 0
    internal var digging = false

    val effector: Effector = PlayerEffector()
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
                WrapperPlayServerTeams.ScoreBoardTeamInfo(
                    Component.empty(), null, null,
                    WrapperPlayServerTeams.NameTagVisibility.ALWAYS, WrapperPlayServerTeams.CollisionRule.ALWAYS,
                    color, WrapperPlayServerTeams.OptionData.NONE
                )
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
                    clamp(packet.forward * CLIENT_STEER_MULT, -1f, 1f),
                    clamp(packet.sideways * CLIENT_STEER_MULT, -1f, 1f),
                    packet.isJump,
                    packet.isUnmount
                )
            }
        }
    }
    
    private inner class PlayerEffector : Effector {
        override fun playSound(effect: SoundEffect, position: Vector3) {
            val playerPos = handle.location.position()
            val delta = position - playerPos
            val distance = delta.length
            val (dx, dy, dz) = (delta / distance) * 8.0
            val volume = (1 - clamp01((distance - effect.dropoff) / (effect.range - effect.dropoff))).toFloat()
            handle.playSound(effect.sound.copy(volume = effect.sound.volume() * volume),
                position.x + dx, position.y + dy, position.z + dz)
        }

        override fun showParticle(effect: ParticleEffect, position: Vector3) {
            val (px, py, pz) = position
            val (sx, sy, sz) = effect.size
            handle.spawnParticle(effect.particle,
                px, py, pz, effect.count.toInt(),
                sx, sy, sz, effect.speed, effect.data)
        }
    }
}

val Player.alexandria: AlexandriaPlayer
    get() = AlexandriaAPI.playerFor(this)
