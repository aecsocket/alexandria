package io.github.aecsocket.alexandria.fabric

import io.github.aecsocket.alexandria.desc.ItemDesc
import io.github.aecsocket.alexandria.desc.ParticleDesc
import io.github.aecsocket.alexandria.desc.RawItemType
import io.github.aecsocket.alexandria.desc.RawParticle
import io.github.aecsocket.klam.DVec3
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.IntTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

data class FabricParticle(
    val handle: ParticleOptions,
) : RawParticle

fun ParticleDesc.spawn(player: ServerPlayer, position: DVec3, force: Boolean = false) {
    val type = type as FabricParticle
    player.getLevel().sendParticles(
        player,
        type.handle,
        force,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed,
    )
}

fun ParticleDesc.spawn(world: ServerLevel, position: DVec3) {
    val type = type as FabricParticle
    world.sendParticles(
        type.handle,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed
    )
}

data class FabricItemType(
    val handle: Item,
) : RawItemType

fun ItemDesc.create(count: Int = 1): ItemStack {
    require(count >= 1) { "requires count >= 1" }

    val type = type as FabricItemType
    val item = ItemStack(type.handle, count)
    item.addTagElement("CustomModelData", IntTag.valueOf(modelData))
    item.damageValue = damage
    if (isUnbreakable) {
        item.addTagElement("Unbreakable", ByteTag.valueOf(true))
    }
    return item
}
