package io.gitlab.aecsocket.alexandria.core.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.Style.style
import net.kyori.adventure.text.format.TextDecoration
import kotlin.math.min

fun Throwable.simpleTrace(): List<String> {
    val className = this::class.java.simpleName
    val lines: List<String> = message?.let { message ->
        val messageLines = message.split('\n')
        if (messageLines.size == 1) listOf("$className: ${messageLines[0]}")
        else listOf("$className:") + messageLines.map { "  $it" }
    } ?: listOf(className)

    return lines + (cause?.simpleTrace() ?: emptyList())
}

data class ThrowableRenderOptions(
    val separator: Style = style(GRAY),
    val className: Style = style(YELLOW),
    val message: Style = style(GOLD),
    val declaringPackage: Style = style(GRAY),
    val declaringClass: Style = style(DARK_RED),
    val methodName: Style = style(RED),
    val lineNumber: Style = style(YELLOW),
    val packageLength: Int? = 3,
    val fileName: Style = style(GOLD),
    val unknownSource: Style = style(GRAY),
    val exDetail: Style = style(GOLD, TextDecoration.ITALIC),
    val framesInCommon: Style = style(GRAY, TextDecoration.ITALIC),
) {
    companion object {
        val DEFAULT = ThrowableRenderOptions()
    }
}

data class ThrowableRender(
    val summary: Component,
    val lines: List<Component>
)

fun framesInCommon(child: Array<StackTraceElement>, parent: Array<StackTraceElement>): Int {
    var m = child.size - 1
    (parent.indices.reversed()).forEach { i ->
        if (child[m] == parent[i]) {
            m--
        }
    }
    return child.size - 1 - m
}

fun List<StackTraceElement>.render(
    long: Boolean,
    options: ThrowableRenderOptions = ThrowableRenderOptions.DEFAULT,
    framesInCommon: Int = 0,
    margin: Component = empty(),
): List<Component> {
    return dropLast(framesInCommon).map { element ->
        text { res ->
            res.append(margin)
            val classSegments = element.className.split('.')
            val pkg = classSegments.dropLast(1)
            val className = classSegments.last()
            res.append(text(pkg.joinToString(".") { segment ->
                if (long) segment else {
                    options.packageLength?.let {
                        segment.subSequence(0, min(segment.length, it))
                    } ?: segment
                }
            } + ".", options.declaringPackage))
            res.append(text(className, options.declaringClass))
            res.append(text(".", options.separator))
            res.append(text(element.methodName, options.methodName))

            val lineNo = element.lineNumber
            if (long) {
                if (lineNo >= 0) {
                    element.fileName?.let { fileName ->
                        res.append(text("(", options.separator))
                        res.append(text(fileName, options.fileName))
                        res.append(text(":", options.separator))
                        res.append(text(lineNo, options.lineNumber))
                        res.append(text(")", options.separator))
                    } ?: run {
                        res.append(text("(Unknown Source)", options.unknownSource))
                    }
                } else {
                    res.append(text("(Native Method)", options.unknownSource))
                }
            } else {
                if (lineNo >= 0) {
                    res.append(text(" : ", options.separator))
                    res.append(text(lineNo, options.lineNumber))
                }
            }
        }
    }
}

private fun Throwable.renderInternal(
    long: Boolean,
    options: ThrowableRenderOptions = ThrowableRenderOptions.DEFAULT,
    framesInCommon: Int = 0,
): ThrowableRender {
    val summary = text { res ->
        res.append(text(this::class.qualifiedName.toString(), options.className))
        message?.let { message ->
            res.append(text(": ", options.separator))
            res.append(text(message, options.message))
        }
    }

    val margin = text("  ")

    val stackTrace = this.stackTrace
    val lines = stackTrace.toList()
        .render(long, options, framesInCommon, margin)
        .toMutableList()

    if (framesInCommon > 0) {
        lines.add(text()
            .append(margin)
            .append(text("... $framesInCommon more", options.framesInCommon))
            .build()
        )
    }

    suppressed.forEach { ex ->
        val childTrace = ex.stackTrace
        val (exSummary, exLines) = ex.renderInternal(long, options, framesInCommon(childTrace, stackTrace))
        lines.add(text()
            .append(text("Suppressed: ", options.exDetail))
            .append(exSummary)
            .build()
        )
        exLines.forEach {
            lines.add(text()
                .append(margin)
                .append(it)
                .build()
            )
        }
    }

    cause?.let { ex ->
        val childTrace = ex.stackTrace
        val (exSummary, exLines) = ex.renderInternal(long, options, framesInCommon(childTrace, stackTrace))
        lines.add(text()
            .append(text("Caused by: ", options.exDetail))
            .append(exSummary)
            .build()
        )
        lines.addAll(exLines)
    }

    return ThrowableRender(summary, lines)
}

fun Throwable.render(
    long: Boolean,
    options: ThrowableRenderOptions = ThrowableRenderOptions.DEFAULT
): List<Component> {
    val (summary, lines) = renderInternal(long, options)
    return listOf(summary) + lines
}
