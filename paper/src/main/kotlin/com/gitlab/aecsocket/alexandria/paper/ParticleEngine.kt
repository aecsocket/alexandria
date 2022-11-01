package com.gitlab.aecsocket.alexandria.paper

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
    companion object {
        val Empty = ParticleEngineEffect()
    }
}

class ParticleEngine internal constructor(
    private val alexandria: Alexandria
) {
    fun spawn(location: Location, effect: ParticleEngineEffect) {
        if (!alexandria.isEnabled) return

        val players = location.chunk.trackedPlayers().map { it.alexandria.effector }
        val position = location.position()

        players.forEach { player ->
            effect.effects.forEach {
                player.showParticle(it, position)
            }
        }
    }
}
