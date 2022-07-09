package com.github.aecsocket.alexandria.paper.physics

import com.github.aecsocket.alexandria.core.extension.Euler3
import com.github.aecsocket.alexandria.core.extension.EulerOrder
import com.github.aecsocket.alexandria.core.extension.quaternion
import com.github.aecsocket.alexandria.core.extension.radians
import com.github.aecsocket.alexandria.core.physics.*
import com.github.aecsocket.alexandria.paper.extension.alexandria
import com.github.aecsocket.alexandria.paper.extension.extent
import com.github.aecsocket.alexandria.paper.extension.vector
import net.minecraft.world.phys.AABB
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.Waterlogged
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.entity.Entity
import kotlin.math.sign

sealed interface PaperBody : Body {
    val fluid: Material?
}

data class PaperBlockBody(
    val block: Block,
    override val transform: Transform,
    override val shape: Shape,
    override val fluid: Material? = null
) : PaperBody {
    override fun toString() =
        "PaperBlockBody(${block.type} fluid=$fluid)"
}

data class PaperEntityBody(
    val entity: Entity,
    override val transform: Transform,
    override val shape: Shape,
) : PaperBody {
    override val fluid get() = null

    override fun toString() =
        "PaperEntityBody(${entity.name})"
}

fun Block.bodies(): List<PaperBlockBody> {
    val location = location.vector()
    fun box(fluid: Material?) = PaperBlockBody(this, Transform(tl = location + 0.5), Box.Half, fluid)

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
                        this,
                        Transform(tl = location + box.center.alexandria()),
                        Box(box.extent / 2.0)
                    )
                }.toMutableList()
                val data = blockData
                if (data is Waterlogged && data.isWaterlogged) {
                    bodies.add(PaperBlockBody(this, Transform(tl = location + 0.5), Box.Half, Material.WATER))
                }
                bodies
            }
        }
    }
}

fun Entity.bodies(): List<PaperEntityBody> {
    val aabb = boundingBox
    return listOf(PaperEntityBody(
        this,
        Transform(
            Euler3(0.0, -location.yaw.toDouble().radians, 0.0).quaternion(EulerOrder.XYZ),
            aabb.center.alexandria()),
        Box(aabb.extent / 2.0)
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
            collides(ray, block.bodies().filter(test))?.let { return it }

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
            collides(ray, entity.bodies().filter(test))?.let { collision ->
                closest = closest?.let {
                    if (collision.tIn < it.tIn) collision
                    else it
                } ?: collision
            }
        }
        return closest
    }
}
