package com.gitlab.aecsocket.alexandria.paper.physics

import com.gitlab.aecsocket.alexandria.core.physics.*
import com.gitlab.aecsocket.alexandria.paper.extension.alexandria
import com.gitlab.aecsocket.alexandria.paper.extension.extent
import com.gitlab.aecsocket.alexandria.paper.extension.transform
import com.gitlab.aecsocket.alexandria.paper.extension.position
import net.minecraft.world.phys.AABB
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.Waterlogged
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.entity.Entity
import kotlin.math.sign

sealed interface PaperBody : Body

class PaperBlockBody(
    override val shape: Shape,
    override val transform: Transform,
    val block: Block,
    val fluid: Material? = null
) : PaperBody {
    override fun toString() =
        "PaperBlockBody(${block.type} fluid=$fluid)"
}

class PaperEntityBody(
    override val shape: Shape,
    override val transform: Transform,
    val entity: Entity,
) : PaperBody {
    override fun toString() =
        "PaperEntityBody(${entity.name})"
}

fun Block.bodies(): List<PaperBlockBody> {
    val location = location.position()
    fun box(fluid: Material?) = PaperBlockBody(BoxShape.Half, Transform(translation = location + 0.5), this, fluid)

    return when (type) {
        Material.AIR, Material.WATER, Material.LAVA -> listOf(box(type))
        else -> {
            if (type.isOccluding) listOf(box(null))
            else {
                val bodies = collisionShape.boundingBoxes.map { box ->
                    // full block: aabb = [0, 0, 0] -> [1, 1, 1]
                    // -> transform = [location + aabb.center]
                    // -> shape = [0.5, 0.5, 0.5]
                    PaperBlockBody(
                        BoxShape(box.extent / 2.0),
                        Transform(translation = location + box.center.alexandria()),
                        this,
                    )
                }.toMutableList()
                val data = blockData
                if (data is Waterlogged && data.isWaterlogged) {
                    bodies.add(PaperBlockBody(BoxShape.Half, Transform(translation = location + 0.5), this, Material.WATER))
                }
                bodies
            }
        }
    }
}

fun Entity.bodies(): List<PaperEntityBody> {
    val aabb = boundingBox
    return listOf(PaperEntityBody(
        BoxShape(aabb.extent / 2.0),
        Transform(transform.rotation, aabb.center.alexandria()),
        this,
    ))
}

class PaperRaycast(
    private val world: World,
    private val options: Options = Options()
) : Raycast<PaperBody>() {
    data class Options(
        val margin: Double = 1.0
    )

    fun world(world: World) = PaperRaycast(world, options)

    override fun cast(
        ray: Ray,
        maxDistance: Double,
        test: (PaperBody) -> Boolean
    ): RayCollision<out PaperBody>? {
        val block = castBlocks(ray, maxDistance, test)
        val entity = castEntities(ray, maxDistance, test)
        return listOfNotNull(block, entity).minByOrNull { it.tIn }
    }

    fun castBlocks(
        ray: Ray,
        maxDistance: Double,
        test: (PaperBlockBody) -> Boolean = { true }
    ): RayCollision<PaperBlockBody>? {
        fun frac(v: Double): Double {
            val l = v.toLong()
            return v - (if (v < l) l - 1 else l)
        }

        fun floor(v: Double): Int {
            val i = v.toInt()
            return if (v < i) i - 1 else i
        }

        val (x0, y0, z0) = ray.pos
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

        var i = 0
        while (true) {
            i++
            val block = world.getBlockAt(xi, yi, zi)
            collides(ray, block.bodies().filter(test), maxDistance)?.let { return it }

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

    fun castEntities(
        ray: Ray,
        maxDistance: Double,
        test: (PaperEntityBody) -> Boolean = { true }
    ): RayCollision<PaperEntityBody>? {
        val world = (world as CraftWorld).handle
        val end = ray.point(maxDistance)
        val (min, max) = min(ray.pos, end) to max(ray.pos, end)

        var closest: RayCollision<PaperEntityBody>? = null
        val mgn = options.margin
        world.entities.get(AABB(
            min.x - mgn, min.y - mgn, min.z - mgn,
            max.x + mgn, max.y + mgn, max.z + mgn
        )) { nms ->
            val entity = nms.bukkitEntity
            collides(ray, entity.bodies().filter(test), maxDistance)?.let { collision ->
                closest = closest?.let {
                    if (collision.tIn < it.tIn) collision
                    else it
                } ?: collision
            }
        }
        return closest
    }
}
