package com.github.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.Template;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A class which provides localization services based on a key and placeholder templates.
 */
public interface I18N {
    /**
     * Creates a message format.
     * @param style The key of the style of the message.
     * @param templates The keys of the styles of placeholders in the message.
     * @return The format.
     */
    static Format format(@Nullable String style, Map<String, String> templates) {
        return new FormatImpl(style, templates);
    }

    /**
     * Context for building format template styles.
     */
    interface FormatTemplateContext {
        /**
         * Adds a style to a placeholder template.
         * @param key The placeholder key.
         * @param style The style key.
         * @return This instance.
         */
        FormatTemplateContext add(String key, String style);
    }

    /**
     * Creates a message format.
     * @param style The key of the style of the message.
     * @param templates The keys of the styles of placeholders in the message.
     * @return The format.
     */
    static Format format(@Nullable String style, Consumer<FormatTemplateContext> templates) {
        Map<String, String> built = new HashMap<>();
        templates.accept(new FormatTemplateContext() {
            @Override
            public FormatTemplateContext add(String key, String style) {
                built.put(key, style);
                return this;
            }
        });
        return new FormatImpl(style, built);
    }

    /**
     * Creates a message format, with no styling for placeholders.
     * @param style The key of the style of the message.
     * @return The format.
     */
    static Format format(@Nullable String style) {
        return new FormatImpl(style, Collections.emptyMap());
    }

    /**
     * Creates a message format, with no styling.
     * @return The format.
     */
    static Format format() {
        return new FormatImpl(null, Collections.emptyMap());
    }

    /**
     * Context for creating values for placeholders in a message.
     */
    interface TemplateContext {
        /**
         * Creates a template from a component.
         * @param key The placeholder key.
         * @param value The component.
         * @return The template.
         */
        Template of(String key, Component value);

        /**
         * Creates a template from a string.
         * @param key The placeholder key.
         * @param value The string.
         * @return The template.
         */
        Template of(String key, String value);

        /**
         * Creates a template from a renderable.
         * @param key The placeholder key.
         * @param value The renderable.
         * @return The template.
         */
        Template of(String key, Renderable value);

        /**
         * Creates a template from a generic object, using {@link Object#toString()}.
         * @param key The placeholder key.
         * @param value The object.
         * @return The template.
         */
        Template of(String key, Object value);

        /**
         * Creates a template by using a format string, automatically using the contextual locale.
         * <p>
         * Uses {@link String#format(String, Object...)}.
         * @param key The placeholder key.
         * @param format The format string.
         * @param args The format args.
         * @return The template.
         */
        Template format(String key, String format, Object... args);

        /**
         * Creates a template by generating a line of another localized message.
         * @param key The placeholder key.
         * @param i18n The i18n key.
         * @param templates The i18n placeholders.
         * @return The template.
         */
        Optional<Template> orLine(String key, String i18n, TemplateFactory... templates);

        /**
         * Creates a template by generating a line of another localized message.
         * <p>
         * If no translation found, the message will be the {@code i18n} param as a text component.
         * @param key The placeholder key.
         * @param i18n The i18n key.
         * @param templates The i18n placeholders.
         * @return The template.
         */
        Template line(String key, String i18n, TemplateFactory... templates);
    }

    /**
     * Creates a template context.
     * @param i18n The i18n service.
     * @param locale The locale.
     * @param styler The function mapping a message key to a style.
     * @return The context.
     */
    static TemplateContext templateContext(I18N i18n, Locale locale, Function<String, @Nullable Style> styler) {
        return new TemplateContextImpl(i18n, locale, styler);
    }

    /**
     * Creates a template from the context of a message localization.
     */
    @FunctionalInterface
    interface TemplateFactory {
        /**
         * Creates a template from the context of a message localization.
         * @param c The context.
         * @return The template.
         */
        Template create(TemplateContext c);
    }

    /**
     * Gets the default locale of this service.
     * @return The default locale.
     */
    Locale locale();

    /**
     * Generates lines of a localized message based on a key and placeholder arguments.
     * @param locale The locale.
     * @param key The localization key.
     * @param templates The placeholder arguments.
     * @return The lines of the message.
     */
    Optional<List<Component>> orLines(Locale locale, String key, TemplateFactory... templates);

    /**
     * Generates lines of a localized message based on a key and placeholder arguments.
     * <p>
     * Transforms each line before returning.
     * @param locale The locale.
     * @param key The localization key.
     * @param transform The transform to apply to each line.
     * @param templates The placeholder arguments.
     * @return The lines of the message.
     */
    default Optional<List<Component>> orModLines(Locale locale, String key, Function<Component, Component> transform, TemplateFactory... templates) {
        return orLines(locale, key, templates)
            .map(lines -> {
                List<Component> result = new ArrayList<>();
                for (var line : lines) {
                    result.add(transform.apply(line));
                }
                return result;
            });
    }

    /**
     * Generates lines of a localized message based on a key and placeholder arguments.
     * <p>
     * If no translation found, the message will be 1 line consisting of the {@code key} parameter as a text component.
     * @param locale The locale.
     * @param key The localization key.
     * @param templates The placeholder arguments.
     * @return The lines of the message.
     */
    List<Component> lines(Locale locale, String key, TemplateFactory... templates);

    /**
     * Generates lines of a localized message based on a key and placeholder arguments.
     * <p>
     * If no translation found, the message will be 1 line consisting of the {@code key} parameter as a text component.
     * <p>
     * Transforms each line before returning.
     * @param locale The locale.
     * @param key The localization key.
     * @param transform The transform to apply to each line.
     * @param templates The placeholder arguments.
     * @return The lines of the message.
     */
    default List<Component> modLines(Locale locale, String key, Function<Component, Component> transform, TemplateFactory... templates) {
        List<Component> result = new ArrayList<>();
        for (var line : lines(locale, key, templates)) {
            result.add(transform.apply(line));
        }
        return result;
    }

    /**
     * Generates one component for localized message based on a key and placeholder arguments.
     * <p>
     * If originally multipline, will join with {@link Component#newline()}s.
     * @param locale The locale.
     * @param key The localization key.
     * @param templates The placeholder arguments.
     * @return The lines of the message.
     */
    Optional<Component> orLine(Locale locale, String key, TemplateFactory... templates);

    /**
     * Generates one component for localized message based on a key and placeholder arguments.
     * <p>
     * If originally multipline, will join with {@link Component#newline()}s.
     * If no translation found, the message will be 1 line consisting of the {@code key} parameter as a text component.
     * @param locale The locale.
     * @param key The localization key.
     * @param templates The placeholder arguments.
     * @return The lines of the message.
     */
    Component line(Locale locale, String key, TemplateFactory... templates);
}
