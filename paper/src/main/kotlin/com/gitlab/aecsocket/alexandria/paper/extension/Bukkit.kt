package com.gitlab.aecsocket.alexandria.paper.extension

import com.gitlab.aecsocket.alexandria.core.extension.*
import com.gitlab.aecsocket.alexandria.core.physics.Quaternion
import com.gitlab.aecsocket.alexandria.core.physics.Transform
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.craftbukkit.v1_19_R2.CraftChunk
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

val bukkitAir = ItemStack(Material.AIR)
val bukkitCurrentTick get() = Bukkit.getCurrentTick()
@Suppress("DEPRECATION")
val bukkitNextEntityId get() = Bukkit.getUnsafe().nextEntityId()
val bukkitPlayers get() = Bukkit.getOnlinePlayers()

var Entity.transform: Transform
    get() = Transform(
        // we ignore pitch, because entities can't really *rotate* up/down
        rotation = Euler3(y = -location.yaw.radians.toDouble()).quaternion(EulerOrder.ZYX),
        position = location.position(),
    )
    set(value) {
        val (x, y, z) = value.position
        val yaw = value.rotation.euler(EulerOrder.XYZ).yaw.degrees.toFloat()
        val location = location.copy(x = x, y = y, z = z, yaw = yaw)
        teleport(location)
    }

var Entity.looking: Quaternion
    // here we DON'T ignore pitch, since entities can *look* up/down
    get() = Euler3(location.pitch.toDouble(), -location.yaw.toDouble()).radians.quaternion(EulerOrder.ZYX)
    set(value) {
        val (pitch, yaw) = value.euler(EulerOrder.XYZ).degrees
        setRotation(yaw.toFloat(), pitch.toFloat())
    }

fun AttributeInstance.forceModifier(modifier: AttributeModifier) {
    removeModifier(modifier)
    addModifier(modifier)
}

fun Player.give(item: ItemStack) {
    val inventory = inventory
    val heldItemSlot = inventory.heldItemSlot
    if (inventory.getItem(heldItemSlot).isEmpty()) {
        inventory.setItem(heldItemSlot, item)
    } else {
        inventory.addItem(item).forEach { (_, nonAdded) ->
            world.dropItem(location, nonAdded)
        }
    }
}

fun Chunk.trackedPlayers(): List<Player> {
    val handle = (this as CraftChunk).handle
    return handle.level.chunkSource.chunkMap.getPlayers(handle.pos, false).map { it.bukkitEntity }
}
