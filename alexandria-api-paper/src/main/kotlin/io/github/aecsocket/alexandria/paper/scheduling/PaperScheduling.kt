package io.github.aecsocket.alexandria.paper.scheduling

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class PaperScheduling(val plugin: Plugin) : Scheduling {
    private fun wrap(block: TaskContext.() -> Unit) = object : BukkitRunnable() {
        val self get() = this

        override fun run() {
            block(object : TaskContext {
                override fun cancelCurrentTask() {
                    self.cancel()
                }
            })
        }
    }

    override fun onServer(block: TaskContext.() -> Unit) = object : SchedulingContext {
        override fun run() {
            wrap(block).runTask(plugin)
        }

        override fun runLater(delay: Long) {
            wrap(block).runTaskLater(plugin, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            wrap(block).runTaskTimer(plugin, delay, period)
        }
    }

    override fun onEntity(entity: Entity, onFailure: () -> Unit, onSuccess: TaskContext.() -> Unit) = object : SchedulingContext {
        override fun run() {
            wrap {
                if (entity.isValid) onSuccess()
                else {
                    onFailure()
                    cancelCurrentTask()
                }
            }.runTask(plugin)
        }

        override fun runLater(delay: Long) {
            wrap {
                if (entity.isValid) onSuccess()
                else {
                    onFailure()
                    cancelCurrentTask()
                }
            }.runTaskLater(plugin, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            wrap {
                if (entity.isValid) onSuccess()
                else {
                    onFailure()
                    cancelCurrentTask()
                }
            }.runTaskTimer(plugin, delay, period)
        }
    }

    override fun onChunk(world: World, chunkX: Int, chunkZ: Int, block: TaskContext.() -> Unit) = object : SchedulingContext {
        override fun run() {
            wrap(block).runTask(plugin)
        }

        override fun runLater(delay: Long) {
            wrap(block).runTaskLater(plugin, delay)
        }

        override fun runRepeating(period: Long, delay: Long) {
            wrap(block).runTaskTimer(plugin, delay, period)
        }
    }
}
