package io.github.aecsocket.alexandria.paper.scheduling

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin

/**
 * Default [Scheduling] implementation for the Folia platform, featuring region multithreading.
 */
class FoliaScheduling(val plugin: Plugin) : Scheduling {
    private val server = plugin.server

    private fun wrap(task: ScheduledTask) = object : TaskContext {
        override fun cancel() {
            task.cancel()
        }
    }

    override fun onServer() = object : SchedulingContext {
        override fun runLater(delay: Long, block: () -> Unit) {
            server.globalRegionScheduler.runDelayed(plugin, { block() }, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            server.globalRegionScheduler.runAtFixedRate(plugin, { block(wrap(it)) }, delay, period)
        }
    }

    override fun onEntity(entity: Entity, onRetire: () -> Unit) = object : SchedulingContext {
        override fun runLater(delay: Long, block: () -> Unit) {
            entity.scheduler.runDelayed(plugin, { block() }, onRetire, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            entity.scheduler.runAtFixedRate(plugin, { block(wrap(it)) }, onRetire, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int) = object : SchedulingContext {
        override fun runLater(delay: Long, block: () -> Unit) {
            server.regionScheduler.runDelayed(plugin, world, chunkX, chunkZ, { block() }, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            server.regionScheduler.runAtFixedRate(plugin, world, chunkX, chunkZ, { block(wrap(it)) }, delay, period)
        }
    }
}
