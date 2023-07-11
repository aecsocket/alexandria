package io.github.aecsocket.alexandria.fabric.extension

import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FQuat
import io.github.aecsocket.klam.FVec3
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f

fun Vec3.toDVec() = DVec3(x, y, z)

fun DVec3.toVec3() = Vec3(x, y, z)

fun Vector3f.toFVec() = FVec3(x, y, z)

fun FVec3.toVector3f() = Vector3f(x, y, z)

fun Quaternionf.toFQuat() = FQuat(x, y, z, w)

fun FQuat.toQuaternionf() = Quaternionf(x, y, z, w)
