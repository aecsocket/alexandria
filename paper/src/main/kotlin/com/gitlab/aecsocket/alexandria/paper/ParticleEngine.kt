package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.effect.Effector
import com.gitlab.aecsocket.alexandria.paper.effect.ParticleEffect
import com.gitlab.aecsocket.alexandria.paper.extension.position
import com.gitlab.aecsocket.alexandria.paper.extension.trackedPlayers
import org.bukkit.Location
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class ParticleEngineEffect(
    @Setting(nodeFromParent = true) val effects: List<ParticleEffect> = emptyList(),
) {
    fun map(transform: (ParticleEffect) -> ParticleEffect) = ParticleEngineEffect(effects.map(transform))

    companion object {
        val Empty = ParticleEngineEffect()
    }
}

class ParticleEngine internal constructor(
    private val alexandria: Alexandria
) {
    fun spawn(effect: ParticleEngineEffect, position: Vector3, targets: Iterable<Effector>) {
        if (!alexandria.isEnabled) return

        targets.forEach { effector ->
            effect.effects.forEach {
                effector.showParticle(it, position)
            }
        }
    }

    fun spawn(effect: ParticleEngineEffect, position: Vector3, target: Effector) {
        spawn(effect, position, setOf(target))
    }

    fun spawn(effect: ParticleEngineEffect, location: Location) {
        spawn(effect, location.position(), location.chunk.trackedPlayers().map { it.alexandria.effector })
    }
}
