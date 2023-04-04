package io.github.aecsocket.alexandria.paper.scheduling

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

class FoliaScheduling(val plugin: Plugin) : Scheduling {
    private fun wrap(block: TaskContext.() -> Unit) = Consumer<ScheduledTask> { task ->
        block(object : TaskContext {
            override fun cancelCurrentTask() {
                task.cancel()
            }
        })
    }

    override fun onServer(block: TaskContext.() -> Unit) = object : SchedulingContext {
        override fun run() {
            Bukkit.getGlobalRegionScheduler().run(plugin, wrap(block))
        }

        override fun runLater(delay: Long) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, wrap(block), delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, wrap(block), delay, period)
        }
    }

    override fun onEntity(entity: Entity, onFailure: () -> Unit, onSuccess: TaskContext.() -> Unit) = object : SchedulingContext {
        override fun run() {
            entity.scheduler.run(plugin, wrap(onSuccess), onFailure)
        }

        override fun runLater(delay: Long) {
            entity.scheduler.runDelayed(plugin, wrap(onSuccess), onFailure, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            entity.scheduler.runAtFixedRate(plugin, wrap(onSuccess), onFailure, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int, block: TaskContext.() -> Unit) = object : SchedulingContext {
        override fun run() {
            Bukkit.getRegionScheduler().run(plugin, world, chunkX, chunkZ, wrap(block))
        }

        override fun runLater(delay: Long) {
            Bukkit.getRegionScheduler().runDelayed(plugin, world, chunkX, chunkZ, wrap(block), delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            Bukkit.getRegionScheduler().runAtFixedRate(plugin, world, chunkX, chunkZ, wrap(block), delay, period)
        }
    }
}
