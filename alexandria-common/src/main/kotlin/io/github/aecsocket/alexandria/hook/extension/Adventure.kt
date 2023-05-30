package io.github.aecsocket.alexandria.hook.extension

import io.github.aecsocket.alexandria.hook.fallbackLocale
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identity

fun Audience.locale() = get(Identity.LOCALE).orElseGet { fallbackLocale }
