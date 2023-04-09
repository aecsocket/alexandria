package io.github.aecsocket.alexandria.paper.extension

import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.IVec3
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.Vector

fun Vector.asKlam() = DVec3(x, y, z)

fun DVec3.asPaper() = Vector(x, y, z)

fun Location.position() = DVec3(x, y, z)
fun DVec3.location(world: World, yaw: Float = 0.0f, pitch: Float = 0.0f) = Location(world, x, y, z, yaw, pitch)

fun Block.position() = IVec3(x, y, z)
