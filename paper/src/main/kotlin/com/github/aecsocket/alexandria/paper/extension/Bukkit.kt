package com.github.aecsocket.alexandria.paper.extension

import org.bukkit.Bukkit

val bukkitCurrentTick get() = Bukkit.getCurrentTick()
@Suppress("DEPRECATION")
val bukkitNextEntityId get() = Bukkit.getUnsafe().nextEntityId()
