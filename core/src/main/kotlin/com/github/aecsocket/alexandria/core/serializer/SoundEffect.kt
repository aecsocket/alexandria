package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.effect.SoundEffect
import com.github.aecsocket.alexandria.core.extension.force
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val SOUND = "sound"
private const val DROPOFF = "dropoff"
private const val RANGE = "range"

object SoundEffectSerializer : TypeSerializer<SoundEffect> {
    override fun serialize(type: Type, obj: SoundEffect?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.node(SOUND).set(obj.sound)
            node.node(DROPOFF).set(obj.dropoff)
            node.node(RANGE).set(obj.range)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = SoundEffect(
        node.node(SOUND).force(),
        node.node(DROPOFF).force(),
        node.node(RANGE).force()
    )
}
