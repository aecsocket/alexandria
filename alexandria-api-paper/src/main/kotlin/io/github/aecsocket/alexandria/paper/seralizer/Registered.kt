package io.github.aecsocket.alexandria.paper.seralizer

import io.github.aecsocket.alexandria.core.extension.force
import io.github.aecsocket.alexandria.paper.extension.toNamespaced
import net.kyori.adventure.key.Key
import org.bukkit.Keyed
import org.bukkit.Registry
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

open class RegisteredSerializer<T : Keyed>(
    private val registry: Registry<T>,
    private val typeName: String
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

val materialSerializer = RegisteredSerializer(Registry.MATERIAL, "material")
val entityTypeSerializer = RegisteredSerializer(Registry.ENTITY_TYPE, "entity type")
val statisticSerializer = RegisteredSerializer(Registry.STATISTIC, "statistic")
