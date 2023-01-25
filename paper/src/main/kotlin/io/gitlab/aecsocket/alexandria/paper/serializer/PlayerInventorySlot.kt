package io.gitlab.aecsocket.alexandria.paper.serializer

import io.gitlab.aecsocket.alexandria.core.extension.force
import io.gitlab.aecsocket.alexandria.paper.PlayerInventorySlot
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

object PlayerInventorySlotSerializer : TypeSerializer<PlayerInventorySlot> {
    override fun serialize(type: Type, obj: PlayerInventorySlot?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            when (obj) {
                is PlayerInventorySlot.ByInteger -> node.set(obj.slot)
                is PlayerInventorySlot.ByEquipment -> node.set(obj.slot)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): PlayerInventorySlot {
        val raw = node.raw()
            ?: throw SerializationException(node, type, "Must be expressed as integer or slot name")
        return when (raw) {
            is Int -> PlayerInventorySlot.ByInteger(raw.toInt())
            is String -> PlayerInventorySlot.ByEquipment(node.force())
            else -> throw SerializationException(node, type, "Must be expressed as integer or slot name")
        }
    }
}
