package io.github.aecsocket.alexandria.fabric.render

import io.github.aecsocket.alexandria.ModelDescriptor
import io.github.aecsocket.alexandria.Render
import io.github.aecsocket.alexandria.TextDescriptor
import io.github.aecsocket.alexandria.fabric.mixin.BroadcastAccessor
import io.github.aecsocket.klam.*
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.server.level.ServerEntity
import net.minecraft.world.item.ItemStack

fun ServerEntity.packetReceiver() = (this as BroadcastAccessor).broadcast

sealed interface FabricRender : Render

interface ModelRender : FabricRender {
    var item: ItemStack
}

interface TextRender : FabricRender {
    var text: Component
}

interface Renders {
    fun createModel(
        descriptor: ModelDescriptor,
        item: ItemStack,
        receiver: (Packet<*>) -> Unit,
        basePosition: DVec3,
        transform: FAffine3,
    ): ModelRender

    fun createText(
        descriptor: TextDescriptor,
        text: Component,
        receiver: (Packet<*>) -> Unit,
        basePosition: DVec3,
        transform: FAffine3,
    ): TextRender
}
