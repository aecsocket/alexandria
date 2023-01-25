package io.gitlab.aecsocket.alexandria.core.serializer

import io.gitlab.aecsocket.alexandria.core.extension.force
import io.gitlab.aecsocket.alexandria.core.extension.forceList
import io.gitlab.aecsocket.alexandria.core.input.InputMapper
import io.gitlab.aecsocket.alexandria.core.input.InputType
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

            inputType to values.forceList(type).map { valueNode ->
                val filter = when (inputType) {
                    InputType.MOUSE -> valueNode.force<InputMapper.MouseFilter>()
                    InputType.HELD_ITEM -> valueNode.force<InputMapper.HeldItemFilter>()
                    InputType.SNEAK -> valueNode.force<InputMapper.ToggleableFilter>()
                    InputType.SPRINT -> valueNode.force<InputMapper.ToggleableFilter>()
                    InputType.FLIGHT -> valueNode.force<InputMapper.ToggleableFilter>()
                    InputType.HORSE_JUMP -> valueNode.force<InputMapper.ToggleableFilter>()
                    InputType.MENU -> valueNode.force<InputMapper.MenuFilter>()
                    else -> InputMapper.TrueFilter
                }
                val value = entrySerial.deserialize(entryType, if (valueNode.hasChild(VALUE)) valueNode.node(VALUE) else valueNode)
                InputMapper.Value(filter, value)
            }
        }.associate { it }

        return InputMapper<Any>(values)
    }
}
