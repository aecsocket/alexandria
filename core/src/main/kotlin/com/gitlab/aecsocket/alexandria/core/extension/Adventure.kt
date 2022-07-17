package com.gitlab.aecsocket.alexandria.core.extension

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text

fun Sound.copy(
    name: Key = name(),
    source: Sound.Source = source(),
    volume: Float = volume(),
    pitch: Float = pitch()
) = Sound.sound(name, source, volume, pitch)

fun Component.repeat(times: Int): Component {
    val res = text()
    repeat(times) { res.append(this) }
    return res.build()
}
