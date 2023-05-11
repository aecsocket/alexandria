package io.github.aecsocket.alexandria.paper.scheduling

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import kotlin.coroutines.CoroutineContext

class FoliaScheduling(val plugin: Plugin) : Scheduling {
    private val server = plugin.server

    private fun wrap(task: ScheduledTask) = object : TaskContext {
        override fun cancel() {
            task.cancel()
        }
    }

    override fun onServer() = object : SchedulingContext {
        override fun launch(block: () -> Unit) {
            if (!plugin.isEnabled) return
            server.globalRegionScheduler.run(plugin) {
                block()
            }
        }

        override fun runLater(delay: Long, block: () -> Unit) {
            if (!plugin.isEnabled) return
            server.globalRegionScheduler.runDelayed(plugin, { block() }, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            if (!plugin.isEnabled) return
            server.globalRegionScheduler.runAtFixedRate(plugin, { task -> block(wrap(task)) }, delay, period)
        }
    }

    override fun onEntity(entity: Entity) = object : SchedulingContext {
        override fun launch(block: () -> Unit) {
            if (!plugin.isEnabled) return
            entity.scheduler.run(plugin, {
                block()
            }, null)
        }

        override fun runLater(delay: Long, block: () -> Unit) {
            if (!plugin.isEnabled) return
            entity.scheduler.runDelayed(plugin, { block() }, null, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            if (!plugin.isEnabled) return
            entity.scheduler.runAtFixedRate(plugin, { task -> block(wrap(task)) }, null, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int) = object : SchedulingContext {
        override fun launch(block: () -> Unit) {
            if (!plugin.isEnabled) return
            server.regionScheduler.run(plugin, world, chunkX, chunkZ) {
                block()
            }
        }

        override fun runLater(delay: Long, block: () -> Unit) {
            if (!plugin.isEnabled) return
            server.regionScheduler.runDelayed(plugin, world, chunkX, chunkZ, { block() }, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            if (!plugin.isEnabled) return
            server.regionScheduler.runAtFixedRate(plugin, world, chunkX, chunkZ, { task -> block(wrap(task)) }, delay, period)
        }
    }
}
