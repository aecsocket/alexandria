package io.github.aecsocket.alexandria.paper.scheduling

import io.papermc.paper.math.Position
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Entity

interface TaskContext {
    fun cancelCurrentTask()
}

interface SchedulingContext {
    fun run()

    fun runLater(delay: Long)

    fun runRepeating(period: Long = 1, delay: Long = 0)
}

interface Scheduling {
    fun onServer(block: TaskContext.() -> Unit): SchedulingContext

    fun onEntity(entity: Entity, onFailure: () -> Unit = {}, onSuccess: TaskContext.() -> Unit): SchedulingContext

    fun onChunk(world: World, chunkX: Int, chunkZ: Int, block: TaskContext.() -> Unit): SchedulingContext

    @Suppress("UnstableApiUsage")
    fun onChunk(world: World, position: Position, block: TaskContext.() -> Unit): SchedulingContext =
        onChunk(world, position.blockX() / 16, position.blockZ() / 16, block)

    fun onChunk(chunk: Chunk, block: TaskContext.() -> Unit): SchedulingContext =
        onChunk(chunk.world, chunk.x, chunk.z, block)
}
