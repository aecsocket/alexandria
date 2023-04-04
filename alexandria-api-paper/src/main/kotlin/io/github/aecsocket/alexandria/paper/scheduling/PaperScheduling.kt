package io.github.aecsocket.alexandria.paper.scheduling

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer
import kotlin.coroutines.CoroutineContext

class PaperScheduling(val plugin: Plugin) : Scheduling {
    private val scheduler = plugin.server.scheduler

    private fun wrap(task: BukkitTask) = object : TaskContext {
        override fun cancel() {
            task.cancel()
        }
    }

    override fun onServer() = object : SchedulingContext {
        override fun launch(block: suspend CoroutineScope.() -> Unit) {
            scheduler.runTask(plugin, Runnable {
                runBlocking(ImmediateCoroutineDispatcher, block)
            })
        }

        override fun runLater(delay: Long, block: () -> Unit) {
            scheduler.runTaskLater(plugin, Runnable(block), delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            scheduler.runTaskTimer(plugin, { task -> block(wrap(task)) }, delay, period)
        }
    }

    override fun onEntity(entity: Entity) = object : SchedulingContext {
        override fun launch(block: suspend CoroutineScope.() -> Unit) {
            scheduler.runTask(plugin, Runnable {
                runBlocking(ImmediateCoroutineDispatcher, block)
            })
        }

        override fun runLater(delay: Long, block: () -> Unit) {
            scheduler.runTaskLater(plugin, Runnable {
                if (entity.isValid) block()
            }, delay)
        }

        override fun runRepeating(period: Long, delay: Long, block: (TaskContext) -> Unit) {
            scheduler.runTaskTimer(plugin, { task ->
                if (entity.isValid) block(wrap(task))
                else task.cancel()
            }, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int) = onServer()
}
