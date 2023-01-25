package io.gitlab.aecsocket.alexandria.paper.extension

import io.gitlab.aecsocket.alexandria.core.physics.Point3
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendBlockDamage(id: Int, position: Point3, stage: Int) {
    this as CraftPlayer
    handle.connection.send(ClientboundBlockDestructionPacket(
        id, BlockPos(position.x, position.y, position.z), stage
    ))
}

val GameMode.survival get() = when (this) {
    GameMode.SURVIVAL, GameMode.ADVENTURE -> true
    else -> false
}
