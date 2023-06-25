package io.github.aecsocket.alexandria.fabric

import io.github.aecsocket.alexandria.desc.ItemDesc
import io.github.aecsocket.alexandria.desc.ParticleDesc
import io.github.aecsocket.alexandria.desc.ItemType
import io.github.aecsocket.alexandria.desc.ParticleType
import io.github.aecsocket.klam.DVec3
import net.kyori.adventure.platform.fabric.FabricAudiences
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.IntTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

data class FabricParticleType(
    val handle: ParticleOptions,
) : ParticleType.Raw

fun ParticleDesc.spawn(player: ServerPlayer, position: DVec3, force: Boolean = false) {
    val type = when (val type = type) {
        is ParticleType.Keyed -> {
            (BuiltInRegistries.PARTICLE_TYPE[FabricAudiences.toNative(type.key)]
                ?: throw IllegalStateException("Invalid particle type ${type.key}"))
                    as? SimpleParticleType ?: throw IllegalStateException("Particle type ${type.key} is not simple")
        }
        is ParticleType.Raw -> (type as FabricParticleType).handle
    }
    player.serverLevel().sendParticles(
        player,
        type,
        force,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed,
    )
}

fun ParticleDesc.spawn(world: ServerLevel, position: DVec3) {
    val type = type as FabricParticleType
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
) : ItemType.Raw

fun ItemDesc.create(count: Int = 1): ItemStack {
    require(count >= 1) { "requires count >= 1" }

    val type = when (val type = type) {
        is ItemType.Keyed -> BuiltInRegistries.ITEM[FabricAudiences.toNative(type.key)]
        is ItemType.Raw -> (type as FabricItemType).handle
    }
    val item = ItemStack(type, count)
    item.addTagElement("CustomModelData", IntTag.valueOf(modelData))
    item.damageValue = damage
    if (isUnbreakable) {
        item.addTagElement("Unbreakable", ByteTag.valueOf(true))
    }
    return item
}
