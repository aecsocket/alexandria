package com.github.aecsocket.alexandria.core

import java.io.File

fun File.walkPathed(
    onEnter: (File, String, List<String>) -> Boolean = { _, _, _ -> true }
) {
    fun walk(file: File, name: String, path: List<String>) {
        if (!onEnter(file, name, path))
            return
        if (file.isDirectory) {
            list()?.forEach { name ->
                walk(resolve(name), name, path + name)
            }
        }
    }

    walk(this, name, emptyList())
}
