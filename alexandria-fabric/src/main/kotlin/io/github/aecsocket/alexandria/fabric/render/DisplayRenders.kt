package io.github.aecsocket.alexandria.fabric.render

import io.github.aecsocket.alexandria.ItemRenderDesc
import io.github.aecsocket.alexandria.DisplayRenderDesc
import io.github.aecsocket.alexandria.TextRenderDesc
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.world.item.ItemStack

class DisplayRenders : Renders {
    override fun createModel(
        descriptor: ItemRenderDesc,
        item: ItemStack,
        receiver: (Packet<*>) -> Unit,
        basePosition: DVec3,
        transform: FAffine3
    ): ModelRender {
        TODO()
    }

    override fun createText(
        descriptor: TextRenderDesc,
        text: Component,
        receiver: (Packet<*>) -> Unit,
        basePosition: DVec3,
        transform: FAffine3
    ): TextRender {
        TODO("Not yet implemented")
    }

    private abstract inner class DisplayRender(
        basePosition: DVec3,
        transform: FAffine3,
        val protocolId: Int,
        descriptor: DisplayRenderDesc,
        override var receiver: (Packet<*>) -> Unit,
    ) : FabricRender {
        val billboard = descriptor.billboard
        val viewRange = descriptor.viewRange
        val interpolationDelay = descriptor.interpolationDelay
        val interpolationDuration = descriptor.interpolationDuration
    }
}
