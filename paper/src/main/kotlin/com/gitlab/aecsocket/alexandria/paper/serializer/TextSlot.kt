package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.paper.TextSlot
import net.kyori.adventure.bossbar.BossBar
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val IN_ACTION_BAR = "in_action_bar"

private const val SUBTITLE = "subtitle"
private const val TIMES = "times"

private const val COLOR = "color"
private const val OVERLAY = "overlay"

object TextSlotSerializer : TypeSerializer<TextSlot> {
    override fun serialize(type: Type, obj: TextSlot?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            when (obj) {
                is TextSlot.InActionBar -> node.set(IN_ACTION_BAR)
                else -> node.set(obj)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): TextSlot {
        return if (node.isMap) {
            when {
                node.hasChild(SUBTITLE) -> TextSlot.InTitle(
                    node.node(SUBTITLE).get { false },
                    node.node(TIMES).get { null },
                )
                node.hasChild(COLOR) -> TextSlot.InBossBar(
                    node.node(COLOR).get { BossBar.Color.WHITE },
                    node.node(OVERLAY).get { BossBar.Overlay.PROGRESS },
                )
                else -> throw SerializationException(node, type, "Invalid text slot format")
            }
        } else when (node.force<String>()) {
            IN_ACTION_BAR -> TextSlot.InActionBar
            else -> throw SerializationException(node, type, "Invalid text slot format")
        }
    }
}
