package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.forceList
import com.gitlab.aecsocket.alexandria.core.extension.typeToken
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
import com.gitlab.aecsocket.alexandria.core.input.InputType
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

private const val ON = "on"
private const val VALUE = "value"

object InputMapperSerializer : TypeSerializer<InputMapper<*>> {
    override fun serialize(type: Type, obj: InputMapper<*>?, node: ConfigurationNode) {}

    override fun deserialize(type: Type, node: ConfigurationNode): InputMapper<*> {
        val entryType = (type as ParameterizedType).actualTypeArguments[0]
        val entrySerial = node.options().serializers()[entryType]
            ?: throw SerializationException(node, entryType, "No applicable type serializer for type")

        val builder = InputMapper.builder<Any>()
        node.forceList(type).forEach { child ->
            val on = child.node(ON).forceList(type)
            if (on.size < 1)
                throw SerializationException(child, type, "Trigger 'on' must contain first element as input type, and rest as tags")
            val inputType = on[0].force<InputType>()
            val tags = on.drop(1).map { it.force<String>() }

            val value = entrySerial.deserialize(entryType, child.node(VALUE))

            builder.trigger(inputType, tags, value)
        }

        return builder.build()
    }
}
