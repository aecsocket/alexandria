package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.desc.ItemDesc
import io.github.aecsocket.alexandria.desc.ParticleDesc
import io.github.aecsocket.alexandria.desc.ItemType
import io.github.aecsocket.alexandria.desc.ParticleType
import io.github.aecsocket.alexandria.paper.extension.toNamespaced
import io.github.aecsocket.alexandria.paper.extension.withMeta
import io.github.aecsocket.alexandria.paper.seralizer.particleFromKey
import io.github.aecsocket.klam.DVec3
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Registry
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

data class PaperParticleType(
    val type: Particle,
    val data: Any?,
) : ParticleType.Raw {
    init {
        if (type.dataType != Unit::class.java) {
            val data = data
                ?: throw IllegalArgumentException("Particle $type requires data of type ${type.dataType}")
            if (type.dataType != data::class.java)
                throw IllegalArgumentException("Particle $type requires data of type ${type.dataType}")
        }
    }
}

fun ParticleDesc.spawn(player: Player, position: DVec3) {
    val (type, data) = when (val type = type) {
        is ParticleType.Keyed -> {
            (particleFromKey(type.key) ?: throw IllegalStateException("Invalid particle type ${type.key}")) to null
        }
        is ParticleType.Raw -> {
            type as PaperParticleType
            type.type to type.data
        }
    }
    player.spawnParticle(
        type,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed, data,
    )
}

fun ParticleDesc.spawn(world: World, position: DVec3, force: Boolean = false) {
    val type = type as PaperParticleType
    world.spawnParticle(
        type.type,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed, type.data, force,
    )
}

data class PaperItemType(val handle: Material) : ItemType.Raw

fun ItemDesc.create(count: Int = 1): ItemStack {
    require(count >= 1) { "requires count >= 1" }

    val type = when (val type = type) {
        is ItemType.Keyed -> Registry.MATERIAL[type.key.toNamespaced()]
            ?: throw IllegalStateException("Invalid item type ${type.key}")
        is ItemType.Raw -> (type as PaperItemType).handle
    }
    return ItemStack(type, count).withMeta<ItemMeta> { meta ->
        meta.setCustomModelData(modelData)
        if (meta is Damageable) {
            meta.damage = damage
        }
        meta.isUnbreakable = isUnbreakable
    }
}
