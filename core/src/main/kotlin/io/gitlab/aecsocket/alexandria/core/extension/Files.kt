package io.gitlab.aecsocket.alexandria.core.extension

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

fun walkFile(
    root: Path,
    onVisit: (path: Path, attrs: BasicFileAttributes) -> FileVisitResult = { _, _ -> FileVisitResult.CONTINUE },
    onFail: (path: Path, ex: IOException) -> FileVisitResult = { _, _ -> FileVisitResult.CONTINUE }
) {
    Files.walkFileTree(root, object : SimpleFileVisitor<Path>() {
        override fun visitFile(path: Path, attrs: BasicFileAttributes) =
            onVisit(path, attrs)

        override fun visitFileFailed(path: Path, ex: IOException) =
            onFail(path, ex)
    })
}
