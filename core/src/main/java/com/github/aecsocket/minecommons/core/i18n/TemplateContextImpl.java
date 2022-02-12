package com.github.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.*;

/* package */ record TemplateContextImpl(
    I18N i18n,
    Locale locale,
    Function<String, @Nullable Style> styler
) implements I18N.TemplateContext {
    @Override
    public I18N.Template of(String key, Supplier<Component> value) {
        return new I18N.Template(key, () -> style(key, value.get()));
    }

    private Component style(String key, Component value) {
        if (value.hasStyling())
            return value;
        Style style = styler.apply(key);
        return style == null ? value : value.style(style);
    }

    @Override
    public Component rd(Renderable value) {
        return value.render(i18n, locale);
    }

    @Override
    public Component rd(String format, Object... args) {
        return text(String.format(locale, format, args));
    }

    @Override
    public Optional<Component> orLine(String key, I18N.TemplateFactory... templates) {
        return i18n.orLine(locale, key, templates);
    }

    @Override
    public Component line(String key, I18N.TemplateFactory... templates) {
        return i18n.line(locale, key, templates);
    }
}
