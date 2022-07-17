package com.gitlab.aecsocket.alexandria.paper.extension

import com.gitlab.aecsocket.alexandria.core.extension.*
import com.gitlab.aecsocket.alexandria.core.physics.Point3
import com.gitlab.aecsocket.alexandria.core.physics.Ray
import com.gitlab.aecsocket.alexandria.core.physics.Vector3
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

operator fun Vector3.plus(v: Vector)    = Vector3(x + v.x, y + v.y, z + v.z)
operator fun Vector3.plus(l: Location)  = Vector3(x + l.x, y + l.y, z + l.z)
operator fun Vector3.minus(v: Vector)   = Vector3(x - v.x, y - v.y, z - v.z)
operator fun Vector3.minus(l: Location) = Vector3(x - l.x, y - l.y, z - l.z)
operator fun Vector3.times(v: Vector)   = Vector3(x * v.x, y * v.y, z * v.z)
operator fun Vector3.times(l: Location) = Vector3(x * l.x, y * l.y, z * l.z)
operator fun Vector3.div(v: Vector)     = Vector3(x / v.x, y  /v.y, z / v.z)
operator fun Vector3.div(l: Location)   = Vector3(x / l.x, y / l.y, z / l.z)
fun Vector3.bukkit() = Vector(x, y, z)
fun Vector3.location(world: World) = Location(world, x, y, z)

operator fun Vector.component1() = x
operator fun Vector.component2() = y
operator fun Vector.component3() = z
operator fun Vector.unaryMinus() = Vector(-x, -y, -z)

operator fun Vector.plus(v: Vector3)   = Vector(x + v.x, y + v.y, z + v.z)
operator fun Vector.plus(l: Location)  = Vector(x + l.x, y + l.y, z + l.z)
operator fun Vector.plus(s: Double)    = Vector(x + s,   y + s,   z + s)
operator fun Vector.minus(v: Vector3)  = Vector(x - v.x, y - v.y, z - v.z)
operator fun Vector.minus(l: Location) = Vector(x - l.x, y - l.y, z - l.z)
operator fun Vector.minus(s: Double)   = Vector(x - s,   y - s,   z - s)
operator fun Vector.times(v: Vector3)  = Vector(x * v.x, y * v.y, z * v.z)
operator fun Vector.times(l: Location) = Vector(x * l.x, y * l.y, z * l.z)
operator fun Vector.times(s: Double)   = Vector(x * s,   y * s,   z * s)
operator fun Vector.div(v: Vector3)    = Vector(x / v.x, y  /v.y, z / v.z)
operator fun Vector.div(l: Location)   = Vector(x / l.x, y / l.y, z / l.z)
operator fun Vector.div(s: Double)     = Vector(x / s,   y / s,   z / s)

fun Vector.alexandria() = Vector3(x, y, z)

operator fun Location.component1() = x
operator fun Location.component2() = y
operator fun Location.component3() = z
operator fun Location.component4() = yaw
operator fun Location.component5() = pitch
operator fun Location.component6(): World? = world

operator fun Location.plus(v: Vector3)  = copy(x = x + v.x, y = y + v.y, z = z + v.z)
operator fun Location.plus(v: Vector)   = copy(x = x + v.x, y = y + v.y, z = z + v.z)
operator fun Location.plus(s: Double)   = copy(x = x + s,   y = y + s,   z = z + s)
operator fun Location.minus(v: Vector3) = copy(x = x - v.x, y = y - v.y, z = z - v.z)
operator fun Location.minus(v: Vector)  = copy(x = x - v.x, y = y - v.y, z = z - v.z)
operator fun Location.minus(s: Double)  = copy(x = x - s,   y = y - s,   z = z - s)
operator fun Location.times(v: Vector3) = copy(x = x * v.x, y = y * v.y, z = z * v.z)
operator fun Location.times(v: Vector)  = copy(x = x * v.x, y = y * v.y, z = z * v.z)
operator fun Location.times(s: Double)  = copy(x = x * s,   y = y * s,   z = z * s)
operator fun Location.div(v: Vector3)   = copy(x = x / v.x, y = y  /v.y, z = z / v.z)
operator fun Location.div(v: Vector)    = copy(x = x / v.x, y = y / v.y, z = z / v.z)
operator fun Location.div(s: Double)    = copy(x = x / s,   y = y / s,   z = z / s)

fun Location.copy(
    world: World = this.world,
    x: Double = this.x,
    y: Double = this.y,
    z: Double = this.z,
    yaw: Float = this.yaw,
    pitch: Float = this.pitch
) = Location(world, x, y, z, yaw, pitch)
fun Location.vector() = Vector3(x, y, z)
fun Location.point() = Point3(blockX, blockY, blockZ)
fun Location.polar() = Polar2(pitch.radians.toDouble(), yaw.radians.toDouble())
fun Location.direction() = direction.alexandria()
fun Location.rotation() = Euler3(pitch.toDouble(), -yaw.toDouble(), 0.0).radians.quaternion(EulerOrder.ZYX)
fun Location.ray() = Ray(vector(), direction.alexandria())

val BoundingBox.extent get() = Vector3(
    maxX - minX, maxY - minY, maxZ - minZ
)
