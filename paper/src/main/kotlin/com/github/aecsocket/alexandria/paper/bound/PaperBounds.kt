package com.github.aecsocket.alexandria.paper.bound

import com.github.aecsocket.alexandria.core.bound.Box
import com.github.aecsocket.alexandria.core.bound.Compound
import com.github.aecsocket.alexandria.paper.extension.*
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.util.BoundingBox
import org.bukkit.util.VoxelShape

fun BoundingBox.bound() = Box(min.alexandria(), max.alexandria())

fun VoxelShape.bound() = Compound(
    boundingBoxes.map { it.bound() }
)

fun Block.bound() = when (type) {
    Material.AIR, Material.WATER, Material.LAVA -> Box.ZeroOne
    else -> if (type.isOccluding) Box.ZeroOne else collisionShape.bound()
}

fun Entity.bound() = Box(
    (boundingBox.min - location).alexandria(),
    (boundingBox.max - location).alexandria(),
    location.vector(),
    location.yaw.toDouble()
)
