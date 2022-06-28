package com.github.aecsocket.alexandria.core.serializer

import com.github.aecsocket.alexandria.core.*
import com.github.aecsocket.alexandria.core.extension.force
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val FACTOR = "factor"
private const val OFFSET = "offset"

object DoubleModSerializer : TypeSerializer<DoubleMod> {
    override fun serialize(type: Type, obj: DoubleMod?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else when (obj) {
            is SetDoubleMod -> node.set(obj.value)
            is OffsetDoubleMod -> {
                node.node(FACTOR).set(obj.factor)
                node.node(OFFSET).set(obj.offset)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): DoubleMod {
        return if (node.isMap) node.force<OffsetDoubleMod>()
        else SetDoubleMod(node.force())
    }
}

object IntModSerializer : TypeSerializer<IntMod> {
    override fun serialize(type: Type, obj: IntMod?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else when (obj) {
            is SetIntMod -> node.set(obj.value)
            is OffsetIntMod -> {
                node.node(OFFSET).set(obj.offset)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): IntMod {
        return if (node.isMap) node.force<OffsetIntMod>()
        else SetIntMod(node.force())
    }
}
