package com.gitlab.aecsocket.alexandria.core

import org.junit.jupiter.api.Test

class TablesTest {
    @Test
    fun test() {
        val rows = listOf(
            TableRow(listOf(listOf("Top Left"), listOf("Top Middle", "This is a bunch of text that goes in the middle."), listOf("Top Right", "Extra", "Extra 2"))),
            TableRow(listOf(listOf("Middle Left"), listOf("Middle Middle", "This is a bunch of text that goes in the middle.", "Line 3"), listOf("Middle Right"))),
            TableRow(listOf(listOf("Bottom Left"), listOf("Bottom Middle", "This is a bunch of text that goes in the middle.", "Line 3"), listOf("Bottom Right"))),
        )

        val (lines) = tableOfMonoStrings(rows,
            colAlign = { when (it) {
                0 -> TableAlign.END
                1 -> TableAlign.CENTER
                else -> TableAlign.START
            } },
            rowAlign = { when (it) {
                else -> TableAlign.CENTER
            } },
            colSeparator = { " | " },
            rowSeparator = { (_, colWidths) -> listOf(colWidths.joinToString(" + ") { "-".repeat(it) }) },
        )

        lines.forEach {
            println("> $it")
        }
    }
}
