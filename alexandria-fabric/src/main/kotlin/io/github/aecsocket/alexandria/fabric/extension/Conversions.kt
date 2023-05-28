package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.klam.DVec3
import net.minecraft.world.phys.Vec3

fun Vec3.toDVec() = DVec3(x, y, z)
fun DVec3.toVec3() = Vec3(x, y, z)
