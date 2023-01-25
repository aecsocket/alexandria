package io.gitlab.aecsocket.alexandria.core.serializer

import io.gitlab.aecsocket.alexandria.core.ColorMod
import io.gitlab.aecsocket.alexandria.core.HSBColorMod
import io.gitlab.aecsocket.alexandria.core.LerpColorMod
import io.gitlab.aecsocket.alexandria.core.extension.force
import io.gitlab.aecsocket.alexandria.core.extension.forceMap
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val TO = "to"
private const val FAC = "fac"

private const val HSB = "hsb"

object ColorModSerializer : TypeSerializer<ColorMod> {
    override fun serialize(type: Type, obj: ColorMod?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else when (obj) {
            is LerpColorMod -> {
                node.node(TO).set(obj.to)
                node.node(FAC).set(obj.fac)
            }
            is HSBColorMod -> {
                node.node(HSB).set(obj.hsb)
            }
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): ColorMod {
        node.forceMap(type)
        return when {
            node.hasChild(TO) -> LerpColorMod(
                node.node(TO).force(),
                node.node(FAC).force()
            )
            node.hasChild(HSB) -> HSBColorMod(
                node.node(HSB).force()
            )
            else -> throw SerializationException(node, type, "Color modifier must be expressed as lerp or HSB factor")
        }
    }
}
