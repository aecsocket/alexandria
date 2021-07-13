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
        /** The default locale. */
        protected Locale defaultLocale = Locale.getDefault();

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
         * Builds a localizer from the provided options.
         * @return The localizer.
         */
        public abstract AbstractLocalizer build();
    }

    private Locale defaultLocale;
    private final Map<Locale, Translation> translations = new HashMap<>();
    private final Map<Locale, Map<String, List<Component>>> cache = new HashMap<>();

    /**
     * Creates an instance.
     * @param defaultLocale The default locale.
     */
    public AbstractLocalizer(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override public Locale defaultLocale() { return defaultLocale; }

    /**
     * Sets the default locale.
     * @param defaultLocale The default locale.
     */
    public void defaultLocale(Locale defaultLocale) { this.defaultLocale = defaultLocale; }

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
     * Uses a translation of a locale for its components, otherwise uses a fallback.
     * @param locale The locale.
     * @param fallback The fallback.
     * @param key The key.
     * @param args The arguments.
     * @return The translated components.
     */
    protected Optional<List<Component>> use(Locale locale, Supplier<Optional<List<Component>>> fallback, String key, Object... args) {
        if (args.length == 0) {
            List<Component> cached = cache.getOrDefault(locale, Collections.emptyMap()).get(key);
            if (cached != null)
                return Optional.of(cached);
        }
        Translation translation = translation(locale);
        if (translation == null || !translation.containsKey(key))
            return fallback.get();

        List<Component> lines = new ArrayList<>();
        for (String line : translation.get(key))
            lines.add(format(line, args));

        if (args.length == 0)
            cache.computeIfAbsent(locale, l -> new HashMap<>()).put(key, lines);
        return Optional.of(lines);
    }

    @Override
    public Optional<Component> get(Locale locale, String key, Object... args) {
        return use(locale, () -> use(defaultLocale, Optional::empty, key, args), key, args)
                .map(c -> Component.join(Component.newline(), c));
    }

    @Override
    public Optional<List<Component>> lines(Locale locale, String key, Object... args) {
        return use(locale, () -> use(defaultLocale, Optional::empty, key, args), key, args);
    }
}
