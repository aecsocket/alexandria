package io.github.aecsocket.alexandria.paper.scheduling

import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.IVec2
import io.papermc.paper.math.Position
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

interface TaskContext {
    fun cancel()
}

interface SchedulingContext {
    fun launch(block: () -> Unit)

    fun runLater(delay: Long, block: () -> Unit)

    fun runRepeating(period: Long = 1, delay: Long = 1, block: (TaskContext) -> Unit)
}

interface Scheduling {
    fun onServer(): SchedulingContext

    fun onEntity(entity: Entity): SchedulingContext

    fun onChunk(world: World, chunkX: Int, chunkZ: Int): SchedulingContext

    fun onChunk(location: Location): SchedulingContext =
        onChunk(location.world, location.blockX / 16, location.blockZ / 16)

    @Suppress("UnstableApiUsage")
    fun onChunk(world: World, position: Position): SchedulingContext =
        onChunk(world, position.blockX() / 16, position.blockZ() / 16)

    fun onChunk(world: World, position: IVec2): SchedulingContext =
        onChunk(world, position.x, position.y)

    fun onChunk(world: World, position: DVec3): SchedulingContext =
        onChunk(world, position.x.toInt() / 16, position.z.toInt() / 16)

    fun onChunk(chunk: Chunk): SchedulingContext =
        onChunk(chunk.world, chunk.x, chunk.z)
}
