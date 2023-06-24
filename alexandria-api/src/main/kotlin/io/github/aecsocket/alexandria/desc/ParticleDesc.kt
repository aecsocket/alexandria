package io.github.aecsocket.alexandria.desc

import io.github.aecsocket.klam.*
import net.kyori.adventure.key.Key
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Required

/**
 * Describes the base type of particle created by a [ParticleDesc].
 */
sealed interface ParticleType {
    /**
     * A particle type described by a namespaced key.
     */
    data class Keyed(val key: Key) : ParticleType

    /**
     * Platform-specific type.
     */
    interface Raw : ParticleType
}

/**
 * Descriptor for a spawnable particle.
 * @param type The base type of particle.
 * @param count The number of particles spawned.
 * @param size The space in which particles are spawned, or direction of particle if `count` is 0.
 * @param speed The speed that particles travel at, or extra miscellaneous data.
 */
@ConfigSerializable
data class ParticleDesc(
    @Required val type: ParticleType,
    val count: Int = 0,
    val size: DVec3 = DVec3(0.0),
    val speed: Double = 0.0,
) {
    init {
        require(count >= 0) { "requires count >= 0" }
        require(speed >= 0.0) { "requires speed >= 0.0" }
    }
}
