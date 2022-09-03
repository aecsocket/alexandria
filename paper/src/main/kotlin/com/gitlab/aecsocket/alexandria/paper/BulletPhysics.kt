package com.gitlab.aecsocket.alexandria.paper

import com.gitlab.aecsocket.alexandria.core.extension.EulerOrder
import com.gitlab.aecsocket.alexandria.core.extension.euler
import com.gitlab.aecsocket.alexandria.paper.extension.*
import com.jme3.bullet.PhysicsSpace
import com.jme3.bullet.collision.shapes.BoxCollisionShape
import com.jme3.bullet.collision.shapes.PlaneCollisionShape
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.bullet.objects.infos.RigidBodyMotionState
import com.jme3.math.Plane
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.system.NativeLibraryLoader
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2

const val TIME_INTERVAL = 0.05f

class BulletPhysics(
    private val plugin: Alexandria
) {
    var enabled = true
    var timeInterval = TIME_INTERVAL

    private val _spaces = HashMap<UUID, PhysicsSpace>()
    val spaces: Map<UUID, PhysicsSpace> = _spaces

    private data class Drop(
        val entity: Entity,
        val motionState: RigidBodyMotionState,
        val space: PhysicsSpace,
        val body: PhysicsRigidBody
    )

    private val drops = ArrayList<Drop>()

    fun space(world: World) = _spaces.computeIfAbsent(world.uid) {
        PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT).also { space ->
            // TODO temporary
            space.addCollisionObject(PhysicsRigidBody(
                PlaneCollisionShape(Plane(Vector3f.UNIT_Y, 128f)),
                staticMass
            ))

            space.addCollisionObject(PhysicsRigidBody(
                PlaneCollisionShape(Plane(Vector3f.UNIT_Y, -128f)),
                staticMass
            ))
        }
    }

    init {
        // TODO auto-download native lib, Linux64DebugSp
        NativeLibraryLoader.loadLibbulletjme(true, plugin.dataFolder, "Debug", "Sp")

        plugin.registerEvents(object : Listener {
            @EventHandler
            fun WorldUnloadEvent.on() {
                _spaces.remove(world.uid)?.destroy()
            }

            // TODO temporary
            @EventHandler
            fun PlayerInteractEvent.on() {
                val held = player.inventory.itemInMainHand
                if (!held.isEmpty() && held.type.isBlock && held.type == Material.REDSTONE_BLOCK) {
                    isCancelled = true
                    val spawnLoc = player.eyeLocation + player.location.direction * 2.0

                    // TODO add actual shape
                    val shape = BoxCollisionShape(0.5f)

                    // TODO variable mass based on type
                    val body = PhysicsRigidBody(shape, 1f).also {
                        it.position = spawnLoc.bullet()
                    }

                    val entity = player.world.spawnEntity(spawnLoc, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM) { entity ->
                        entity as ArmorStand
                        entity.isVisible = false
                        entity.setAI(false)
                        entity.setGravity(false)
                        //entity.equipment.helmet = held.clone()
                        entity.equipment.helmet = ItemStack(Material.IRON_NUGGET).withMeta {
                            setCustomModelData(1)
                        }
                    }

                    val space = space(player.world)
                    drops.add(Drop(entity, body.motionState, space, body))
                    space.addCollisionObject(body)
                }
            }
        })

        plugin.scheduleRepeating { tick() }
    }

    private fun tick() {
        if (!enabled) return

        _spaces.forEach { (_, space) ->
            space.update(timeInterval)
        }

        val iter = drops.iterator()
        while (iter.hasNext()) {
            val (entity, motionState, space, body) = iter.next()
            if (!entity.isValid) {
                space.removeCollisionObject(body)
                iter.remove()
                continue
            }

            val loc = motionState.getLocation(null).location(entity.world)
            loc.y -= 0.45 // 1.45, difference of 1.0 is because of bounding box

            val rot = Quaternion().also { motionState.getOrientation(it) }

            entity.teleportAsync(loc)
            (entity as ArmorStand).headPose = rot.alexandria().euler(EulerOrder.ZYX).x { -it }.bukkitEuler()
        }

        bukkitPlayers.forEach { player ->
            player.sendActionBar(text("drops active = ${drops.size}"))
        }
    }
}
