package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.alexandria.core.effect.ParticleEffect
import com.gitlab.aecsocket.alexandria.core.extension.EulerOrder
import com.gitlab.aecsocket.alexandria.core.extension.boxVertices
import com.gitlab.aecsocket.alexandria.core.extension.euler
import com.gitlab.aecsocket.alexandria.core.extension.showCuboid
import com.gitlab.aecsocket.alexandria.core.physics.BoxShape
import com.gitlab.aecsocket.alexandria.core.physics.Transform
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import com.gitlab.aecsocket.alexandria.paper.effect.PaperEffectors
import com.gitlab.aecsocket.alexandria.paper.extension.*
import com.jme3.bullet.PhysicsSpace
import com.jme3.bullet.collision.shapes.BoxCollisionShape
import com.jme3.bullet.collision.shapes.CompoundCollisionShape
import com.jme3.bullet.collision.shapes.PlaneCollisionShape
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.bullet.objects.infos.RigidBodyMotionState
import com.jme3.math.Plane
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.system.NativeLibraryLoader
import kotlinx.coroutines.*
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
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

const val TIME_INTERVAL = 0.05f

// use java classes here because we can't access Bullet classes from non-main threads
// it *will* cause a JVM internal error
private data class BlockBody(val shape: BoxShape, val offset: Vector3)

private suspend fun chunkCollisionBodies(world: World, chunk: ChunkSnapshot): List<BlockBody> = coroutineScope {
    val bodies = (0 until 16).flatMap { x ->
        (0 until 16).flatMap { z ->
            (world.minHeight until world.maxHeight).map { y -> async {
                val block = chunk.getBlockData(x, y, z)
                // todo proper stuff
                return@async if (block.material.isSolid) listOf(
                    BlockBody(BoxShape.Half, Vector3(x + 0.5, y + 0.5, z + 0.5))
                ) else emptyList()
            } }
        }
    }.awaitAll()

    bodies.flatten()
}

class BulletPhysics(
    private val plugin: Alexandria
) {
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
        PhysicsWorld(PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT).also { space ->
            space.addCollisionObject(PhysicsRigidBody(
                PlaneCollisionShape(Plane(Vector3f.UNIT_Y, -128f)),
                staticMass
            ))
        })
    }

    init {
        // TODO auto-download native lib, Linux64DebugSpMt
        NativeLibraryLoader.loadLibbulletjme(true, plugin.dataFolder, "Debug", "SpMt")

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
                    world.startChunkUpdate(chunk)
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

            private var a = 0

            @EventHandler
            fun ChunkLoadEvent.on() {
                a++
                plugin.log.line(LogLevel.Verbose) { "Loaded $a" }
                //world(world).startChunkUpdate(chunk)
            }

            /*@EventHandler
            fun EntitiesLoadEvent.on() {
                entities.forEach { entity ->
                    val body = PhysicsRigidBody(BoxCollisionShape(0.4f), 150f)


                    world(world).space.addCollisionObject(body)
                }
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
            world.update(timeInterval)
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

    private data class ChunkUpdate(
        val key: Long,
        val x: Int,
        val z: Int,
        val bodies: List<BlockBody>,
    )

    inner class PhysicsWorld(
        val space: PhysicsSpace
    ) {
        private val _chunkBodies = HashMap<Long, PhysicsRigidBody>()
        val chunkBodies: Map<Long, PhysicsRigidBody> get() = _chunkBodies

        // TODO better data structure?
        private val chunkUpdates = ArrayList<ChunkUpdate>()

        private var consumed = 0

        internal fun update(dt: Float) {
            space.update(dt)
            runBlocking {
                chunkUpdates.map { (key, x, z, bodies) -> async {
                    val start = System.currentTimeMillis()
                    val shape = CompoundCollisionShape()
                    /*bodies.forEach {
                        shape.addChildShape(
                            BoxCollisionShape(it.shape.halfExtent.bullet()),
                            it.offset.bullet()
                        )
                    }*/

                    removeChunk(key)
                    _chunkBodies[key] = PhysicsRigidBody(shape, staticMass).also {
                        it.position = Vector3f(x * 16f, 0f, z * 16f)
                        space.addCollisionObject(it)
                    }
                    consumed++
                    plugin.log.line(LogLevel.Verbose) { "Consumed ($x, $z) in ${System.currentTimeMillis() - start} ms (total = $consumed)" }
                } }.awaitAll()
                chunkUpdates.clear()
            }
        }

        fun removeChunk(key: Long) {
            _chunkBodies[key]?.also { space.removeCollisionObject(it) }
        }

        fun startChunkUpdate(chunk: Chunk) {
            runBlocking(Dispatchers.Default) {
                plugin.log.line(LogLevel.Verbose) { "Computing shape for (${chunk.x}, ${chunk.z})..." }
                val start = System.currentTimeMillis()
                val shape = chunkCollisionBodies(
                    chunk.world,
                    chunk.getChunkSnapshot(false, false, false)
                )
                plugin.log.line(LogLevel.Verbose) { "Computed in ${System.currentTimeMillis() - start} ms" }
                chunkUpdates.add(ChunkUpdate(chunk.chunkKey, chunk.x, chunk.z, shape))
            }
        }
    }
}
