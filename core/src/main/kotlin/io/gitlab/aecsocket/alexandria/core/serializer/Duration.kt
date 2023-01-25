package io.gitlab.aecsocket.alexandria.core.serializer

import io.gitlab.aecsocket.alexandria.core.extension.force
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type
import kotlin.time.Duration

data class WDuration(val value: Duration)

fun Duration.wrap() = WDuration(this)

object DurationSerializer : TypeSerializer<WDuration> {
    override fun serialize(type: Type, obj: WDuration?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(obj.value.toIsoString())
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode) = try {
        Duration.parse(node.force()).wrap()
    } catch (ex: IllegalArgumentException) {
        throw SerializationException(node, type, "Invalid duration format", ex)
    }
}
