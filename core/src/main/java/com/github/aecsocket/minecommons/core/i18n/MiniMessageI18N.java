package com.github.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import java.util.*;

/**
 * An i18n service using the {@link MiniMessage} platform to parse messages.
 */
public final class MiniMessageI18N implements MutableI18N {
    private static final JoinConfiguration join = JoinConfiguration.separator(Component.newline());

    private final MiniMessage miniMessage;
    private final Map<String, Style> styles = new HashMap<>();
    private final Map<String, Format> formats = new HashMap<>();
    private final Map<Locale, Translation> translations = new HashMap<>();
    private final Map<String, Map<Locale, List<Component>>> cache = new HashMap<>();

    private Locale defaultLocale;
    private Translation defaultTranslation;

    /**
     * Creates an instance.
     * @param miniMessage The MiniMessage instance.
     * @param defaultLocale The default locale.
     */
    public MiniMessageI18N(MiniMessage miniMessage, Locale defaultLocale) {
        this.miniMessage = miniMessage;
        this.defaultLocale = defaultLocale;
    }

    /**
     * Gets the MiniMessage instance used.
     * @return The MiniMessage instance.
     */
    public MiniMessage miniMessage() { return miniMessage; }

    /**
     * Gets all registered styles.
     * @return The styles.
     */
    public Map<String, Style> styles() { return styles; }

    @Override
    public void registerStyle(String key, Style style) {
        styles.put(key, style);
    }

    /**
     * Gets all registered formats.
     * @return The formats.
     */
    public Map<String, Format> formats() { return formats; }

    @Override
    public void registerFormat(String key, Format format) {
        formats.put(key, format);
    }

    /**
     * Gets all translations registered on this.
     * @return The translations.
     */
    public Map<Locale, Translation> translations() { return translations; }

    @Override
    public void registerTranslation(Translation translation) {
        Locale locale = translation.locale();
        Translation existing = translations.get(locale);
        if (existing == null)
            translations.put(locale, translation);
        else
            existing.handle().putAll(translation.handle());
    }

    @Override public Locale defaultLocale() { return defaultLocale; }

    /**
     * Sets the default locale.
     * @param defaultLocale The default locale.
     */
    public void defaultLocale(Locale defaultLocale) { this.defaultLocale = defaultLocale; }

    @Override
    public void clear() {
        styles.clear();
        formats.clear();
        translations.clear();
        cache.clear();
    }

    /**
     * Lazily gets the default translation, based on the {@link #defaultLocale}.
     * @return The default translation.
     */
    public Translation defaultTranslation() {
        if (defaultTranslation == null) defaultTranslation = translations.get(defaultLocale);
        if (defaultTranslation == null)
            throw new IllegalStateException("No translation for default locale `" + defaultLocale.toLanguageTag() + "`");
        return defaultTranslation;
    }

    /**
     * Gets a translation.
     * @param locale The locale.
     * @param key The translation key.
     * @return The translation.
     */
    public List<String> translation(Locale locale, String key) {
        Translation translation = translations.get(locale);
        List<String> value;
        if (translation == null || (value = translation.get(key)) == null)
            return defaultTranslation().get(key, Collections.singletonList(key));
        return value;
    }

    @Override
    public Optional<List<Component>> orLines(Locale locale, String key, TemplateFactory... templates) {
        boolean caches = templates.length == 0;
        if (caches) {
            // get from cache
            var cacheValues = cache.get(key);
            List<Component> cacheValue;
            if (cacheValues != null && (cacheValue = cacheValues.get(locale)) != null)
                return Optional.of(cacheValue);
        }

        Format format = formats.get(key);
        Style style;
        TemplateContext ctx;
        if (format == null) {
            // ad-hoc format
            style = null;
            ctx = I18N.templateContext(this, locale, k -> null);
        } else {
            style = styles.get(format.style());
            ctx = I18N.templateContext(this, locale, k -> styles.get(format.templates().get(k)));
        }

        List<Template> placeholders = new ArrayList<>();
        for (var factory : templates) {
            placeholders.add(factory.create(ctx));
        }

        List<Component> lines = new ArrayList<>();
        for (var line : translation(locale, key)) {
            Component generated = miniMessage.parse(line, placeholders);
            lines.add(style == null ? generated : generated.style(style));
        }

        if (caches)
            cache.computeIfAbsent(key, k -> new HashMap<>())
                    .put(locale, lines);
        return Optional.of(lines);
    }

    @Override
    public List<Component> lines(Locale locale, String key, TemplateFactory... templates) {
        return orLines(locale, key, templates)
                .orElse(Collections.singletonList(Component.text(key)));
    }

    @Override
    public Optional<Component> orLine(Locale locale, String key, TemplateFactory... templates) {
        return orLines(locale, key, templates)
                .map(comps -> Component.join(join, comps));
    }

    @Override
    public Component line(Locale locale, String key, TemplateFactory... templates) {
        return orLine(locale, key, templates)
                .orElse(Component.text(key));
    }
}
