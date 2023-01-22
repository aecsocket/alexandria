package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.glossa.core.I18N
import com.gitlab.aecsocket.glossa.core.I18NArgs
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import java.util.*

class ReplacingI18N(
    private val backing: I18N<Component>,
    private val replacements: List<TextReplacementConfig>
) : I18N<Component> {
    override val empty get() = backing.empty
    override val newline get() = backing.newline

    override fun Iterable<Component>.join(separator: Component) = backing.run { join(separator) }

    private fun replace(lines: List<Component>) = lines.map { line ->
        var current = line
        replacements.forEach { current = current.replaceText(it) }
        current
    }

    override fun make(key: String, args: I18NArgs<Component>): List<Component>? {
        return backing.make(key, args)?.let { replace(it) }
    }

    override fun safe(key: String, args: I18NArgs<Component>): List<Component> {
        return replace(backing.safe(key, args))
    }

    override fun withLocale(locale: Locale) = ReplacingI18N(backing.withLocale(locale), replacements)
}
