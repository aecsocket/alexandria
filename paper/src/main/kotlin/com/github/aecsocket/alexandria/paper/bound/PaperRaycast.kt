package com.github.aecsocket.alexandria.paper.bound

import com.github.aecsocket.alexandria.core.bound.*
import com.github.aecsocket.alexandria.core.vector.Vector3
import com.github.aecsocket.alexandria.paper.extension.vector
import net.minecraft.world.phys.AABB
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.Waterlogged
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.entity.Entity
import kotlin.math.sign

/*
possible optimizations:
- if testing for blocks, don't translate ray and NMS block bounds,
  since NMS bounds are already at the world-space coords
- use raw NMS types like VoxelShape, nms.Block, nms.Entity (internally)
 */

sealed interface PaperBoundable : Boundable {
    val fluid: Material?

    data class OfBlock(
        val block: Block,
        override val bound: Bound,
        override val fluid: Material?
    ) : PaperBoundable {
        override fun toString() =
            "OfBlock(${block.type} fluid=$fluid)"
    }

    data class OfEntity(
        val entity: Entity
    ) : PaperBoundable {
        override val bound = entity.bound().translated(entity.location.vector())
        override val fluid: Material?
            get() = null

        override fun toString() =
            "OfEntity(${entity.name})"
    }
}

class PaperRaycast(
    val world: World,
    val options: Options
) : Raycast<PaperBoundable>() {
    data class Options(
        val epsilon: Double = 1.0
    )

    override fun cast(
        ray: Ray,
        maxDistance: Double,
        test: (PaperBoundable) -> Boolean
    ): Result<PaperBoundable> {
        val block = castBlocks(ray, maxDistance, test)
        val entity = castEntities(ray, maxDistance, test)
        return if (block.travelled < entity.travelled) block else entity
    }

    fun castBlocks(
        ray: Ray,
        maxDistance: Double,
        test: (PaperBoundable.OfBlock) -> Boolean
    ): Result<PaperBoundable.OfBlock> {
        data class Triple<T>(val a: T, val y: T, val z: T)

        fun frac(v: Double): Double {
            val l = v.toLong()
            return v - (if (v < l) l - 1 else l)
        }

        fun floor(v: Double): Int {
            val i = v.toInt()
            return if (v < i) i - 1 else i
        }

        val (x0, y0, z0) = ray.origin
        val (x1, y1, z1) = ray.point(maxDistance)
        var (xi, yi, zi) = Triple(floor(x0), floor(y0), floor(z0))
        val (dx, dy, dz) = Triple(x1 - x0, y1 - y0, z1 - z0)
        val (xs, ys, zs) = Triple(dx.sign.toInt(), dy.sign.toInt(), dz.sign.toInt())
        val (xa, ya, za) = Triple(
            if (xs == 0) Double.MAX_VALUE else xs / dx,
            if (ys == 0) Double.MAX_VALUE else ys / dy,
            if (zs == 0) Double.MAX_VALUE else zs / dz,
        )
        var (xb, yb, zb) = Triple(
            xa * (if (xs > 0) 1 - frac(x0) else frac(x0)),
            ya * (if (ys > 0) 1 - frac(y0) else frac(y0)),
            za * (if (zs > 0) 1 - frac(z0) else frac(z0)),
        )

        fun result(): Result<PaperBoundable.OfBlock>? {
            var i = 0
            while (true) {
                i++
                val block = world.getBlockAt(xi, yi, zi)

                val type = block.type
                // block bounds are translated into ray space
                val box = listOf(PaperBoundable.OfBlock(block, Box.ZeroOne.translated(ray.origin), type))
                val boundables = when (type) {
                    Material.AIR, Material.WATER, Material.LAVA -> box
                    else -> when {
                        type.isOccluding -> box
                        else -> {
                            val blockData = block.blockData
                            listOf(
                                PaperBoundable.OfBlock(block, block.bound().translated(ray.origin), null)
                            ) + (if (blockData is Waterlogged && blockData.isWaterlogged)
                                listOf(PaperBoundable.OfBlock(block, Box.ZeroOne.translated(ray.origin), Material.WATER))
                            else emptyList())
                        }
                    }
                }

                boundables
                    .filter(test)
                    .mapNotNull {
                        /*
                        this intersection test is ran in world space rather than block-bound space
                        e.g. a block at (10, 20, 30) would have a ray around (10, 20, 30) intersect a bound
                        {min = (10, 20, 30) max = (11, 21, 31)}.
                        this completely screws up oriented bounds and rotations, and should be fixed,
                        because the rotation origin is (0, 0, 0) yet that's the *world origin*
                        and I don't think using (0.5, 0.5, 0.5) and intersecting in block-bound space is a solution
                        either, because different parts of a block may want to have a different rotation origin

                        tldr: if you're not using oriented bounds for this, it works fine
                              otherwise it will break completely

                        update: block bounds cannot be overridden, so rotatable bounds will never be tested here
                         */
                        intersects(ray, it)
                    }
                    .minByOrNull { it.tIn }?.let {
                        return it
                    }

                if (xb < yb) {
                    if (xb < zb) {
                        xi += xs
                        xb += xa
                    } else {
                        zi += zs
                        zb += za
                    }
                } else if (yb < zb) {
                    yi += ys
                    yb += ya
                } else {
                    zi += zs
                    zb += za
                }

                if ((xb > 1 && yb > 1 && zb > 1) || !world.isChunkLoaded(xi / 16, zi / 16))
                    break
            }
            return null
        }

        return result() ?: Result.Miss(ray, maxDistance)
    }

    fun castEntities(
        ray: Ray,
        maxDistance: Double,
        test: (PaperBoundable.OfEntity) -> Boolean
    ): Result<PaperBoundable.OfEntity> {
        val world = (world as CraftWorld).handle
        val end = ray.point(maxDistance)
        val (min, max) = Vector3.min(ray.origin, end) to Vector3.max(ray.origin, end)

        var closest: Result.Hit<PaperBoundable.OfEntity>? = null
        world.entities.get(AABB(
            min.x - options.epsilon, min.y - options.epsilon, min.z - options.epsilon,
            max.x + options.epsilon, max.y + options.epsilon, max.z + options.epsilon
        )) { nms ->
            val entity = nms.bukkitEntity
            val boundable = PaperBoundable.OfEntity(entity) // todo
            if (test(boundable)) {
                intersects(ray, boundable)?.let { hit ->
                    closest = closest?.let {
                        if (hit.tIn < it.tIn) hit else it
                    } ?: hit
                }
            }
        }

        return closest ?: Result.Miss(ray, maxDistance)
    }

    fun inWorld(world: World) = PaperRaycast(world, options)
}
