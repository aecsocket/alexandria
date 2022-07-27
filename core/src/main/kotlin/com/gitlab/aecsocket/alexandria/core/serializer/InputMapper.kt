package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.forceList
import com.gitlab.aecsocket.alexandria.core.extension.forceMap
import com.gitlab.aecsocket.alexandria.core.input.INPUT_TYPES
import com.gitlab.aecsocket.alexandria.core.input.InputMapper
import com.gitlab.aecsocket.alexandria.core.input.InputPredicate
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

object InputMapperSerializer : TypeSerializer<InputMapper> {
    override fun serialize(type: Type, obj: InputMapper?, node: ConfigurationNode) {}

    override fun deserialize(type: Type, node: ConfigurationNode): InputMapper {
        node.forceMap(type)

        return InputMapper(node.childrenMap().map { (inputType, child) ->
            if (!INPUT_TYPES.contains(inputType))
                throw SerializationException(child, type, "Invalid input type '$inputType'")
            inputType.toString() to child.forceList(type).map { predicateNode ->
                predicateNode.forceList(type, "tags", "action").run { InputPredicate(
                    get(0).force<HashSet<String>>(),
                    get(1).force(),
                ) }
            }
        }.associate { it })
    }
}
