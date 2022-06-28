package com.github.aecsocket.alexandria.paper.extension

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

fun Plugin.disable() = Bukkit.getPluginManager().disablePlugin(this)

fun Plugin.key(value: String) = NamespacedKey(this, value)

fun Plugin.scheduleDelayed(delay: Long = 0, task: () -> Unit) =
    Bukkit.getScheduler().scheduleSyncDelayedTask(this, task, delay)

fun Plugin.scheduleRepeating(period: Long = 0, delay: Long = 0, task: () -> Unit) =
    Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, delay, period)

fun Plugin.registerEvents(listener: Listener) = Bukkit.getPluginManager().registerEvents(listener, this)
