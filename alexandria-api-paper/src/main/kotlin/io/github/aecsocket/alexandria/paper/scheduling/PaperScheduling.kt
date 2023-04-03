package io.github.aecsocket.alexandria.paper.scheduling

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin

class PaperScheduling(val plugin: Plugin) : Scheduling {
    override fun onServer(block: () -> Unit) = object : SchedulingContext {
        override fun run() {
            Bukkit.getScheduler().runTask(plugin, Runnable(block))
        }

        override fun runDelayed(delay: Long) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, Runnable(block), delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, Runnable(block), delay, period)
        }
    }

    override fun onEntity(entity: Entity, onFailure: () -> Unit, onSuccess: () -> Unit) = object : SchedulingContext {
        override fun run() {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                if (entity.isValid) onSuccess()
                else onFailure()
            })
        }

        override fun runDelayed(delay: Long) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                if (entity.isValid) onSuccess()
                else onFailure()
            }, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
                if (entity.isValid) onSuccess()
                else onFailure()
            }, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int, block: () -> Unit) = object : SchedulingContext {
        override fun run() {
            Bukkit.getScheduler().runTask(plugin, Runnable(block))
        }

        override fun runDelayed(delay: Long) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, Runnable(block), delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, Runnable(block), delay, period)
        }
    }
}
