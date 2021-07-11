package com.gitlab.aecsocket.minecommons.core.translation;

import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.Optional;

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
     * @return An Optional of the localized component.
     */
    Optional<Component> get(Locale locale, String key, Object... args);

    /**
     * Localizes a key and arguments into a component, using a specific locale.
     * <p>
     * Uses the {@link #defaultLocale()} as a fallback.
     * <p>
     * How the arguments passed are used, is an implementation detail.
     * <p>
     * If a translation was not found, will throw an exception.
     * @param locale The locale to localize for.
     * @param key The key of the localization value.
     * @param args The arguments.
     * @return The localized component.
     */
    default Component req(Locale locale, String key, Object... args) {
        return get(locale, key, args)
                .orElseThrow(() -> new IllegalArgumentException("Could not get translation for key [" + key + "]"));
    }

    /**
     * Localizes a key and arguments into a component, using a specific locale.
     * <p>
     * Uses the {@link #defaultLocale()} as a fallback.
     * <p>
     * How the arguments passed are used, is an implementation detail.
     * <p>
     * If a translation was not found, will return the key passed.
     * @param locale The locale to localize for.
     * @param key The key of the localization value.
     * @param args The arguments.
     * @return The localized component.
     */
    default Component safe(Locale locale, String key, Object... args) {
        return get(locale, key, args)
                .orElse(Component.text(key));
    }
}
