package io.github.aecsocket.alexandria.paper.scheduling

import kotlinx.coroutines.Runnable
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

/**
 * Default [Scheduling] implementation for the base Paper platform.
 */
class PaperScheduling(val plugin: Plugin) : Scheduling {
    private val scheduler = plugin.server.scheduler

    private fun wrap(task: BukkitTask) = object : TaskContext {
        override fun cancel() {
            task.cancel()
        }
    }

    override fun onServer() = object : SchedulingContext {
        override fun runLater(delay: Long, block: () -> Unit) {
            if (!plugin.isEnabled) return
            scheduler.runTaskLater(plugin, Runnable(block), delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            if (!plugin.isEnabled) return
            scheduler.runTaskTimer(plugin, { task -> block(wrap(task)) }, delay, period)
        }
    }

    override fun onEntity(entity: Entity, onRetire: () -> Unit) = object : SchedulingContext {
        override fun runLater(delay: Long, block: () -> Unit) {
            if (!plugin.isEnabled) return
            scheduler.runTaskLater(plugin, Runnable {
                if (entity.isValid) block()
                else onRetire()
            }, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            if (!plugin.isEnabled) return
            scheduler.runTaskTimer(plugin, { task ->
                if (entity.isValid) block(wrap(task))
                else {
                    onRetire()
                    task.cancel()
                }
            }, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int) = onServer()
}
