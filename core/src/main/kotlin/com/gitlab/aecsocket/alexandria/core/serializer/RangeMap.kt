package com.gitlab.aecsocket.alexandria.core.serializer

import com.gitlab.aecsocket.alexandria.core.RangeMapDouble
import com.gitlab.aecsocket.alexandria.core.RangeMapFloat
import com.gitlab.aecsocket.alexandria.core.extension.force
import com.gitlab.aecsocket.alexandria.core.extension.forceList
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val IN = "in"
private const val OUT = "out"
private const val MIN = "min"
private const val MAX = "max"

object RangeMapFloatSerializer : TypeSerializer<RangeMapFloat> {
    override fun serialize(type: Type, obj: RangeMapFloat?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            val nIn = node.node(IN)
            nIn.appendListNode().set(obj.inFrom)
            nIn.appendListNode().set(obj.inTo)

            val nOut = node.node(OUT)
            nOut.appendListNode().set(obj.outFrom)
            nOut.appendListNode().set(obj.outTo)

            if (obj.outMin > Float.NEGATIVE_INFINITY) node.node(MIN).set(obj.outMin)
            if (obj.outMax < Float.POSITIVE_INFINITY) node.node(MAX).set(obj.outMax)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): RangeMapFloat {
        val lIn = node.node(IN).forceList(type, "from", "to")
        val lOut = node.node(OUT).forceList(type, "from", "to")

        return RangeMapFloat(
            lIn[0].force(), lIn[1].force(),
            lOut[0].force(), lOut[1].force(),
            node.node(MIN).get { Float.NEGATIVE_INFINITY },
            node.node(MAX).get { Float.POSITIVE_INFINITY }
        )
    }
}

object RangeMapDoubleSerializer : TypeSerializer<RangeMapDouble> {
    override fun serialize(type: Type, obj: RangeMapDouble?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            val nIn = node.node(IN)
            nIn.appendListNode().set(obj.inFrom)
            nIn.appendListNode().set(obj.inTo)

            val nOut = node.node(OUT)
            nOut.appendListNode().set(obj.outFrom)
            nOut.appendListNode().set(obj.outTo)

            if (obj.outMin > Double.NEGATIVE_INFINITY) node.node(MIN).set(obj.outMin)
            if (obj.outMax < Double.POSITIVE_INFINITY) node.node(MAX).set(obj.outMax)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): RangeMapDouble {
        val lIn = node.node(IN).forceList(type, "from", "to")
        val lOut = node.node(OUT).forceList(type, "from", "to")

        return RangeMapDouble(
            lIn[0].force(), lIn[1].force(),
            lOut[0].force(), lOut[1].force(),
            node.node(MIN).get { Double.NEGATIVE_INFINITY },
            node.node(MAX).get { Double.POSITIVE_INFINITY }
        )
    }
}
