package com.gitlab.aecsocket.alexandria.core

import com.gitlab.aecsocket.glossa.core.I18N
import com.gitlab.aecsocket.glossa.core.I18NArgs
import java.util.*

class SuppliedKeyI18N<T>(
    private val backing: I18N<T>,
    private val keyPrefix: String
) : I18N<T> {
    override val empty get() = backing.empty
    override val newline get() = backing.newline

    override fun Iterable<T>.join(separator: T) = backing.run { join(separator) }

    override fun make(key: String, args: I18NArgs<T>): List<T>? {
        return backing.make("$keyPrefix.$key", args)
    }

    override fun safe(key: String, args: I18NArgs<T>): List<T> {
        return backing.safe("$keyPrefix.$key", args)
    }

    override fun withLocale(locale: Locale) = SuppliedKeyI18N(backing.withLocale(locale), keyPrefix)
}
