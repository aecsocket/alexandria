package com.gitlab.aecsocket.alexandria.paper.datatype

import com.gitlab.aecsocket.alexandria.core.physics.*
import com.gitlab.aecsocket.alexandria.paper.extension.dataType
import com.gitlab.aecsocket.alexandria.paper.extension.force
import com.gitlab.aecsocket.alexandria.paper.extension.key
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.util.*

private const val NAME = "name"
private const val VALUE = "value"
private const val SIGNATURE = "signature"

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

fun texturePropertyDataType(plugin: Plugin): PersistentDataType<PersistentDataContainer, TextureProperty> {
    val name = plugin.key(NAME)
    val value = plugin.key(VALUE)
    val signature = plugin.key(SIGNATURE)
    return dataType(
        { obj, ctx -> ctx.newPersistentDataContainer().apply {
            set(name, PersistentDataType.STRING, obj.name)
            set(value, PersistentDataType.STRING, obj.value)
            obj.signature?.let { set(signature, PersistentDataType.STRING, it) }
        } },
        { pdc, _ -> TextureProperty(
            pdc.force(name, PersistentDataType.STRING),
            pdc.force(value, PersistentDataType.STRING),
            pdc.get(signature, PersistentDataType.STRING),
        ) }
    )
}
