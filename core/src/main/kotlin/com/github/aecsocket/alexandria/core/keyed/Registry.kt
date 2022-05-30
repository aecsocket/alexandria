package com.github.aecsocket.alexandria.core.keyed

interface Registry<T : Keyed> : Iterable<Map.Entry<String, T>> {
    operator fun get(id: String): T?

    companion object {
        fun <T : Keyed> create(): MutableRegistry<T> = RegistryImpl()
    }
}

interface MutableRegistry<T : Keyed> : Registry<T> {
    fun register(value: T)
}

private class RegistryImpl<T : Keyed> : MutableRegistry<T> {
    private val entries = HashMap<String, T>()

    override fun get(id: String) = entries[id]

    override fun register(value: T) {
        entries[Keyed.validate(value.id)] = value
    }

    override fun iterator() = entries.iterator()
}
