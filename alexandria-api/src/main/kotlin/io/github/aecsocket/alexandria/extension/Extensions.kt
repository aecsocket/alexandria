package io.github.aecsocket.alexandria.extension

import java.io.IOException
import java.io.InputStream

/**
 * Opens a resource in the class loader of the receiver.
 *
 * @throws RuntimeException if there was an error reading the resource.
 */
fun Any.resource(path: String): InputStream {
  val url =
      javaClass.classLoader.getResource(path)
          ?: throw RuntimeException("Resource at $path does not exist")
  try {
    val connection = url.openConnection()
    connection.useCaches = false
    return connection.getInputStream()
  } catch (ex: IOException) {
    throw RuntimeException("Could not load resource from $path", ex)
  }
}
