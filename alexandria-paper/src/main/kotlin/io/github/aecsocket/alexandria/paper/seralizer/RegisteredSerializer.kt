package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.desc.ItemType
import io.github.aecsocket.alexandria.extension.force
import io.github.aecsocket.alexandria.paper.PaperItemType
import io.github.aecsocket.alexandria.paper.extension.toNamespaced
import net.kyori.adventure.key.Key
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.Registry
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class RegisteredSerializer<T : Keyed>(
    private val registry: Registry<T>,
    private val typeName: String,
) : TypeSerializer<T> {
    override fun serialize(type: Type, obj: T?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(obj.key)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): T {
        val key = node.force<Key>()
        return registry[key.toNamespaced()]
            ?: throw SerializationException(node, type, "Invalid $typeName $key")
    }
}

inline fun <reified T : Keyed> RegisteredSerializer(registry: Registry<T>) =
    RegisteredSerializer(registry, T::class.simpleName!!)

object ItemTypeSerializer : TypeSerializer<ItemType> {
    override fun serialize(type: Type, obj: ItemType?, node: ConfigurationNode) {
        if (obj !is PaperItemType) return
        node.set(obj.handle)
    }

    override fun deserialize(type: Type, node: ConfigurationNode): ItemType {
        val handle = node.force<Material>()
        return PaperItemType(handle)
    }
}
