package com.github.aecsocket.alexandria.core.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.Style.style
import net.kyori.adventure.text.format.TextDecoration
import kotlin.math.min

data class ThrowableRenderOptions(
    val className: Style = style(AQUA),
    val separator: Style = style(GRAY),
    val message: Style = style(WHITE),
    val declaringPackage: Style = style(GRAY),
    val declaringClass: Style = style(DARK_GREEN),
    val methodName: Style = style(GREEN),
    val nativeMethod: Style = style(GRAY, TextDecoration.ITALIC),
    val fileName: Style = style(GOLD),
    val lineNumber: Style = style(YELLOW),
    val unknownSource: Style = style(GRAY, TextDecoration.ITALIC),
    val packageLength: Int? = 3,
    val suppressed: Style = style(WHITE, TextDecoration.ITALIC),
    val causedBy: Style = style(WHITE, TextDecoration.ITALIC),
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

private fun Throwable.renderInternal(
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
    val lines: MutableList<Component> = stackTrace.dropLast(framesInCommon).map { element ->
        text { res ->
            res.append(margin)
            val classSegments = element.className.split('.')
            val pkg = classSegments.dropLast(1)
            val className = classSegments.last()
            res.append(text(pkg.joinToString(".") { segment ->
                options.packageLength?.let {
                    segment.subSequence(0, min(segment.length, it))
                } ?: segment
            } + ".", options.declaringPackage))
            res.append(text(className, options.declaringClass))
            res.append(text(".", options.separator))
            res.append(text(element.methodName, options.methodName))
            res.append(text(" @ ", options.separator))
            if (element.isNativeMethod) {
                res.append(text("(native)", options.nativeMethod))
            } else {
                element.fileName?.let { fileName ->
                    res.append(text(fileName, options.fileName))
                    val lineNo = element.lineNumber
                    if (lineNo >= 0) {
                        res.append(text(" : ", options.separator))
                        res.append(text(lineNo, options.lineNumber))
                    }
                } ?: run {
                    res.append(text("(unknown)", options.unknownSource))
                }
            }
        }
    }.toMutableList()

    if (framesInCommon > 0) {
        lines.add(text()
            .append(margin)
            .append(text("... $framesInCommon more", options.framesInCommon))
            .build()
        )
    }

    suppressed.forEach { ex ->
        val childTrace = ex.stackTrace
        val (exSummary, exLines) = ex.renderInternal(options, framesInCommon(childTrace, stackTrace))
        lines.add(text()
            .append(text("Suppressed: ", options.suppressed))
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
        val (exSummary, exLines) = ex.renderInternal(options, framesInCommon(childTrace, stackTrace))
        lines.add(text()
            .append(text("Caused by: ", options.causedBy))
            .append(exSummary)
            .build()
        )
        lines.addAll(exLines)
    }

    return ThrowableRender(summary, lines)
}

fun Throwable.render(
    options: ThrowableRenderOptions = ThrowableRenderOptions.DEFAULT
): List<Component> {
    val (summary, lines) = renderInternal(options)
    return listOf(summary) + lines
}
