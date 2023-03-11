package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.core.math.Vec3d
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

@ConfigSerializable
data class ParticleEffect(
    @Required val particle: Particle,
    val count: Float = 0.0f,
    val size: Vec3d = Vec3d.Zero,
    val speed: Double = 0.0,
    val data: Any? = null,
) {
    fun spawnTo(player: Player, position: Vec3d) {
        player.spawnParticle(
            particle,
            position.x, position.y, position.z,
            count.toInt(),
            size.x, size.y, size.z,
            speed, data,
        )
    }
}
