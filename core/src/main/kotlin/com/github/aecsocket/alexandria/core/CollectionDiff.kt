package com.github.aecsocket.alexandria.core

data class CollectionDiff<T>(
    val added: Set<T>,
    val removed: Set<T>,
) {
    fun forEach(added: (T) -> Unit, removed: (T) -> Unit) {
        this.added.forEach(added)
        this.removed.forEach(removed)
    }
}

fun <T> diff(old: Collection<T>, new: Collection<T>) = CollectionDiff(
    new.toMutableSet().apply { removeAll(old.toSet()) },
    old.toMutableSet().apply { removeAll(new.toSet()) }
)
