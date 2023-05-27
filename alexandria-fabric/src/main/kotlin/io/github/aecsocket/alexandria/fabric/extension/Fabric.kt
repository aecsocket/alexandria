package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.alexandria.extension.DEFAULT
import net.minecraft.world.level.Level

fun <V> Map<String, V>.forLevel(level: Level) = get(level.dimension().location().toString()) ?: get(DEFAULT)
