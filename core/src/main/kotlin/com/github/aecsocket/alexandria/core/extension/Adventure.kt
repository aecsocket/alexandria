package com.github.aecsocket.alexandria.core.extension

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound

fun Sound.copy(
    name: Key = name(),
    source: Sound.Source = source(),
    volume: Float = volume(),
    pitch: Float = pitch()
) = Sound.sound(name, source, volume, pitch)
