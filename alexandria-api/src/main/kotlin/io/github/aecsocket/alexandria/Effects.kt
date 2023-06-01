package io.github.aecsocket.alexandria

import io.github.aecsocket.klam.DVec3
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

interface RawParticle<D>

@ConfigSerializable
data class ParticleEffect<D>(
    @Required val particle: RawParticle<D>,
    val count: Int = 0,
    val size: DVec3 = DVec3(0.0),
    val speed: Double = 0.0,
    val data: D,
)
