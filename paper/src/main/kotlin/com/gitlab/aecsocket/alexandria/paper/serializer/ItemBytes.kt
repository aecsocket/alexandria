package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.paper.ItemBytes
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type
import java.util.Base64

object ItemBytesSerializer : TypeSerializer<ItemBytes> {
    override fun serialize(type: Type, obj: ItemBytes?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(Base64.getEncoder().encodeToString(obj.bytes))
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = ItemBytes(
        Base64.getDecoder().decode(node.force<String>())
    )
}
