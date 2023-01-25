package io.gitlab.aecsocket.alexandria.paper.serializer

import io.gitlab.aecsocket.alexandria.core.extension.force
import io.gitlab.aecsocket.alexandria.core.extension.forceList
import org.bukkit.Particle.DustOptions
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val SIZE = "size"
private const val COLOR = "color"

object DustOptionsSerializer : TypeSerializer<DustOptions> {
    override fun serialize(type: Type, obj: DustOptions?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.node(COLOR).set(obj.color)
            node.node(SIZE).set(obj.size)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = DustOptions(
        node.node(COLOR).force(),
        node.node(SIZE).force()
    )
}
