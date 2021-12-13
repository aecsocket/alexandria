package com.gitlab.aecsocket.minecommons.core.i18n;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A map of translation keys to lists of string messages.
 */
public final class Translation {
    private final Locale locale;
    private final Map<String, List<String>> handle;

    /**
     * Creates an instance.
     * @param locale The locale.
     * @param handle The backing map.
     */
    public Translation(Locale locale, Map<String, List<String>> handle) {
        this.locale = locale;
        this.handle = handle;
    }

    /**
     * Creates an instance with an empty backing map.
     * @param locale The locale.
     */
    public Translation(Locale locale) {
        this.locale = locale;
        handle = new HashMap<>();
    }

    /**
     * Context for creating translations.
     */
    public interface TranslationsContext {
        /**
         * Adds a translation.
         * @param key The key.
         * @param lines The message lines/
         * @return This instance.
         */
        TranslationsContext add(String key, String... lines);
    }

    /**
     * Creates a translation.
     * @param locale The locale.
     * @param translations The translations.
     * @return The translation.
     */
    public static Translation translation(Locale locale, Consumer<TranslationsContext> translations) {
        Map<String, List<String>> handle = new HashMap<>();
        translations.accept(new TranslationsContext() {
            @Override
            public TranslationsContext add(String key, String... lines) {
                handle.put(key, Arrays.asList(lines));
                return this;
            }
        });
        return new Translation(locale, handle);
    }

    /**
     * Gets the locale.
     * @return The locale.
     */
    public Locale locale() { return locale; }

    /**
     * Gets the backing map of this translation.
     * @return The backing map.
     */
    public Map<String, List<String>> handle() { return handle; }

    /**
     * Gets a translation from this map.
     * @param key The key.
     * @return The result.
     */
    public @Nullable List<String> get(String key) { return handle.get(key); }

    /**
     * Gets a translation from this map, or gets a default.
     * @param key The key.
     * @param def The default value.
     * @return The result.
     */
    public List<String> get(String key, List<String> def) { return handle.getOrDefault(key, def); }
}
