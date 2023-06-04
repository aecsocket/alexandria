package io.github.aecsocket.alexandria.desc

import io.github.aecsocket.klam.DVec3
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

interface RawParticle

@ConfigSerializable
data class ParticleDesc(
    @Required val type: RawParticle,
    val count: Int = 0,
    val size: DVec3 = DVec3(0.0),
    val speed: Double = 0.0,
)
