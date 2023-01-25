package io.gitlab.aecsocket.alexandria.paper.datatype

import io.gitlab.aecsocket.alexandria.core.physics.*
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import java.util.*

inline fun <reified T : Any, reified Z : Any> dataType(
    crossinline serialize: (Z, PersistentDataAdapterContext) -> T,
    crossinline deserialize: (T, PersistentDataAdapterContext) -> Z,
) = object : PersistentDataType<T, Z> {
    override fun getPrimitiveType() = T::class.java
    override fun getComplexType() = Z::class.java

    override fun toPrimitive(
        obj: Z,
        ctx: PersistentDataAdapterContext
    ) = serialize(obj, ctx)

    override fun fromPrimitive(
        raw: T,
        ctx: PersistentDataAdapterContext
    ) = deserialize(raw, ctx)
}

val UUIDDataType = dataType<LongArray, UUID>(
    { obj, _ -> longArrayOf(obj.mostSignificantBits, obj.leastSignificantBits) },
    { raw, _ -> UUID(raw[0], raw[1]) }
)

val Vector2DataType = dataType<ByteArray, Vector2>(
    { obj, _ -> doublesToBytes(doubleArrayOf(obj.x, obj.y)) },
    { raw, _ -> bytesToDoubles(raw).run { Vector2(get(0), get(1)) } }
)

val Vector3DataType = dataType<ByteArray, Vector3>(
    { obj, _ -> doublesToBytes(doubleArrayOf(obj.x, obj.y, obj.z)) },
    { raw, _ -> bytesToDoubles(raw).run { Vector3(get(0), get(1), get(2)) } }
)

val Point2DataType = dataType<IntArray, Point2>(
    { obj, _ -> intArrayOf(obj.x, obj.y) },
    { raw, _ -> raw.run { Point2(get(0), get(1)) } }
)

val Point3DataType = dataType<IntArray, Point3>(
    { obj, _ -> intArrayOf(obj.x, obj.y, obj.z) },
    { raw, _ -> raw.run { Point3(get(0), get(1), get(2)) } }
)

val QuaternionDataType = dataType<ByteArray, Quaternion>(
    { obj, _ -> doublesToBytes(doubleArrayOf(obj.x, obj.y, obj.z, obj.w)) },
    { raw, _ -> bytesToDoubles(raw).run { Quaternion(get(0), get(1), get(2), get(3)) } }
)
