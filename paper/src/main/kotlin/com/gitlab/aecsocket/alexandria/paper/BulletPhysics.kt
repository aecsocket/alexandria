package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.effect.ParticleEffect
import com.gitlab.aecsocket.alexandria.core.extension.EulerOrder
import com.gitlab.aecsocket.alexandria.core.extension.boxVertices
import com.gitlab.aecsocket.alexandria.core.extension.euler
import com.gitlab.aecsocket.alexandria.core.extension.showCuboid
import com.gitlab.aecsocket.alexandria.core.physics.Transform
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.effect.PaperEffectors
import com.gitlab.aecsocket.alexandria.paper.extension.*
import com.jme3.bullet.PhysicsSpace
import com.jme3.bullet.collision.shapes.*
import com.jme3.bullet.collision.shapes.infos.IndexedMesh
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.bullet.objects.infos.RigidBodyMotionState
import com.jme3.math.Plane
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.system.NativeLibraryLoader
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component.text
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

const val TIME_INTERVAL = 0.05f

// todo man this code is slow (probably)
fun collisionShapeOf(chunk: Chunk): CollisionShape {
    val indices = ArrayList<Int>()

    fun addIndices(vararg values: Int) {
        indices.addAll(values.asIterable())
    }

    (0 until 16).forEach { ix ->
        (0 until 16).forEach { iz ->
            (-64 until 256).forEach { iy ->
                val block = chunk.getBlock(ix, iy, iz)
                val x = ix
                val y = iy + 64
                val z = iz
                if (block.isSolid) {
                    val i = x + (16 * z) + (16 * 16 * y)

                    val aa1 = i
                    val ba1 = i+1
                    val ab1 = i+16
                    val bb1 = i+17

                    val aa2 = aa1+256
                    val ba2 = ba1+256
                    val ab2 = ab1+256
                    val bb2 = bb1+256

                    addIndices(
                        // bottom
//                        aa1, ba1, bb1,
//                        aa1, ab1, bb1,
                        // top
                        //aa2, ba2, bb2,
                        //aa2, ab2, bb2,
                        aa2, bb2, ba2,
                        aa2, bb2, ab2,
//                        // -X
//                        aa1, ab1, ab2,
//                        aa1, aa2, ab2,
//                        // +X
//                        ba1, bb1, bb2,
//                        ba1, ba2, bb2,
//                        // -Z
//                        aa1, ba1, ba2,
//                        aa1, aa2, ba2,
//                        // +X
//                        ab1, bb1, bb2,
//                        ab1, ab2, bb2,
                    )
                }
            }
        }
    }

    return GImpactCollisionShape(IndexedMesh(BulletPhysics.chunkVertices, indices.toIntArray()))
}

class PhysicsWorld(
    val worldId: UUID,
    val space: PhysicsSpace
) {
    val world: World? get() = Bukkit.getWorld(worldId)

    private val _chunkBodies = HashMap<Long, PhysicsRigidBody>()
    val chunkBodies: Map<Long, PhysicsRigidBody> get() = _chunkBodies

    fun removeChunk(key: Long) {
        _chunkBodies[key]?.also { space.removeCollisionObject(it) }
    }

    fun setChunk(chunk: Chunk, shape: CollisionShape) {
        val key = chunk.chunkKey
        removeChunk(key)
        _chunkBodies[key] = PhysicsRigidBody(shape, staticMass).also {
            it.position = Vector3f(chunk.x * 16f, 0f, chunk.z * 16f)
            space.addCollisionObject(it)
        }
    }
}

