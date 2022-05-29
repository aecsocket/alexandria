package com.github.aecsocket.alexandria.paper.extension

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextColor
import net.minecraft.resources.ResourceLocation
import org.bukkit.Color

fun Key.asLocation() = ResourceLocation(namespace(), value())

fun Color.asAdventure() = TextColor.color(asRGB())

fun TextColor.asBukkit() = Color.fromRGB(value())
