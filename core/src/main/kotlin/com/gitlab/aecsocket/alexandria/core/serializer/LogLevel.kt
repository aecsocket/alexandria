package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.LogLevel
import com.gitlab.aecsocket.glossa.core.force
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

object LogLevelSerializer : TypeSerializer<LogLevel> {
    override fun serialize(type: Type, obj: LogLevel?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            node.set(obj.name)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): LogLevel {
        val name = node.force<String>()
        return try {
            LogLevel.valueOf(name)
        } catch (ex: IllegalArgumentException) {
            throw SerializationException(node, type, "Invalid log level '$name'")
        }
    }
}
