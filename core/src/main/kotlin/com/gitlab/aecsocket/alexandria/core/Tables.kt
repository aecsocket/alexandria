package com.gitlab.aecsocket.alexandria.core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text

enum class TableAlign {
    START,
    END,
    CENTER,
}

typealias TableCell<T> = Collection<T>

data class TableRow<T>(
    val cells: Iterable<TableCell<T>>,
    val mapper: (T) -> T = { it },
)

data class TableColumnSeparatorData(val colIdx: Int, val lineIdx: Int)

data class TableRowSeparatorData(val rowIdx: Int, val colWidths: List<Int>)

data class TableData<T>(val lines: List<T>, val rowHeights: List<Int>, val colWidths: List<Int>)

fun <T> tableOf(
    rows: Iterable<TableRow<T>>,
    colAlign: (Int) -> TableAlign,
    rowAlign: (Int) -> TableAlign,
    colSeparator: (TableColumnSeparatorData) -> T,
    rowSeparator: (TableRowSeparatorData) -> Iterable<T>,

    widthOf: (T) -> Int,
    paddingOf: (Int) -> T,
    append2: (T, T) -> T,
    append3: (T, T, T) -> T,
    makeEmpty: () -> T,
): TableData<T> {
    val rowHeights = HashMap<Int, Int>()
    val colWidths = HashMap<Int, Int>()

    rows.forEachIndexed { rowIdx, row ->
        var height = 0
        row.cells.forEachIndexed { colIdx, cell ->
            // get the widest T in this cell
            // if it's wider than our current widest for this column, update this
            cell.maxOfOrNull(widthOf)?.let { width ->
                if (colWidths[colIdx]?.let { width > it } != false) {
                    colWidths[colIdx] = width
                }
            }

            // if this cell's height (no. of lines) is taller than our current highest,
            // make this the highest cell
            if (cell.size > height) {
                height = cell.size
            }
        }
        rowHeights[rowIdx] = height
    }

    val cRowHeights = rowHeights.map { (_, v) -> v }
    val cColWidths = colWidths.map { (_, v) -> v }

    val lines = ArrayList<T>()
    val rowIter = rows.iterator()
    var rowIdx = 0
    while (rowIter.hasNext()) {
        val row = rowIter.next()

        val height = cRowHeights[rowIdx]
        val justify = rowAlign(rowIdx)
        val rowLines = MutableList(height) { makeEmpty() }

        val colIter = row.cells.iterator()
        var colIdx = 0
        while (colIter.hasNext()) {
            val cell = colIter.next()
            val width = cColWidths[colIdx]

            val align = colAlign(colIdx)
            val aligned = cell.map { line ->
                val alignWidth = width - widthOf(line)
                when (align) {
                    TableAlign.START -> append2(line, paddingOf(alignWidth))
                    TableAlign.END -> append2(paddingOf(alignWidth), line)
                    TableAlign.CENTER -> {
                        val halfWidth = alignWidth / 2
                        append3(paddingOf(halfWidth), line, paddingOf(alignWidth - halfWidth))
                    }
                }
            }

            val alignPad = paddingOf(width)
            val justifyHeight = height - aligned.size
            val justified = when (justify) {
                TableAlign.START -> aligned + List(justifyHeight) { alignPad }
                TableAlign.END -> List(justifyHeight) { alignPad } + aligned
                TableAlign.CENTER -> {
                    val halfHeight = justifyHeight / 2
                    List(halfHeight) { alignPad } + aligned + List(justifyHeight - halfHeight) { alignPad }
                }
            }

            justified.forEachIndexed { lineIdx, line ->
                rowLines[lineIdx] = if (colIter.hasNext())
                    append3(rowLines[lineIdx], line, colSeparator(TableColumnSeparatorData(colIdx, lineIdx)))
                else append2(rowLines[lineIdx], line)
            }

            colIdx++
        }

        val toAdd = rowLines +
            if (rowIter.hasNext()) rowSeparator(TableRowSeparatorData(rowIdx, cColWidths))
            else emptyList()

        toAdd.forEach { line ->
            lines.add(row.mapper(line))
        }

        rowIdx++
    }

    return TableData(lines, cRowHeights, cColWidths)
}

data class TableFormat(
    val align: (Int) -> TableAlign,
    val justify: (Int) -> TableAlign,
)

fun tableOfStrings(
    rows: Iterable<TableRow<String>>,
    colAlign: (Int) -> TableAlign,
    rowAlign: (Int) -> TableAlign,
    colSeparator: (TableColumnSeparatorData) -> String,
    rowSeparator: (TableRowSeparatorData) -> Iterable<String>,

    widthOf: (String) -> Int,
    paddingOf: (Int) -> String,
): TableData<String> = tableOf(
    rows, colAlign, rowAlign, colSeparator, rowSeparator, widthOf, paddingOf,
    { a, b -> a + b }, { a, b, c -> a + b + c }, { "" }
)

fun tableOfMonoStrings(
    rows: Iterable<TableRow<String>>,
    colAlign: (Int) -> TableAlign,
    rowAlign: (Int) -> TableAlign,
    colSeparator: (TableColumnSeparatorData) -> String,
    rowSeparator: (TableRowSeparatorData) -> Iterable<String>,
): TableData<String> = tableOfStrings(
    rows, colAlign, rowAlign, colSeparator, rowSeparator, { it.length }, { " ".repeat(it) },
)

fun tableOfComponents(
    rows: Iterable<TableRow<Component>>,
    colAlign: (Int) -> TableAlign,
    rowAlign: (Int) -> TableAlign,
    colSeparator: (TableColumnSeparatorData) -> Component,
    rowSeparator: (TableRowSeparatorData) -> Iterable<Component>,

    widthOf: (Component) -> Int,
    paddingOf: (Int) -> Component,
): TableData<Component> = tableOf(
    rows, colAlign, rowAlign, colSeparator, rowSeparator, widthOf, paddingOf,
    { a, b -> text().append(a).append(b).build() },
    { a, b, c -> text().append(a).append(b).append(c).build() },
    { empty() },
)
