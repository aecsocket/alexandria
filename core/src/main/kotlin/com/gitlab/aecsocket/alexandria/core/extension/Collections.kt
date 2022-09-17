package com.gitlab.aecsocket.alexandria.core.extension

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
fun <T, R> Iterable<T>.join(mapper: (T) -> Iterable<R>, separator: (T) -> Iterable<R>): List<R> {
    val res = ArrayList<R>()

    val iter = iterator()
    while (iter.hasNext()) {
        val cur = iter.next()
        res.addAll(mapper(cur))
        if (iter.hasNext()) {
            res.addAll(separator(cur))
        }
    }

    return res
}

fun <T> Iterable<Iterable<T>>.join(separator: Iterable<T>): List<T> {
    return join({ it }, { separator })
}

private class MappingIterator<T, R>(val backing: Iterator<T>, val mapper: (T) -> R) : Iterator<R> {
    override fun hasNext() = backing.hasNext()

    override fun next(): R {
        return mapper(backing.next())
    }
}

fun <T, R> Iterator<T>.mapping(mapper: (T) -> R): Iterator<R> {
    return MappingIterator(this, mapper)
}
