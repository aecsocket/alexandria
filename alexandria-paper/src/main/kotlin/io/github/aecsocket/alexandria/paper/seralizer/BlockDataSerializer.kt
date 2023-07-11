package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.extension.force
import java.lang.reflect.Type
import org.bukkit.Bukkit
import org.bukkit.block.data.BlockData
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer

object BlockDataSerializer : TypeSerializer<BlockData> {
  override fun serialize(type: Type, obj: BlockData?, node: ConfigurationNode) {
    if (obj == null) node.set(null)
    else {
      node.set(obj.getAsString(true))
    }
  }

  override fun deserialize(type: Type, node: ConfigurationNode): BlockData {
    return try {
      Bukkit.createBlockData(node.force<String>())
    } catch (ex: IllegalArgumentException) {
      throw SerializationException(node, type, "Invalid block data", ex)
    }
  }
}
