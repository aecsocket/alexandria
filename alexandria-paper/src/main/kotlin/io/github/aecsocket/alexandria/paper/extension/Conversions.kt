package io.github.aecsocket.alexandria.paper.extension

import io.github.aecsocket.klam.*
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.Vector

fun Vector.toDVec() = DVec3(x, y, z)
fun Vector.toFVec() = FVec3(x.toFloat(), y.toFloat(), z.toFloat())

fun DVec3.toVector() = Vector(x, y, z)
fun FVec3.toVector() = Vector(x.toDouble(), y.toDouble(), z.toDouble())

fun Location.position() = DVec3(x, y, z)
fun Location.direction() = direction.toDVec()
fun Location.rotation() = DQuat.ofAxisAngle(direction(), 0.0)
fun Location.isometry() = DIso3(position(), rotation())
fun DVec3.location(world: World, pitch: Float = 0.0f, yaw: Float = 0.0f) = Location(world, x, y, z, yaw, pitch)
fun DIso3.location(world: World): Location {
    val (x, y, z) = translation
    val rot = asEuler(rotation, EulerOrder.XYZ)
    return Location(world, x, y, z, rot.y.toFloat(), rot.x.toFloat())
}

fun Block.position() = IVec3(x, y, z)
