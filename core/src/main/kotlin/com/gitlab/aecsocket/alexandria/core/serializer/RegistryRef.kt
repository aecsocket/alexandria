package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.keyed.Keyed
import com.gitlab.aecsocket.alexandria.core.keyed.Registry
import com.gitlab.aecsocket.alexandria.core.keyed.RegistryRef
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class RegistryRefSerializer<T : Keyed, R : RegistryRef<T>>(
    private val registry: Registry<T>,
    private val typeName: String,
    private val creator: (T) -> R,
) : TypeSerializer<R> {
    override fun serialize(type: Type, obj: R?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else node.set(obj.value.id)
    }

    override fun deserialize(type: Type, node: ConfigurationNode): R {
        val id = node.force<String>()
        val obj = registry[id]
            ?: throw SerializationException(node, type, "No $typeName with ID '$id'")
        return creator(obj)
    }
}
