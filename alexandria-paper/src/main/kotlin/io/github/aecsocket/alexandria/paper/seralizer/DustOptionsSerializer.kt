package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.extension.force
import io.github.aecsocket.alexandria.paper.extension.toColor
import io.github.aecsocket.alexandria.paper.extension.toTextColor
import java.lang.reflect.Type
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Particle.DustOptions
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer

private const val COLOR = "color"
private const val SIZE = "size"

object DustOptionsSerializer : TypeSerializer<DustOptions> {
  override fun serialize(type: Type, obj: DustOptions?, node: ConfigurationNode) {
    if (obj == null) node.set(null)
    else {
      node.node(COLOR).set(obj.color.toTextColor())
      node.node(SIZE).set(obj.size)
    }
  }

  override fun deserialize(type: Type, node: ConfigurationNode): DustOptions {
    val size = node.node(SIZE).force<Float>()
    if (size < 0) throw SerializationException(node, type, "Size must be >= 0")
    return DustOptions(
        node.node(COLOR).force<TextColor>().toColor(),
        size,
    )
  }
}
