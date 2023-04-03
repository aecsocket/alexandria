package io.github.aecsocket.alexandria.paper.scheduling

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin

class FoliaScheduling(val plugin: Plugin) : Scheduling {
    override fun onServer(block: () -> Unit) = object : SchedulingContext {
        override fun run() {
            Bukkit.getGlobalRegionScheduler().run(plugin) { block() }
        }

        override fun runDelayed(delay: Long) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, { block() }, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, { block() }, delay, period)
        }
    }

    override fun onEntity(entity: Entity, onFailure: () -> Unit, onSuccess: () -> Unit) = object : SchedulingContext {
        override fun run() {
            entity.scheduler.run(plugin, { onSuccess() }, { onFailure() })
        }

        override fun runDelayed(delay: Long) {
            entity.scheduler.runDelayed(plugin, { onSuccess() }, { onFailure() }, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            entity.scheduler.runAtFixedRate(plugin, { onSuccess() }, { onFailure() }, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int, block: () -> Unit) = object : SchedulingContext {
        override fun run() {
            Bukkit.getRegionScheduler().run(plugin, world, chunkX, chunkZ) { block() }
        }

        override fun runDelayed(delay: Long) {
            Bukkit.getRegionScheduler().runDelayed(plugin, world, chunkX, chunkZ, { block() }, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            Bukkit.getRegionScheduler().runAtFixedRate(plugin, world, chunkX, chunkZ, { block() }, delay, period)
        }
    }
}
