package com.gitlab.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Template;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public interface I18N {
    static Format format(@Nullable Style style, Map<String, Style> templates) {
        return new FormatImpl(style, templates);
    }

    interface FormatTemplateContext {
        FormatTemplateContext add(String key, Style style);
    }

    static Format format(@Nullable Style style, Consumer<FormatTemplateContext> templates) {
        Map<String, Style> built = new HashMap<>();
        templates.accept(new FormatTemplateContext() {
            @Override
            public FormatTemplateContext add(String key, Style style) {
                built.put(key, style);
                return this;
            }
        });
        return new FormatImpl(style, built);
    }

    static Format format(@Nullable Style style) {
        return new FormatImpl(style, Collections.emptyMap());
    }

    static Format format() {
        return new FormatImpl(null, Collections.emptyMap());
    }

    interface TemplateContext {
        Template of(String key, Component value);

        Template of(String key, String value);

        Template of(String key, Renderable value);

        Template of(String key, Object value);

        Template format(String key, String format, Object... args);

        Optional<Template> orLine(String key, String i18n, TemplateFactory... templates);

        Template line(String key, String i18n, TemplateFactory... templates);
    }

    static TemplateContext templateContext(I18N i18n, Locale locale, Function<String, @Nullable Style> styler) {
        return new TemplateContextImpl(i18n, locale, styler);
    }

    @FunctionalInterface
    interface TemplateFactory {
        Template create(TemplateContext c);
    }

    Locale defaultLocale();


    Optional<List<Component>> orLines(Locale locale, String key, TemplateFactory... templates);

    List<Component> lines(Locale locale, String key, TemplateFactory... templates);


    Optional<Component> orLine(Locale locale, String key, TemplateFactory... templates);

    Component line(Locale locale, String key, TemplateFactory... templates);

    static void test() {
        I18N i18n = null;
        Locale locale = Locale.US;
        Audience audience = null;

        Style ACCENT = Style.style(TextColor.color(127, 127, 127));
        Style INFO = Style.style(NamedTextColor.GRAY);
        Style VARIABLE = Style.style(NamedTextColor.WHITE);

        String COMMAND_VERSION = "command.version";
        String CONSTANT_ON = "constant.on";
        String CONSTANT_OFF = "constant.off";
        String NAME = "name";

        boolean function = true;
        i18n.lines(locale, COMMAND_VERSION,
                c -> c.of("name", "MyPlugin"),
                c -> c.of("function", function ? CONSTANT_ON : CONSTANT_OFF),
                c -> c.line("author", NAME))
                .forEach(audience::sendMessage);
    }
}
