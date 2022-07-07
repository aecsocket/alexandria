package com.github.aecsocket.alexandria.core.effect

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.sound.Sound.sound

data class SoundEffect(
    val sound: Sound,
    val dropoff: Double,
    val range: Double
) {
    val sqrDropoff = dropoff * dropoff

    val sqrRange = range * range

    fun copy(
        name: Key = sound.name(),
        source: Source = sound.source(),
        volume: Float = sound.volume(),
        pitch: Float = sound.pitch(),
        dropoff: Double = this.dropoff,
        range: Double = this.range
    ) = SoundEffect(sound(name, source, volume, pitch), dropoff, range)
}
