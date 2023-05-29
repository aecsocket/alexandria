package io.github.aecsocket.alexandria.fabric.render

import io.github.aecsocket.alexandria.ModelDescriptor
import io.github.aecsocket.alexandria.TextDescriptor
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.world.item.ItemStack

class DisplayRenders : Renders {
    override fun createModel(
        descriptor: ModelDescriptor,
        item: ItemStack,
        receiver: (Packet<*>) -> Unit,
        basePosition: DVec3,
        transform: FAffine3
    ): ModelRender {
        TODO()
    }

    override fun createText(
        descriptor: TextDescriptor,
        text: Component,
        receiver: (Packet<*>) -> Unit,
        basePosition: DVec3,
        transform: FAffine3
    ): TextRender {
        TODO("Not yet implemented")
    }
}
