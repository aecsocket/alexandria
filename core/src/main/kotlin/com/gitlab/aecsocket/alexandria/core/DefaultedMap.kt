package com.gitlab.aecsocket.alexandria.core

interface DefaultedMap<K, out V> {
    val map: Map<K, V>
    val default: V

    operator fun get(key: K): V
}

interface MutableDefaultedMap<K, V> : DefaultedMap<K, V> {
    override val map: MutableMap<K, V>
    override var default: V
}

private class EmptyDefaultedMap<K, V>(
    override val default: V
) : DefaultedMap<K, V> {
    override val map get() = emptyMap<K, V>()

    override fun get(key: K) = default
}

private class DefaultedMapImpl<K, V>(
    override val map: MutableMap<K, V>,
    override var default: V
) : MutableDefaultedMap<K, V> {
    override fun get(key: K) = map[key] ?: default
}

fun <K, V> emptyDefaultedMap(default: V): DefaultedMap<K, V> =
    EmptyDefaultedMap(default)

fun <K, V> defaultedMapOf(map: Map<K, V>, default: V): DefaultedMap<K, V> =
    DefaultedMapImpl(map.toMutableMap(), default)

fun <K, V> mutableDefaultedMapOf(map: MutableMap<K, V>, default: V): MutableDefaultedMap<K, V> =
    DefaultedMapImpl(map, default)
