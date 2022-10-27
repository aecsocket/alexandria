package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.forceList
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
import com.gitlab.aecsocket.alexandria.core.input.InputType
import io.leangen.geantyref.TypeToken
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val ON = "on"
private const val VALUE = "value"

class InputMapperSerializer<V>(private val valueType: TypeToken<V>) : TypeSerializer<InputMapper<V>> {
    override fun serialize(type: Type, obj: InputMapper<V>?, node: ConfigurationNode) {}

    override fun deserialize(type: Type, node: ConfigurationNode): InputMapper<V> {
        val builder = InputMapper.builder<V>()
        node.forceList(type).forEach { child ->
            val on = node.node(ON).forceList(type)
            if (on.size < 1)
                throw SerializationException(child, type, "Trigger 'on' must contain first element as input type, and rest as tags")
            val inputType = on[0].force<InputType>()
            val tags = on.drop(1).map { it.force<String>() }
            val value = node.node(VALUE).force(valueType)

            builder.trigger(inputType, tags, value)
        }
        return builder.build()
    }
}
