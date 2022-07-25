package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.DefaultedMap
import com.gitlab.aecsocket.alexandria.core.defaultedMapOf
import com.gitlab.aecsocket.alexandria.core.extension.force
import org.spongepowered.configurate.ConfigurationNode

const val DEFAULT = "default"

fun <K, V> serializeDefaultedMap(obj: DefaultedMap<K, V>, node: ConfigurationNode) {
    node.set(obj.map)
    node.node(DEFAULT).set(obj.default)
}

inline fun <K, reified V : Any> deserializeDefaultedMap(node: ConfigurationNode): DefaultedMap<K, V> {
    return defaultedMapOf(
        node.copy().apply { removeChild(DEFAULT) }.force(),
        node.node(DEFAULT).force(),
    )
}
