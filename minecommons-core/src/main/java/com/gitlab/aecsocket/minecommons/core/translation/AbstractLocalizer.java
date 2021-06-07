package com.gitlab.aecsocket.minecommons.core.translation;

import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.function.Supplier;

/**
 * An abstract implementation of a {@link RegistryLocalizer}, defining a default locale,
 * fallback message and translations.
 */
public abstract class AbstractLocalizer implements RegistryLocalizer {
    /**
     * An abstract builder for a {@link AbstractLocalizer}.
     */
    public static abstract class Builder {
        protected Locale defaultLocale = Locale.getDefault();
        protected String fallbackMessage = "<locale_key> <args>";

        public Locale defaultLocale() { return defaultLocale; }
        public Builder defaultLocale(Locale defaultLocale) { this.defaultLocale = defaultLocale; return this; }

        public String fallbackMessage() { return fallbackMessage; }
        public Builder fallbackMessage(String fallbackMessage) { this.fallbackMessage = fallbackMessage; return this; }

        public abstract AbstractLocalizer build();
    }

    private Locale defaultLocale;
    private String fallbackMessage;
    private final Map<Locale, Translation> translations = new HashMap<>();

    public AbstractLocalizer(Locale defaultLocale, String fallbackMessage) {
        this.defaultLocale = defaultLocale;
        this.fallbackMessage = fallbackMessage;
    }

    @Override public Locale defaultLocale() { return defaultLocale; }
    public void defaultLocale(Locale defaultLocale) { this.defaultLocale = defaultLocale; }

    public String fallbackMessage() { return fallbackMessage; }
    public void fallbackMessage(String fallbackMessage) { this.fallbackMessage = fallbackMessage; }

    public Map<Locale, Translation> translations() { return translations; }

    @Override
    public Translation translation(Locale locale) { return translations.get(locale); }

    @Override
    public void register(Translation translation) {
        translations.computeIfAbsent(translation.locale(), Translation::new).putAll(translation);
    }

    @Override
    public void unregister(Translation translation) {
        Translation ourTranslation = translations.get(translation.locale());
        if (ourTranslation == null)
            return;
        ourTranslation.entrySet().removeIf(entry -> translation.containsKey(entry.getKey()));
    }

    @Override
    public void unregister(Locale locale) {
        translations.remove(locale);
    }

    /**
     * Clears all translations.
     */
    public void clear() { translations.clear(); }

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

    protected Component use(Locale locale, Supplier<Component> fallback, String key, Object... args) {
        Translation translation = translation(locale);
        if (translation == null || !translation.containsKey(key))
            return fallback.get();
        return format(translation.get(key), args);
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
