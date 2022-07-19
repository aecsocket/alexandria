package com.gitlab.aecsocket.alexandria.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty

enum class ColumnAlign {
    LEFT,
    RIGHT,
}

fun <T> tableOf(
    rows: Iterable<Iterable<T>>,
    separator: T,
    alignCol: (Int) -> ColumnAlign,
    widthOf: (T) -> Int,
    paddingOf: (Int) -> T,
    append: (T, T) -> T,
    makeEmpty: () -> T,
): List<T> {
    val colSizes = HashMap<Int, Int>()
    rows.forEach { row ->
        row.forEachIndexed { colIdx, col ->
            val size = widthOf(col)
            if (colSizes[colIdx]?.let { size > it } != false) {
                colSizes[colIdx] = size
            }
        }
    }

    return rows.map { row ->
        var rowRes = makeEmpty()
        row.forEachIndexed { colIdx, content ->
            val colSize = colSizes[colIdx] ?: throw IllegalStateException("Column $colIdx - sizes = $colSizes")
            val padding = paddingOf(colSize - widthOf(content))
            rowRes = append(rowRes, append(when (alignCol(colIdx)) {
                ColumnAlign.LEFT -> append(content, padding)
                ColumnAlign.RIGHT -> append(padding, content)
            }, separator))
        }
        rowRes
    }
}

fun tableOfComponents(
    rows: Iterable<Iterable<Component>>,
    separator: Component,
    alignCol: (Int) -> ColumnAlign,
    widthOf: (Component) -> Int,
    paddingOf: (Int) -> Component,
) = tableOf(rows, separator, alignCol, widthOf, paddingOf, { a, b -> a.append(b) }, { empty() })
