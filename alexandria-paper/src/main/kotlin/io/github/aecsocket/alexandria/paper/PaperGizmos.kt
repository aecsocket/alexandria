package io.github.aecsocket.alexandria.paper

import io.github.aecsocket.alexandria.GizmoDraw
import io.github.aecsocket.alexandria.Gizmos
import io.github.aecsocket.alexandria.ItemRenderDesc
import io.github.aecsocket.klam.*
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack

private val lineItem = ItemStack(Material.STONE)

class PaperGizmos(
    private val plugin: AlexandriaPlugin<*>,
) : Gizmos<World> {
    override fun line(settings: GizmoDraw, world: World, from: DVec3, to: DVec3) {
        val delta = to - from
        val render = DisplayRenders.createItem(
            world = world,
            position = from,
            transform = FAffine3(
                rotation = FQuat.ofAxisAngle(FVec3(normalize(delta)), 0.0f),
                scale = FVec3(length(delta).toFloat(), 0.1f, 0.1f),
            ),
            item = lineItem,
            desc = ItemRenderDesc(glowColor = settings.color),
        )
        plugin.scheduling.onEntity(render.entity).runLater((settings.duration * 20).toLong()) {
            render.remove()
        }
    }
}
