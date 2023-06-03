package io.github.aecsocket.alexandria.hook.extension

import io.github.aecsocket.alexandria.hook.AlexandriaHook
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identity
import java.util.Locale

fun Audience.locale(): Locale = get(Identity.LOCALE).orElseGet { AlexandriaHook.FallbackLocale }
