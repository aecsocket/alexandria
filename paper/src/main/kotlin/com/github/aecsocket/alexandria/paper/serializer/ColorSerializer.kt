package com.github.aecsocket.alexandria.paper.serializer

import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.paper.extension.asAdventure
import com.github.aecsocket.alexandria.paper.extension.asBukkit
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

object ColorSerializer : TypeSerializer<Color> {
    override fun serialize(type: Type, obj: Color?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(obj.asAdventure())
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = node.force<TextColor>().asBukkit()
}
