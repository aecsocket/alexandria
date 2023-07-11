package io.github.aecsocket.alexandria.hook.extension

import io.github.aecsocket.alexandria.hook.AlexandriaHook
import java.util.Locale
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identity

fun Audience.locale(): Locale = get(Identity.LOCALE).orElseGet { AlexandriaHook.fallbackLocale }
