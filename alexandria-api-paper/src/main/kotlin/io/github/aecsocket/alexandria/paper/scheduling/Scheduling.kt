package io.github.aecsocket.alexandria.paper.scheduling

import io.papermc.paper.math.Position
import org.bukkit.Chunk
import org.bukkit.Location
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

    @Suppress("UnstableApiUsage")
    fun onChunk(world: World, position: Position, block: () -> Unit): SchedulingContext =
        onChunk(world, position.blockX() / 16, position.blockZ() / 16, block)

    fun onChunk(chunk: Chunk, block: () -> Unit): SchedulingContext =
        onChunk(chunk.world, chunk.x, chunk.z, block)
}
