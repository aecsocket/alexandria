package io.github.aecsocket.alexandria.paper.extension

import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FQuat
import io.github.aecsocket.klam.FVec3
import io.github.aecsocket.klam.IVec3
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.Vector

fun Vector.asKlam() = DVec3(x, y, z)
fun Vector.asKlamF() = FVec3(x.toFloat(), y.toFloat(), z.toFloat())

fun DVec3.asPaper() = Vector(x, y, z)
fun FVec3.asPaperD() = Vector(x.toDouble(), y.toDouble(), z.toDouble())

fun Location.position() = DVec3(x, y, z)
fun Location.direction() = direction.asKlamF()
fun Location.rotation() = FQuat.ofAxisAngle(direction(), 0.0f)
fun DVec3.location(world: World, pitch: Float = 0.0f, yaw: Float = 0.0f) = Location(world, x, y, z, yaw, pitch)

fun Block.position() = IVec3(x, y, z)
