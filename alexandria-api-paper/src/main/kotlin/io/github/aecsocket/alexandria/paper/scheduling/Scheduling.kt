package io.github.aecsocket.alexandria.paper.scheduling

import org.bukkit.World
import org.bukkit.entity.Entity

interface SchedulingContext {
    fun run()

    fun runDelayed(delay: Long)

    fun runRepeating(period: Long, delay: Long = 0)
}

interface Scheduling {
    fun onServer(block: () -> Unit): SchedulingContext

    fun onEntity(entity: Entity, onFailure: () -> Unit = {}, onSuccess: () -> Unit): SchedulingContext

    fun onChunk(world: World, chunkX: Int, chunkZ: Int, block: () -> Unit): SchedulingContext
}
