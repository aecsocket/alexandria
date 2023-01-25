package io.gitlab.aecsocket.alexandria.paper.effect

import io.gitlab.aecsocket.alexandria.core.physics.Vector3

interface Effector {
    fun playSound(effect: SoundEffect, position: Vector3)

    fun showParticle(effect: ParticleEffect, position: Vector3)

    companion object {
        val Empty: Effector = EmptyEffector
    }
}

private object EmptyEffector : Effector {
    override fun playSound(effect: SoundEffect, position: Vector3) {}
    override fun showParticle(effect: ParticleEffect, position: Vector3) {}
}

fun interface ForwardingEffector : Effector {
    fun effectors(): Iterable<Effector>

    override fun playSound(effect: SoundEffect, position: Vector3) =
        effectors().forEach { it.playSound(effect, position) }

    override fun showParticle(effect: ParticleEffect, position: Vector3) =
        effectors().forEach { it.showParticle(effect, position) }
}
