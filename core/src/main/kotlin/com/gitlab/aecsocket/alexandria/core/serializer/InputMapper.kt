package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.forceList
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
import com.gitlab.aecsocket.alexandria.core.input.InputType
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

private const val VALUE = "value"

object InputMapperSerializer : TypeSerializer<InputMapper<*>> {
    override fun serialize(type: Type, obj: InputMapper<*>?, node: ConfigurationNode) {}

    override fun deserialize(type: Type, node: ConfigurationNode): InputMapper<*> {
        val entryType = (type as ParameterizedType).actualTypeArguments[0]
        val entrySerial = node.options().serializers()[entryType]
            ?: throw SerializationException(node, entryType, "No applicable type serializer for type")

        val values = node.childrenMap().map { (key, values) ->
            val inputType = try {
                InputType.valueOf(key.toString().uppercase())
            } catch (ex: IllegalArgumentException) {
                throw SerializationException(values, type, "Invalid input type value", ex)
            }

            inputType to values.forceList(type).map { value ->
                val filter = when (inputType) {
                    InputType.MOUSE -> value.force<InputMapper.MouseFilter>()
                    InputType.HELD_ITEM -> value.force<InputMapper.HeldItemFilter>()
                    InputType.SNEAK -> value.force<InputMapper.ToggleableFilter>()
                    InputType.SPRINT -> value.force<InputMapper.ToggleableFilter>()
                    InputType.FLIGHT -> value.force<InputMapper.ToggleableFilter>()
                    InputType.HORSE_JUMP -> value.force<InputMapper.ToggleableFilter>()
                    InputType.MENU -> value.force<InputMapper.MenuFilter>()
                    else -> InputMapper.TrueFilter
                }
                val value = entrySerial.deserialize(entryType, if (value.hasChild(VALUE)) value.node(VALUE) else value)
                InputMapper.Value(filter, value)
            }
        }.associate { it }

        return InputMapper<Any>(values)
    }
}
