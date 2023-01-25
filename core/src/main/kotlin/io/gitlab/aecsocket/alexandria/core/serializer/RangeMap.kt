package io.gitlab.aecsocket.alexandria.core.serializer

import io.gitlab.aecsocket.alexandria.core.RangeMapDouble
import io.gitlab.aecsocket.alexandria.core.RangeMapFloat
import io.gitlab.aecsocket.alexandria.core.extension.force
import io.gitlab.aecsocket.alexandria.core.extension.forceList
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

private const val FROM = "from"
private const val TO = "to"
private const val MIN = "min"
private const val MAX = "max"
private const val RECIPROCAL = "reciprocal"

object RangeMapFloatSerializer : TypeSerializer<RangeMapFloat> {
    override fun serialize(type: Type, obj: RangeMapFloat?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            val nIn = node.node(FROM)
            nIn.appendListNode().set(obj.fromA)
            nIn.appendListNode().set(obj.fromB)

            val nOut = node.node(TO)
            nOut.appendListNode().set(obj.toA)
            nOut.appendListNode().set(obj.toB)

            if (obj.toMin > Float.NEGATIVE_INFINITY) node.node(MIN).set(obj.toMin)
            if (obj.toMax < Float.POSITIVE_INFINITY) node.node(MAX).set(obj.toMax)

            if (obj.reciprocal) node.node(RECIPROCAL).set(true)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): RangeMapFloat {
        val lIn = node.node(FROM).forceList(type, "from", "to")
        val lOut = node.node(TO).forceList(type, "from", "to")

        return RangeMapFloat(
            lIn[0].force(), lIn[1].force(),
            lOut[0].force(), lOut[1].force(),
            node.node(MIN).get { Float.NEGATIVE_INFINITY },
            node.node(MAX).get { Float.POSITIVE_INFINITY },
            node.node(RECIPROCAL).get { false }
        )
    }
}

object RangeMapDoubleSerializer : TypeSerializer<RangeMapDouble> {
    override fun serialize(type: Type, obj: RangeMapDouble?, node: ConfigurationNode) {
        if (obj == null) node.set(null)
        else {
            val nIn = node.node(FROM)
            nIn.appendListNode().set(obj.fromA)
            nIn.appendListNode().set(obj.fromB)

            val nOut = node.node(TO)
            nOut.appendListNode().set(obj.toA)
            nOut.appendListNode().set(obj.toB)

            if (obj.toMin > Double.NEGATIVE_INFINITY) node.node(MIN).set(obj.toMin)
            if (obj.toMax < Double.POSITIVE_INFINITY) node.node(MAX).set(obj.toMax)

            if (obj.reciprocal) node.node(RECIPROCAL).set(true)
        }
    }

    override fun deserialize(type: Type, node: ConfigurationNode): RangeMapDouble {
        val lIn = node.node(FROM).forceList(type, "from", "to")
        val lOut = node.node(TO).forceList(type, "from", "to")

        return RangeMapDouble(
            lIn[0].force(), lIn[1].force(),
            lOut[0].force(), lOut[1].force(),
            node.node(MIN).get { Double.NEGATIVE_INFINITY },
            node.node(MAX).get { Double.POSITIVE_INFINITY },
            node.node(RECIPROCAL).get { false }
        )
    }
}
