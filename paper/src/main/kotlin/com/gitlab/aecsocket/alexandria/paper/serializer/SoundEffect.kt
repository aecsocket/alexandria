package com.gitlab.aecsocket.alexandria.paper.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.paper.effect.SoundEffect
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val DROPOFF = "dropoff"
private const val RANGE = "range"

object SoundEffectSerializer : TypeSerializer<SoundEffect> {
    override fun serialize(type: Type, obj: SoundEffect?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(obj.sound)
            node.node(DROPOFF).set(obj.dropoff)
            node.node(RANGE).set(obj.range)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = SoundEffect(
        node.force(),
        node.node(DROPOFF).force(),
        node.node(RANGE).force()
    )
}
