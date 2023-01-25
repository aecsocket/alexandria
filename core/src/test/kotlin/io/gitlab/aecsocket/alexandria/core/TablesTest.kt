package io.gitlab.aecsocket.alexandria.core

import org.junit.jupiter.api.Test

class TablesTest {
    @Test
    fun test() {
        val rows = listOf(
            tableRowOf(listOf("Top Left", "This is some text that goes to the right."), listOf("Top Middle", "This is a bunch of text that goes in the middle."), listOf("Top Right", "Extra", "Extra 2")),
            tableRowOf(listOf("Middle Left"), listOf("Middle Middle", "This is a bunch of text that goes in the middle.", "Line 3"), listOf("Middle Right")),
            tableRowOf(listOf("Bottom Left"), listOf("Bottom Middle", "This is a bunch of text that goes in the middle.", "Line 3"), listOf("Bottom Right")),
        )

        val sections = MonoStringTableRenderer(
            align = { when (it) {
                0 -> TableAlign.END
                1 -> TableAlign.CENTER
                else -> TableAlign.START
            } },
            justify = { TableAlign.CENTER },
            colSeparator = " | ",
            rowSeparator = { widths -> listOf(widths.joinToString("-+-") { "-".repeat(it) }) }
        ).render(rows)

        sections.forEach {
            println("> $it")
        }
    }
}
