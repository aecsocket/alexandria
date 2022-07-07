package com.github.aecsocket.alexandria.core.keyed

interface Registry<T : Keyed> : Iterable<Map.Entry<String, T>> {
    val entries: Map<String, T>

    val size: Int

    operator fun get(id: String): T?

    companion object {
        fun <T : Keyed> create(): MutableRegistry<T> = RegistryImpl()
    }
}

interface MutableRegistry<T : Keyed> : Registry<T> {
    fun register(value: T)

    fun unregister(key: String)

    fun clear()
}

private class RegistryImpl<T : Keyed> : MutableRegistry<T> {
    private val _entries = HashMap<String, T>()
    override val entries: Map<String, T> get() = _entries

    override val size get() = _entries.size

    override fun get(id: String) = _entries[id]

    override fun register(value: T) {
        _entries[Keyed.validate(value.id)] = value
    }

    override fun unregister(key: String) {
        _entries.remove(key)
    }

    override fun clear() {
        _entries.clear()
    }

    override fun iterator() = _entries.iterator()
}
