package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.ParticleEffect
import io.github.aecsocket.alexandria.RawParticle
import io.github.aecsocket.klam.DVec3
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player

data class PaperParticle(
    val handle: Particle,
) : RawParticle<Any?>

fun <D> ParticleEffect<D>.spawn(player: Player, position: DVec3) {
    val particle = particle as PaperParticle
    player.spawnParticle(
        particle.handle,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed, data,
    )
}

fun <D> ParticleEffect<D>.spawn(world: World, position: DVec3, force: Boolean = false) {
    val particle = particle as PaperParticle
    world.spawnParticle(
        particle.handle,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed, data, force,
    )
}
