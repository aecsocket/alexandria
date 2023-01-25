package io.gitlab.aecsocket.alexandria.paper.datatype

import java.nio.ByteBuffer

fun floatsToBytes(obj: FloatArray): ByteArray = ByteBuffer.allocate(4 * obj.size).apply {
    obj.forEach { putFloat(it) }
}.array()

fun bytesToFloats(raw: ByteArray): FloatArray = ByteBuffer.wrap(raw).run {
    (0 until raw.size / 4).map { float }.toFloatArray()
}

fun doublesToBytes(obj: DoubleArray): ByteArray = ByteBuffer.allocate(8 * obj.size).apply {
    obj.forEach { putDouble(it) }
}.array()

fun bytesToDoubles(raw: ByteArray): DoubleArray = ByteBuffer.wrap(raw).run {
    (0 until raw.size / 8).map { double }.toDoubleArray()
}

val FloatArrayDataType = dataType<ByteArray, FloatArray>(
    { obj, _ -> floatsToBytes(obj) },
    { raw, _ -> bytesToFloats(raw) },
)

val DoubleArrayDataType = dataType<ByteArray, DoubleArray>(
    { obj, _ -> doublesToBytes(obj) },
    { raw, _ -> bytesToDoubles(raw) },
)
