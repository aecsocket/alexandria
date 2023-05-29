package io.github.aecsocket.alexandria.fabric.render

import io.github.aecsocket.alexandria.ModelDescriptor
import io.github.aecsocket.alexandria.RenderDescriptor
import io.github.aecsocket.alexandria.TextDescriptor
import io.github.aecsocket.klam.DVec3
import io.github.aecsocket.klam.FAffine3
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
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

    private abstract inner class DisplayRender(
        basePosition: DVec3,
        transform: FAffine3,
        val protocolId: Int,
        descriptor: RenderDescriptor,
        override var receiver: (Packet<*>) -> Unit,
    ) : FabricRender {
        val billboard = descriptor.billboard
        val viewRange = descriptor.viewRange
        val interpolationDelay = descriptor.interpolationDelay
        val interpolationDuration = descriptor.interpolationDuration
    }
}
