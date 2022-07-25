package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.Quantifier
import com.gitlab.aecsocket.alexandria.core.extension.forceList
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object QuantifierSerializer : TypeSerializer<Quantifier<*>> {
    override fun serialize(type: Type, obj: Quantifier<*>?, node: ConfigurationNode) {
        if (type !is ParameterizedType)
            throw SerializationException(type, "Raw types are not supported for quantifiers")
        if (type.actualTypeArguments.size != 1)
            throw SerializationException(type, "Quantifier expected one type argument")

        val valueType = type.actualTypeArguments[0]
        val valueSerial = node.options().serializers()[valueType]
            ?: throw SerializationException(type, "No type serializer available for value type $valueType")

        if (obj == null) node.set(null)
        else {
            valueSerial.serialize(valueType, obj.obj as Nothing?, node.appendListNode())
            node.appendListNode().set(obj.amount)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): Quantifier<*> {
        if (type !is ParameterizedType)
            throw SerializationException(type, "Raw types are not supported for quantifiers")
        if (type.actualTypeArguments.size != 1)
            throw SerializationException(type, "Quantifier expected one type argument")

        val valueType = type.actualTypeArguments[0]
        val valueSerial = node.options().serializers()[valueType]
            ?: throw SerializationException(type, "No type serializer available for value type $valueType")

        node.forceList(type, "value", "amount")

        return Quantifier(
            valueSerial.deserialize(valueType, node.node(0)),
            node.node(1).get { 1 },
        )
    }
}
