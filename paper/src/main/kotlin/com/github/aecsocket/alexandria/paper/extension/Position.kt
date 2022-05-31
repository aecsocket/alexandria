package com.github.aecsocket.alexandria.paper.extension

import com.github.aecsocket.alexandria.core.vector.Point3
import com.github.aecsocket.alexandria.core.vector.Polar3
import com.github.aecsocket.alexandria.core.vector.Vector3
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

operator fun Vector3.plus(other: Vector)    = Vector3(x + other.x, y + other.y, z + other.z)
operator fun Vector3.plus(other: Location)  = Vector3(x + other.x, y + other.y, z + other.z)
operator fun Vector3.minus(other: Vector)   = Vector3(x - other.x, y - other.y, z - other.z)
operator fun Vector3.minus(other: Location) = Vector3(x - other.x, y - other.y, z - other.z)
operator fun Vector3.times(other: Vector)   = Vector3(x * other.x, y * other.y, z * other.z)
operator fun Vector3.times(other: Location) = Vector3(x * other.x, y * other.y, z * other.z)
operator fun Vector3.div(other: Vector)     = Vector3(x / other.x, y  /other.y, z / other.z)
operator fun Vector3.div(other: Location)   = Vector3(x / other.x, y / other.y, z / other.z)
fun Vector3.bukkit() = Vector(x, y, z)
fun Vector3.location(world: World) = Location(world, x, y, z)

operator fun Vector.component1() = x
operator fun Vector.component2() = y
operator fun Vector.component3() = z
operator fun Vector.unaryMinus() = Vector(-x, -y, -z)

operator fun Vector.plus(other: Vector3)   = Vector(x + other.x, y + other.y, z + other.z)
operator fun Vector.plus(other: Location)  = Vector(x + other.x, y + other.y, z + other.z)
operator fun Vector.plus(value: Double)    = Vector(x + value,   y + value,   z + value)
operator fun Vector.minus(other: Vector3)  = Vector(x - other.x, y - other.y, z - other.z)
operator fun Vector.minus(other: Location) = Vector(x - other.x, y - other.y, z - other.z)
operator fun Vector.minus(value: Double)   = Vector(x - value,   y - value,   z - value)
operator fun Vector.times(other: Vector3)  = Vector(x * other.x, y * other.y, z * other.z)
operator fun Vector.times(other: Location) = Vector(x * other.x, y * other.y, z * other.z)
operator fun Vector.times(value: Double)   = Vector(x * value,   y * value,   z * value)
operator fun Vector.div(other: Vector3)    = Vector(x / other.x, y  /other.y, z / other.z)
operator fun Vector.div(other: Location)   = Vector(x / other.x, y / other.y, z / other.z)
operator fun Vector.div(value: Double)     = Vector(x / value,   y / value,   z / value)

fun Vector.alexandria() = Vector3(x, y, z)

operator fun Location.component1() = x
operator fun Location.component2() = y
operator fun Location.component3() = z
operator fun Location.component4() = yaw
operator fun Location.component5() = pitch
operator fun Location.component6(): World? = world
operator fun Location.unaryMinus() = copy(x = -x, y = -y, z = -z)

operator fun Location.plus(other: Vector3)  = copy(x = x + other.x, y = y + other.y, z = z + other.z)
operator fun Location.plus(other: Vector)   = copy(x = x + other.x, y = y + other.y, z = z + other.z)
operator fun Location.plus(value: Double)   = copy(x = x + value,   y = y + value,   z = z + value)
operator fun Location.minus(other: Vector3) = copy(x = x - other.x, y = y - other.y, z = z - other.z)
operator fun Location.minus(other: Vector)  = copy(x = x - other.x, y = y - other.y, z = z - other.z)
operator fun Location.minus(value: Double)  = copy(x = x - value,   y = y - value,   z = z - value)
operator fun Location.times(other: Vector3) = copy(x = x * other.x, y = y * other.y, z = z * other.z)
operator fun Location.times(other: Vector)  = copy(x = x * other.x, y = y * other.y, z = z * other.z)
operator fun Location.times(value: Double)  = copy(x = x * value,   y = y * value,   z = z * value)
operator fun Location.div(other: Vector3)   = copy(x = x / other.x, y = y  /other.y, z = z / other.z)
operator fun Location.div(other: Vector)    = copy(x = x / other.x, y = y / other.y, z = z / other.z)
operator fun Location.div(value: Double)    = copy(x = x / value,   y = y / value,   z = z / value)

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
fun Location.polar() = Polar3(1.0, yaw.toDouble(), pitch.toDouble())
