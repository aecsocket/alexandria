package io.github.aecsocket.alexandria.paper.scheduling

import io.papermc.paper.math.Position
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import kotlin.coroutines.CoroutineContext

interface TaskContext {
    fun cancel()
}

interface SchedulingContext {
    fun launch(block: suspend CoroutineScope.() -> Unit)

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

    fun onChunk(chunk: Chunk): SchedulingContext =
        onChunk(chunk.world, chunk.x, chunk.z)
}

object ImmediateCoroutineDispatcher : CoroutineDispatcher() {
    override fun isDispatchNeeded(context: CoroutineContext) = false
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        block.run()
    }
}