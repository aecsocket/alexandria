package com.github.aecsocket.alexandria.paper.bound

import com.github.aecsocket.alexandria.core.bound.Box
import com.github.aecsocket.alexandria.core.bound.Compound
import com.github.aecsocket.alexandria.paper.extension.*
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.util.BoundingBox
import org.bukkit.util.VoxelShape

fun BoundingBox.bound() = Box(min.alexandria(), max.alexandria(), 0.0)

fun VoxelShape.bound() = Compound(
    boundingBoxes.map { it.bound() }
)

fun Block.bound() = Compound(
    collisionShape.boundingBoxes.map { it.bound() }
)

fun Entity.bound() = Box(
    (boundingBox.min - location).alexandria(),
    (boundingBox.max - location).alexandria(),
    location.yaw.toDouble()
)
