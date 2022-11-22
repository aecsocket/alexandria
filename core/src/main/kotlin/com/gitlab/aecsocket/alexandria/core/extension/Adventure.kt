package com.gitlab.aecsocket.alexandria.core.extension

import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Key.key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import java.awt.Color

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

fun TextColor.hsb(): Vector3 {
    val hsb = floatArrayOf(0f, 0f, 0f)
    Color.RGBtoHSB(red(), green(), blue(), hsb)
    return Vector3(hsb[0].toDouble(), hsb[1].toDouble(), hsb[2].toDouble())
}

fun Key.with(path: String) = key(namespace(), "${value()}/$path")
