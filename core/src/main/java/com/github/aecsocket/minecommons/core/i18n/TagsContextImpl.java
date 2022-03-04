package com.github.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Inserting;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.kyori.adventure.text.Component.*;

/* package */ record TagsContextImpl(
    I18N i18n,
    Locale locale,
    Function<String, @Nullable Style> styler
) implements I18N.TagsContext {
    @Override
    public TagResolver of(String name, Supplier<Component> value) {
        return TagResolver.resolver(name, (Inserting) () -> style(name, value.get()));
    }

    private Component style(String name, Component value) {
        if (value.hasStyling())
            return value;
        Style style = styler.apply(name);
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
    public Optional<Component> orLine(String key, I18N.Tags... tags) {
        return i18n.orLine(locale, key, tags);
    }

    @Override
    public Component line(String key, I18N.Tags... tags) {
        return i18n.line(locale, key, tags);
    }
}
