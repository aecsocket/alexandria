package io.github.aecsocket.alexandria.paper.scheduling

import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.IVec2
import io.papermc.paper.math.Position
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

/** Provides access to modifying a task's state, created by a [Scheduling] object. */
interface TaskContext {
  fun cancel()
}

/** Context for registering a task on a [Scheduling]. */
interface SchedulingContext {
  /**
   * Runs a task at a later point in time once.
   *
   * @param delay The delay, in ticks.
   * @param block The task to run.
   */
  fun runLater(delay: Long = 1, block: () -> Unit)

  /**
   * Runs a task indefinitely at later points in time until the task is cancelled.
   *
   * @param period The interval between each task invocation, in ticks.
   * @param delay How long until the first task runs, in ticks.
   * @param block The task to run, which can also modify the [TaskContext].
   */
  fun runRepeating(period: Long = 1, delay: Long = 1, block: (TaskContext) -> Unit)
}

/**
 * Allows scheduling tasks to be run in the future, automatically handling platform-specific task
 * registration.
 */
interface Scheduling {
  /** Runs a task on the whole server. */
  fun onServer(): SchedulingContext

  /**
   * Runs a task on a specific entity.
   *
   * If the entity is not [Entity.isValid] before the given task is run, [onRetire] is called.
   * Therefore, it is guaranteed that [entity] is [Entity.isValid] when tasks are run.
   */
  fun onEntity(entity: Entity, onRetire: () -> Unit = {}): SchedulingContext

  /**
   * Runs a task on a specific chunk, specified by its X and Z coordinates.
   *
   * This does not guarantee that the chunk is loaded.
   */
  fun onChunk(world: World, chunkX: Int, chunkZ: Int): SchedulingContext

  /**
   * Runs a task on a specific chunk, specified by a location.
   *
   * This does not guarantee that the chunk is loaded.
   */
  fun onChunk(location: Location): SchedulingContext =
      onChunk(location.world, location.blockX / 16, location.blockZ / 16)

  /**
   * Runs a task on a specific chunk, specified by a position.
   *
   * This does not guarantee that the chunk is loaded.
   */
  @Suppress("UnstableApiUsage")
  fun onChunk(world: World, position: Position): SchedulingContext =
      onChunk(world, position.blockX() / 16, position.blockZ() / 16)

  /**
   * Runs a task on a specific chunk, specified by its X and Z coordinates.
   *
   * This does not guarantee that the chunk is loaded.
   */
  fun onChunk(world: World, position: IVec2): SchedulingContext =
      onChunk(world, position.x, position.y)

  /**
   * Runs a task on a specific chunk, specified by a world position's X, Y, Z coordinates.
   *
   * This does not guarantee that the chunk is loaded.
   */
  fun onChunk(world: World, position: DVec3): SchedulingContext =
      onChunk(world, position.x.toInt() / 16, position.z.toInt() / 16)

  /**
   * Runs a task on a specific chunk.
   *
   * This does not guarantee that the chunk is loaded.
   */
  fun onChunk(chunk: Chunk): SchedulingContext = onChunk(chunk.world, chunk.x, chunk.z)
}
