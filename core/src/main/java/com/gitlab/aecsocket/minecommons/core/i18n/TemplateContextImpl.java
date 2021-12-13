package com.gitlab.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.Template;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.*;

/* package */ record TemplateContextImpl(
        I18N i18n,
        Locale locale,
        Function<String, @Nullable Style> styler
) implements I18N.TemplateContext {
    @Override
    public Template of(String key, Component value) {
        return Template.of(key, style(key, value));
    }

    private Component style(String key, Component value) {
        if (value.hasStyling())
            return value;
        Style style = styler.apply(key);
        return style == null ? value : value.style(style);
    }

    @Override
    public Template of(String key, String value) {
        return Template.of(key, style(key, text(value)));
    }

    @Override
    public Template of(String key, Renderable value) {
        return Template.of(key, style(key, value.render(i18n, locale)));
    }

    @Override
    public Template of(String key, Object value) {
        return Template.of(key, style(key, text(""+value)));
    }

    @Override
    public Template format(String key, String format, Object... args) {
        return Template.of(key, style(key, text(String.format(locale, format, args))));
    }

    @Override
    public Optional<Template> orLine(String key, String i18n, I18N.TemplateFactory... templates) {
        return this.i18n.orLine(locale, i18n, templates)
                .map(comp -> Template.of(key, style(i18n, comp)));
    }

    @Override
    public Template line(String key, String i18n, I18N.TemplateFactory... templates) {
        return Template.of(key, style(key, this.i18n.line(locale, i18n, templates)));
    }
}
