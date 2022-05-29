package com.github.aecsocket.alexandria.core.effect

import com.github.aecsocket.alexandria.core.vector.Vector3
import net.kyori.adventure.key.Key

data class ParticleEffect(
    val particle: Key,
    val count: Double,
    val size: Vector3,
    val speed: Double,
    val data: Any? = null
)
