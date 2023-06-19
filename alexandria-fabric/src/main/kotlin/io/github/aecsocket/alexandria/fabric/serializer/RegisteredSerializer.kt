package io.github.aecsocket.alexandria.fabric.serializer

import io.github.aecsocket.alexandria.desc.RawItemType
import io.github.aecsocket.alexandria.extension.force
import io.github.aecsocket.alexandria.fabric.FabricItemType
import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.fabric.FabricAudiences
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class RegisteredSerializer<T>(
    private val registry: Registry<T>,
    private val typeName: String,
) : TypeSerializer<T> {
    override fun serialize(type: Type, obj: T?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(registry.getKey(obj))
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): T {
        val key = node.force<Key>()
        return registry.get(FabricAudiences.toNative(key))
            ?: throw SerializationException(node, type, "Invalid $typeName $key")
    }
}

inline fun <reified T> RegisteredSerializer(registry: Registry<T>) =
    RegisteredSerializer(registry, T::class.simpleName!!)

object RawItemTypeSerializer : TypeSerializer<RawItemType> {
    override fun serialize(type: Type, obj: RawItemType?, node: ConfigurationNode) {
        obj as FabricItemType?
        node.set(obj?.handle)
    }

    override fun deserialize(type: Type, node: ConfigurationNode): RawItemType {
        val handle = node.force<Item>()
        return FabricItemType(handle)
    }
}
