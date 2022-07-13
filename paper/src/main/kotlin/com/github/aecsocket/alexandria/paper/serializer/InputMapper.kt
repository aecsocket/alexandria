package com.github.aecsocket.alexandria.paper.serializer

import com.github.aecsocket.alexandria.core.extension.force
import com.github.aecsocket.alexandria.core.extension.forceList
import com.github.aecsocket.alexandria.core.extension.forceMap
import com.github.aecsocket.alexandria.paper.input.INPUT_TYPES
import com.github.aecsocket.alexandria.paper.input.InputMapper
import com.github.aecsocket.alexandria.paper.input.InputPredicate
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
