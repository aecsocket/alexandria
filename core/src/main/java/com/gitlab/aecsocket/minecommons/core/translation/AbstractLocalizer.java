package com.gitlab.aecsocket.minecommons.core.translation;

import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Filter;

/**
 * An abstract implementation of a {@link RegistryLocalizer}, defining a default locale,
 * fallback message and translations.
 */
public abstract class AbstractLocalizer implements RegistryLocalizer {
    /**
     * An abstract builder for a {@link AbstractLocalizer}.
     */
    public static abstract class Builder {
        /** The default locale. */
        protected Locale defaultLocale = Locale.getDefault();
        /** The fallback message. */
        protected String fallbackMessage = "<locale_key> <args>";

        /**
         * Gets the default locale.
         * @return The default locale.
         */
        public Locale defaultLocale() { return defaultLocale; }

        /**
         * Sets the default locale.
         * @param defaultLocale The default locale.
         * @return This instance.
         */
        public Builder defaultLocale(Locale defaultLocale) { this.defaultLocale = defaultLocale; return this; }

        /**
         * Gets the fallback message.
         * @return The fallback message.
         */
        public String fallbackMessage() { return fallbackMessage; }

        /**
         * Sets the fallback message.
         * @param fallbackMessage The fallback message.
         * @return This instance.
         */
        public Builder fallbackMessage(String fallbackMessage) { this.fallbackMessage = fallbackMessage; return this; }

        /**
         * Builds a localizer from the provided options.
         * @return The localizer.
         */
        public abstract AbstractLocalizer build();
    }

    private Locale defaultLocale;
    private String fallbackMessage;
    private final Map<Locale, Translation> translations = new HashMap<>();
    private final Map<Locale, Map<String, Component>> cache = new HashMap<>();

    /**
     * Creates an instance.
     * @param defaultLocale The default locale.
     * @param fallbackMessage The fallback message.
     */
    public AbstractLocalizer(Locale defaultLocale, String fallbackMessage) {
        this.defaultLocale = defaultLocale;
        this.fallbackMessage = fallbackMessage;
    }

    @Override public Locale defaultLocale() { return defaultLocale; }

    /**
     * Sets the default locale.
     * @param defaultLocale The default locale.
     */
    public void defaultLocale(Locale defaultLocale) { this.defaultLocale = defaultLocale; }

    /**
     * Gets the fallback message to translate, if a proper message cannot be obtained.
     * @return The fallback message.
     */
    public String fallbackMessage() { return fallbackMessage; }

    /**
     * Sets the fallback message to translate, if a proper message cannot be obtained.
     * @param fallbackMessage The fallback message.
     */
    public void fallbackMessage(String fallbackMessage) { this.fallbackMessage = fallbackMessage; }

    /**
     * Gets all registered translations.
     * @return The translations.
     */
    public Map<Locale, Translation> translations() { return translations; }

    @Override
    public Translation translation(Locale locale) { return translations.get(locale); }

    @Override
    public void register(Translation translation) {
        translations.computeIfAbsent(translation.locale(), Translation::new).putAll(translation);
    }

    @Override
    public void unregister(Translation translation) {
        translations.getOrDefault(translation.locale(), Translation.empty()).entrySet().removeIf(entry -> translation.containsKey(entry.getKey()));
        cache.getOrDefault(translation.locale(), Collections.emptyMap()).entrySet().removeIf(entry -> translation.containsKey(entry.getKey()));
    }

    @Override
    public void unregister(Locale locale) {
        translations.remove(locale);
        cache.remove(locale);
    }

    /**
     * Clears all translations, including the cache.
     */
    public void clear() {
        translations.clear();
        cache.clear();
    }

    /**
     * Removes all cached translations.
     */
    public void invalidateCache() { cache.clear(); }

    /**
     * Formats a string and arguments into a component.
     * @param value The string value.
     * @param args The arguments.
     * @return The component.
     */
    public abstract Component format(String value, Object... args);

    /**
     * Generates a fallback component using {@link #fallbackMessage}.
     * @param key The translation key.
     * @param args The arguments.
     * @return The fallback component.
     */
    public Component fallback(String key, Object... args) {
        return format(fallbackMessage,
                "locale_key", key,
                "args", args.length == 0 ? "" : Arrays.toString(args));
    }

    /**
     * Uses a translation of a locale for its component, otherwise uses a fallback.
     * @param locale The locale.
     * @param fallback The fallback.
     * @param key The key.
     * @param args The arguments.
     * @return The translated component.
     */
    protected Component use(Locale locale, Supplier<Component> fallback, String key, Object... args) {
        if (args.length == 0) {
            Component cached = cache.getOrDefault(locale, Collections.emptyMap()).get(key);
            if (cached != null)
                return cached;
        }
        Translation translation = translation(locale);
        if (translation == null || !translation.containsKey(key))
            return fallback.get();
        Component component = format(translation.get(key), args);
        if (args.length == 0)
            cache.computeIfAbsent(locale, l -> new HashMap<>()).put(key, component);
        return component;
    }

    @Override
    public Component localize(String key, Object... args) {
        return use(defaultLocale, () -> fallback(key, args), key, args);
    }

    @Override
    public Component localize(Locale locale, String key, Object... args) {
        return use(locale, () -> localize(key, args), key, args);
    }
}
