package com.gitlab.aecsocket.alexandria.core

import net.kyori.adventure.extra.kotlin.join
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import kotlin.math.max

enum class TableAlign {
    START,
    END,
    CENTER,
}

typealias TableCell<T> = Collection<T>

typealias TableRow<T> = Iterable<TableCell<T>>

data class TableDimensions(val rowHeights: List<Int>, val colWidths: List<Int>)

fun <T> tableDimensionsOf(
    rows: Iterable<TableRow<T>>,
    widthOf: (T) -> Int,
): TableDimensions {
    val colWidths = HashMap<Int, Int>()
    val rowHeights = rows.map { row ->
        var height = 0
        row.forEachIndexed { colIdx, cell ->
            // calculate tallest cell
            if (cell.size > height) {
                height = cell.size
            }

            // if this cell is wider than its column's current widest,
            // update this to be the widest
            cell.maxOfOrNull(widthOf)?.let { width ->
                colWidths.compute(colIdx) { _, value -> value?.let { max(it, width) } ?: width }
            }
        }
        height
    }
    return TableDimensions(rowHeights, colWidths.map { (_, width) -> width })
}

interface TableRenderer<T> {
    fun render(
        rows: Iterable<TableRow<T>>,
        dimensions: TableDimensions,
    ): List<T>

    fun render(rows: Iterable<TableRow<T>>): List<T>
}

abstract class AbstractTableRenderer<T>(
    var align: (Int) -> TableAlign = { TableAlign.START },
    var justify: (Int) -> TableAlign = { TableAlign.START },
    var colSeparator: T,
    var rowSeparator: (List<Int>) -> Iterable<T>,
) : TableRenderer<T> {

    protected abstract fun widthOf(value: T): Int

    protected abstract fun paddingOf(width: Int): T

    override fun render(rows: Iterable<TableRow<T>>) =
        render(rows, tableDimensionsOf(rows, this::widthOf))

    protected abstract fun join(values: Iterable<T>): T

    private fun join(vararg values: T): T = join(values.asIterable())

    override fun render(rows: Iterable<TableRow<T>>, dimensions: TableDimensions): List<T> {
        val (rowHeights, colWidths) = dimensions
        val lines = ArrayList<T>()

        val rowIter = rows.iterator()
        var rowIdx = 0
        while (rowIter.hasNext()) {
            val row = rowIter.next()
            val maxHeight = rowHeights[rowIdx]
            val justify = justify(rowIdx)

            val rowLines = List(maxHeight) { ArrayList<T>() }

            val colIter = row.iterator()
            var colIdx = 0
            while (colIter.hasNext()) {
                val cell = colIter.next()
                val maxWidth = colWidths[colIdx]
                val align = align(colIdx)

                val aligned = cell.map {
                    val padWidth = maxWidth - widthOf(it)
                    when (align) {
                        TableAlign.START -> join(it, paddingOf(padWidth))
                        TableAlign.END -> join(paddingOf(padWidth), it)
                        TableAlign.CENTER -> {
                            val halfPad = padWidth / 2
                            join(paddingOf(halfPad), it, paddingOf(padWidth - halfPad))
                        }
                    }
                }

                val padValue = paddingOf(maxWidth)
                val padHeight = maxHeight - aligned.size
                val justified = when (justify) {
                    TableAlign.START -> aligned + List(padHeight) { padValue }
                    TableAlign.END -> List(padHeight) { padValue } + aligned
                    TableAlign.CENTER -> {
                        val halfPad = padHeight / 2
                        List(halfPad) { padValue } + aligned + List(padHeight - halfPad) { padValue }
                    }
                }

                justified.forEachIndexed { lineIdx, line ->
                    rowLines[lineIdx].add(line)
                    if (colIter.hasNext()) {
                        rowLines[lineIdx].add(colSeparator)
                    }
                }

                colIdx++
            }

            lines.addAll(rowLines.map { join(it) })
            if (rowIter.hasNext()) {
                lines.addAll(rowSeparator(colWidths))
            }

            rowIdx++
        }

        return lines
    }
}

abstract class StringTableRenderer(
    align: (Int) -> TableAlign = { TableAlign.START },
    justify: (Int) -> TableAlign = { TableAlign.START },
    colSeparator: String = "",
    rowSeparator: (List<Int>) -> Iterable<String> = { emptySet() },
) : AbstractTableRenderer<String>(align, justify, colSeparator, rowSeparator) {
    override fun join(values: Iterable<String>) =
        values.joinToString("")
}

class MonoStringTableRenderer(
    align: (Int) -> TableAlign = { TableAlign.START },
    justify: (Int) -> TableAlign = { TableAlign.START },
    colSeparator: String = "",
    rowSeparator: (List<Int>) -> Iterable<String> = { emptySet() },
) : StringTableRenderer(align, justify, colSeparator, rowSeparator) {
    override fun widthOf(value: String) =
        value.length

    override fun paddingOf(width: Int) =
        " ".repeat(width)
}

abstract class ComponentTableRenderer(
    align: (Int) -> TableAlign = { TableAlign.START },
    justify: (Int) -> TableAlign = { TableAlign.START },
    colSeparator: Component = empty(),
    rowSeparator: (List<Int>) -> Iterable<Component> = { emptySet() },
) : AbstractTableRenderer<Component>(align, justify, colSeparator, rowSeparator) {
    override fun join(values: Iterable<Component>) =
        values.join()
}
