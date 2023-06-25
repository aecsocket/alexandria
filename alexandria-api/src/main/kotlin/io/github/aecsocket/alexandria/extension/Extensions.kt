package io.github.aecsocket.alexandria.extension

import java.io.IOException
import java.io.InputStream

/**
 * Opens a resource in the class loader of the receiver.
 * @throws RuntimeException If there was an error reading the resource.
 */
fun Any.resource(path: String): InputStream {
    val url = javaClass.classLoader.getResource(path)
        ?: throw RuntimeException("Resource at $path does not exist")
    try {
        val connection = url.openConnection()
        connection.useCaches = false
        return connection.getInputStream()
    } catch (ex: IOException) {
        throw RuntimeException("Could not load resource from $path", ex)
    }
}

/**
 * Clears this collection and returns the previous values as a list.
 */
fun <E> MutableCollection<E>.swapList(): MutableList<E> {
    val res = toMutableList()
    clear()
    return res
}

/**
 * Clears this collection and returns the previous values as a set.
 */
fun <E> MutableCollection<E>.swapSet(): MutableSet<E> {
    val res = toMutableSet()
    clear()
    return res
}
