package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.forceList
import org.bukkit.Particle.DustOptions
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

object DustOptionsSerializer : TypeSerializer<DustOptions> {
    override fun serialize(type: Type, obj: DustOptions?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.appendListNode().set(obj.size)
            node.appendListNode().set(obj.color)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): DustOptions {
        val list = node.forceList(type, "size", "color")
        return DustOptions(
            list[1].force(),
            list[0].force()
        )
    }
}
