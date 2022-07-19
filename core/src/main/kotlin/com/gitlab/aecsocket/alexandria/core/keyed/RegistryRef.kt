package com.gitlab.aecsocket.alexandria.core.keyed

import kotlin.reflect.KProperty

open class RegistryRef<T : Keyed>(val value: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
}
