package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.desc.ItemDesc
import io.github.aecsocket.alexandria.desc.ParticleDesc
import io.github.aecsocket.alexandria.desc.RawItemType
import io.github.aecsocket.alexandria.desc.RawParticle
import io.github.aecsocket.alexandria.paper.extension.withMeta
import io.github.aecsocket.klam.DVec3
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

data class PaperParticle(
    val type: Particle,
    val data: Any?,
) : RawParticle

fun ParticleDesc.spawn(player: Player, position: DVec3) {
    val type = type as PaperParticle
    player.spawnParticle(
        type.type,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed, type.data,
    )
}

fun ParticleDesc.spawn(world: World, position: DVec3, force: Boolean = false) {
    val type = type as PaperParticle
    world.spawnParticle(
        type.type,
        position.x, position.y, position.z,
        count,
        size.x, size.y, size.z,
        speed, type.data, force,
    )
}

data class PaperItemType(
    val handle: Material
) : RawItemType

fun ItemDesc.create(count: Int = 1): ItemStack {
    require(count >= 1) { "requires count >= 1" }

    val type = type as PaperItemType
    return ItemStack(type.handle, count).withMeta<ItemMeta> { meta ->
        meta.setCustomModelData(modelData)
        if (meta is Damageable) {
            meta.damage = damage
        }
        meta.isUnbreakable = isUnbreakable
    }
}
