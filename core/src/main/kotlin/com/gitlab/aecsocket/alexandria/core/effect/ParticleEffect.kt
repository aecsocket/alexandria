package com.gitlab.aecsocket.alexandria.core.effect

import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import net.kyori.adventure.key.Key

data class ParticleEffect(
    val particle: Key,
    val count: Double = 0.0,
    val size: Vector3 = Vector3.Zero,
    val speed: Double = 0.0,
    val data: Any? = null
)
