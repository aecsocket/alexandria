package com.github.aecsocket.alexandria.paper.extension

import com.github.aecsocket.alexandria.core.vector.Point3
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendBlockDamage(id: Int, position: Point3, stage: Int) {
    this as CraftPlayer
    handle.connection.send(ClientboundBlockDestructionPacket(
        id, BlockPos(position.x, position.y, position.z), stage
    ))
}