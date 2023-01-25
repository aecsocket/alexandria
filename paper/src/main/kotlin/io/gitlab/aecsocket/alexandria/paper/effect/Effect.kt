package io.gitlab.aecsocket.alexandria.paper.effect

import io.gitlab.aecsocket.alexandria.core.physics.Vector3
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Particle

data class SoundEffect(
    val sound: Sound,
    val dropoff: Double,
    val range: Double
) {
    fun copy(
        name: Key = sound.name(),
        source: Sound.Source = sound.source(),
        volume: Float = sound.volume(),
        pitch: Float = sound.pitch(),
        dropoff: Double = this.dropoff,
        range: Double = this.range
    ) = SoundEffect(Sound.sound(name, source, volume, pitch), dropoff, range)
}

data class ParticleEffect(
    val particle: Particle,
    val count: Float = 0f,
    val size: Vector3 = Vector3.Zero,
    val speed: Double = 0.0,
    val data: Any? = null
)
