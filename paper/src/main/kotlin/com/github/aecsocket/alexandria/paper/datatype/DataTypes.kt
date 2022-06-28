package com.github.aecsocket.alexandria.paper.datatype

import com.github.aecsocket.alexandria.core.vector.Polar2
import com.github.aecsocket.alexandria.core.vector.Polar3
import com.github.aecsocket.alexandria.core.vector.Vector2
import com.github.aecsocket.alexandria.core.vector.Vector3
import com.github.aecsocket.alexandria.paper.extension.dataType
import com.github.aecsocket.alexandria.paper.extension.force
import com.github.aecsocket.alexandria.paper.extension.key
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

val Polar2DataType = dataType<ByteArray, Polar2>(
    { obj, _ -> doublesToBytes(doubleArrayOf(obj.radius, obj.angle)) },
    { raw, _ -> bytesToDoubles(raw).run { Polar2(get(0), get(1)) } }
)

val Polar3DataType = dataType<ByteArray, Polar3>(
    { obj, _ -> doublesToBytes(doubleArrayOf(obj.radius, obj.yaw, obj.pitch)) },
    { raw, _ -> bytesToDoubles(raw).run { Polar3(get(0), get(1), get(2)) } }
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
