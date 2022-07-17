package com.gitlab.aecsocket.alexandria.core.effect

import com.gitlab.aecsocket.alexandria.core.physics.Vector3

interface Effector {
    fun playSound(effect: SoundEffect, position: Vector3)

    fun showParticle(effect: ParticleEffect, position: Vector3)

    companion object {
        val EMPTY: Effector = EmptyEffector
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

fun Iterable<SoundEffect>.play(effector: Effector, position: Vector3) =
    forEach { effector.playSound(it, position) }

fun Iterable<ParticleEffect>.show(effector: Effector, position: Vector3) =
    forEach { effector.showParticle(it, position) }
