package com.gitlab.aecsocket.minecommons.core.translation;

import net.kyori.adventure.text.Component;

import java.util.Locale;

/**
 * Allows generating {@link Component}s for different keys depending on the locale, and arguments provided.
 * <p>
 * Note that how the optional arguments passed are used, are an implementation detail.
 */
public interface Localizer {
    /**
     * Gets the default locale that this instance uses.
     * @return The default locale.
     */
    Locale defaultLocale();

    /**
     * Localizes a key and arguments into a component, using a specific locale.
     * <p>
     * Uses the {@link #defaultLocale()} as a fallback.
     * <p>
     * How the arguments passed are used, is an implementation detail.
     * @param locale The locale to localize for.
     * @param key The key of the localization value.
     * @param args The arguments.
     * @return The localized component.
     */
    Component localize(Locale locale, String key, Object... args);

    /**
     * Localizes a key and arguments into a component, using the {@link #defaultLocale()}.
     * <p>
     * How the arguments passed are used, is an implementation detail.
     * @param key The key of the localization value.
     * @param args The arguments.
     * @return The localized component.
     */
    Component localize(String key, Object... args);
}