class BulletPhysics(
    private val plugin: Alexandria
) {
    fun collisionShapeOf(chunk: Chunk): CollisionShape {
        /*val indices = ArrayList<Int>()

        fun addIndices(vararg values: Int) {
            indices.addAll(values.asIterable())
        }

        (0 until 16).forEach { ix ->
            (0 until 16).forEach { iz ->
                (-64 until 256).forEach { iy ->
                    val block = chunk.getBlock(ix, iy, iz)
                    val x = ix
                    val y = iy + 64
                    val z = iz
                    if (block.isSolid) {
                        val i = x + (16 * z) + (16 * 16 * y)

                        val aa1 = i
                        val ba1 = i+1
                        val ab1 = i+16
                        val bb1 = i+17

                        val aa2 = aa1+256
                        val ba2 = ba1+256
                        val ab2 = ab1+256
                        val bb2 = bb1+256

                        addIndices(
                            // bottom
                            aa1, ba1, bb1,
                            aa1, ab1, bb1,
                            // top
                            aa2, ba2, bb2,
                            aa2, ab2, bb2,
                            // -X
                            aa1, ab1, ab2,
                            aa1, aa2, ab2,
                            // +X
                            ba1, bb1, bb2,
                            ba1, ba2, bb2,
                            // -Z
                            aa1, ba1, ba2,
                            aa1, aa2, ba2,
                            // +X
                            ab1, bb1, bb2,
                            ab1, ab2, bb2,
                        )
                    }
                }
            }
        }

        val offset = Vector3f(chunk.x * 16f, 0f, chunk.z * 16f)
        plugin.scheduleRepeating {
            bukkitPlayers.forEach { player ->
                val eff = PaperEffectors().apply { init(plugin) }.player(player)
                val part = ParticleEffect(Key.key("minecraft", "bubble"))
                repeat(indices.size / 3) {
                    val start = it * 3
                    val a = (chunkVertices[indices[start]] + offset).alexandria()
                    val b = (chunkVertices[indices[start+1]] + offset).alexandria()
                    val c = (chunkVertices[indices[start+2]] + offset).alexandria()
                    eff.showLine(part, a, b, 0.1)
                    eff.showLine(part, b, c, 0.1)
                    eff.showLine(part, c, a, 0.1)
                }
            }
        }



        return GImpactCollisionShape(IndexedMesh(BulletPhysics.chunkVertices, indices.toIntArray()))*/

        val shape = CompoundCollisionShape()

        (0 until 16).forEach { x ->
            (0 until 16).forEach { z ->
                (-64 until 320).forEach { y ->
                    val block = chunk.getBlock(x, y, z)
                    if (block.isSolid) {
                        shape.addChildShape(BoxCollisionShape(0.5f), Vector3f(x + 0.5f, y + 0.5f, z + 0.5f))
                    }
                }
            }
        }

        return shape
    }

    var enabled = true
    var timeInterval = TIME_INTERVAL

    private val _worlds = HashMap<UUID, PhysicsWorld>()
    val worlds: Map<UUID, PhysicsWorld> = _worlds

    private data class Drop(
        val entity: Entity,
        val motionState: RigidBodyMotionState,
        val space: PhysicsSpace,
        val body: PhysicsRigidBody,
    )

    private val drops = ArrayList<Drop>()

    fun world(world: World) = _worlds.computeIfAbsent(world.uid) {
        PhysicsWorld(it, PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT).also { space ->
//            // TODO temporary
//            space.addCollisionObject(PhysicsRigidBody(
//                PlaneCollisionShape(Plane(Vector3f.UNIT_Y, 128f)),
//                staticMass
//            ))

            space.addCollisionObject(PhysicsRigidBody(
                PlaneCollisionShape(Plane(Vector3f.UNIT_Y, -128f)),
                staticMass
            ))
        })
    }

    init {
        // TODO auto-download native lib, Linux64DebugSp
        NativeLibraryLoader.loadLibbulletjme(true, plugin.dataFolder, "Debug", "Sp")

        plugin.registerEvents(object : Listener {
            @EventHandler
            fun WorldUnloadEvent.on() {
                _worlds.remove(world.uid)?.space?.destroy()
            }

            @EventHandler
            fun PlayerToggleSneakEvent.on() {
                val chunk = player.chunk
                val world = world(player.world)
                if (!world.chunkBodies.contains(chunk.chunkKey)) {
                    world.setChunk(chunk, collisionShapeOf(chunk))
                    player.sendMessage("Computed collision shape for chunk (${chunk.x}, ${chunk.z})")
                    println("Complete shape for (${chunk.x}, ${chunk.z})")
                }
            }

            @EventHandler
            fun PlayerInteractEvent.on() {
                if (
                    (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                    && player.inventory.itemInMainHand.type == Material.STICK
                ) {
                    isCancelled = true
                    val loc = player.eyeLocation
                    val ent = player.world.spawnEntity(loc, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM) { entity ->
                        entity as ArmorStand
                        entity.setAI(false)
                        entity.setGravity(false)
                        entity.isVisible = false
                        entity.equipment.helmet = ItemStack(Material.IRON_NUGGET).withMeta {
                            setCustomModelData(1)
                        }
                    }

                    val space = world(player.world).space
                    val body = PhysicsRigidBody(BoxCollisionShape(0.5f), 1f).also {
                        it.position = loc.bullet()
                        it.applyCentralImpulse(loc.direction.bullet() * (player.inventory.heldItemSlot * 3f))
                        space.addCollisionObject(it)
                    }

                    drops.add(Drop(ent, body.motionState, space, body))
                }
            }

            /*@EventHandler
            fun ChunkLoadEvent.on() {
                val shape = collisionShapeOf(chunk)
                _worlds[world.uid]?.setChunk(chunk, shape)
                println("Complete shape for (${chunk.x}, ${chunk.z})")
            }*/

            @EventHandler
            fun ChunkUnloadEvent.on() {
                _worlds[world.uid]?.removeChunk(chunk.chunkKey)
            }
        })

        plugin.scheduleRepeating { tick() }
    }

    private fun tick() {
        if (!enabled) return

        _worlds.forEach { (_, world) ->
            world.space.update(timeInterval)
        }

        val iter = drops.iterator()
        while (iter.hasNext()) {
            val (entity, motionState, space, body) = iter.next()
            if (!entity.isValid) {
                space.removeCollisionObject(body)
                iter.remove()
                continue
            }

            val rot = Quaternion().also { motionState.getOrientation(it) }

            val loc = motionState.getLocation(null).location(entity.world)

            bukkitPlayers.forEach { player ->
                player.spawnParticle(Particle.VILLAGER_HAPPY, loc, 0)

                val fx = PaperEffectors().apply { init(plugin) }.player(player)
                val tx = Transform(rot.alexandria(), loc.position())
                val verts = boxVertices(Vector3(-0.5), Vector3(0.5)).map { tx.apply(it) }
                fx.showCuboid(ParticleEffect(Key.key("minecraft", "bubble")), verts, 0.1)
            }

            loc.y -= 1.45 // 1.45, difference of 1.0 is because of bounding box

            entity.teleportAsync(loc)
            (entity as ArmorStand).headPose = rot.alexandria().euler(EulerOrder.ZYX).x { -it }.bukkitEuler()
        }

        bukkitPlayers.forEach { player ->
            player.sendActionBar(text("drops active = ${drops.size}"))
        }
    }

    companion object {
        // todo different world sizes
        val chunkVertices: Array<Vector3f> = Array(16 * 16 * 384) { i ->
            val x = i % 16
            val z = (i / 16) % 16
            val y = i / 256

            Vector3f(x.toFloat(), y.toFloat() - 64f, z.toFloat())
        }
    }
}
