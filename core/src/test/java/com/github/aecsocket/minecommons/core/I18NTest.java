package com.github.aecsocket.minecommons.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.aecsocket.minecommons.core.i18n.MiniMessageI18N;
import com.github.aecsocket.minecommons.core.i18n.Renderable;
import com.github.aecsocket.minecommons.core.i18n.Translation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.github.aecsocket.minecommons.core.i18n.I18N.*;
import static com.github.aecsocket.minecommons.core.i18n.Translation.*;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.Style.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

class I18NTest {
    static final String
        INFO = "info",
        ACCENT = "accent",

        BASIC = "basic",
        FALLBACK = "fallback",
        PLACEHOLDERS = "placeholders",
        PLACEHOLDERS_STYLED = "placeholders_styled",
        MINIMESSAGE = "minimessage",
        CONSTANT = "constant",
        STYLED_CONSTANT = "styled_constant",
        RENDERABLE = "renderable",
        AD_HOC = "ad_hoc",
        UNKNOWN_KEY = "unknown_key";

    static final Translation US = translation(Locale.US, tl -> tl
        .add(BASIC, "Basic message")
        .add(FALLBACK, "Fallback message")
        .add(PLACEHOLDERS, "Placeholder: <target>")
        .add(PLACEHOLDERS_STYLED, "Styled placeholder: <target>")
        .add(MINIMESSAGE, "MiniMessage test: <b>bold</b>")
        .add(CONSTANT, "A constant")
        .add(STYLED_CONSTANT, "A styled constant")
        .add(RENDERABLE, "Renderable")
        .add(AD_HOC, "Ad hoc message"));
    static final Translation UK = translation(Locale.UK, tl -> tl
        .add(BASIC, "UK basic message")
        .add(RENDERABLE, "UK renderable"));

    MiniMessageI18N createI18N() {
        MiniMessageI18N i18n = new MiniMessageI18N(MiniMessage::builder, Locale.US);

        i18n.registerStyle(INFO, style(GRAY));
        i18n.registerStyle(ACCENT, style(BLUE));

        i18n.registerFormat(BASIC, format(INFO));
        i18n.registerFormat(FALLBACK, format(INFO));
        i18n.registerFormat(PLACEHOLDERS, format(INFO));
        i18n.registerFormat(PLACEHOLDERS_STYLED, format(INFO, tp -> tp
            .add("target", ACCENT)));
        i18n.registerFormat(MINIMESSAGE, format(INFO));
        i18n.registerFormat(CONSTANT, format());
        i18n.registerFormat(STYLED_CONSTANT, format(ACCENT));
        i18n.registerFormat(RENDERABLE, format());

        i18n.registerTranslation(US);
        i18n.registerTranslation(UK);

        return i18n;
    }

    void assertEquals(Component expected, Component actual) {
        Assertions.assertEquals(
            GsonComponentSerializer.gson().serialize(expected),
            GsonComponentSerializer.gson().serialize(actual)
        );
    }

    @Test
    void testBasic() {
        MiniMessageI18N i18n = createI18N();
        assertEquals(text("Basic message", GRAY), i18n.line(Locale.US, BASIC));
        assertEquals(text("UK basic message", GRAY), i18n.line(Locale.UK, BASIC));
    }

    @Test
    void testFallbacks() {
        MiniMessageI18N i18n = createI18N();

        assertEquals(text("Fallback message", GRAY), i18n.line(Locale.US, FALLBACK));
        assertEquals(text("Fallback message", GRAY), i18n.line(Locale.UK, FALLBACK));

        assertEquals(text(UNKNOWN_KEY), i18n.line(Locale.US, UNKNOWN_KEY));
        assertEquals(text(UNKNOWN_KEY), i18n.line(Locale.UK, UNKNOWN_KEY));
    }

    @Test
    void testPlaceholder() {
        MiniMessageI18N i18n = createI18N();
        assertEquals(text("", GRAY)
            .append(text("Placeholder: "))
            .append(text("One")),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> text("One"))));
        assertEquals(text("", GRAY)
            .append(text("Placeholder: "))
            .append(text("Two", RED)),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> text("Two", RED))));
    }

    @Test
    void testRenderable() {
        MiniMessageI18N i18n = createI18N();
        Renderable rr = (i18n1, locale) -> i18n1.line(locale, RENDERABLE);

        assertEquals(text("Renderable"),
            rr.render(i18n, Locale.US));
        assertEquals(text("UK renderable"),
            rr.render(i18n, Locale.UK));

        assertEquals(text("", GRAY)
            .append(text("Placeholder: "))
            .append(text("Renderable")),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.rd(rr))));
    }

    record Dummy(int value) {}

    @Test
    void testPlaceholderFormat() {
        MiniMessageI18N i18n = createI18N();
        assertEquals(text("", GRAY)
            .append(text("Placeholder: "))
            .append(text("1,234.5")),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.rd("%,.1f", 1234.54))));
        assertEquals(text("", GRAY)
            .append(text("Placeholder: "))
            .append(text("1.234,5")),
            i18n.line(Locale.GERMAN, PLACEHOLDERS,
                c -> c.of("target", () -> c.rd("%,.1f", 1234.54))));

        assertEquals(text("", GRAY)
            .append(text("Placeholder: "))
            .append(text("Dummy[value=3]")),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> text(""+new Dummy(3)))));
    }

    @Test
    void testPlaceholderStyling() {
        MiniMessageI18N i18n = createI18N();
        assertEquals(text("", NamedTextColor.GRAY)
            .append(text("Styled placeholder: "))
            .append(text("Blue", NamedTextColor.BLUE)),
            i18n.line(Locale.US, PLACEHOLDERS_STYLED,
                    c -> c.of("target", () -> text("Blue"))));
        assertEquals(text("", NamedTextColor.GRAY)
            .append(text("Styled placeholder: "))
            .append(text("Green", NamedTextColor.GREEN)),
            i18n.line(Locale.US, PLACEHOLDERS_STYLED,
                    c -> c.of("target", () -> text("Green", NamedTextColor.GREEN))));
    }

    @Test
    void testMiniMessage() {
        MiniMessageI18N i18n = createI18N();
        assertEquals(text("", NamedTextColor.GRAY)
            .append(text("MiniMessage test: "))
            .append(text("bold").decorate(BOLD)),
            i18n.line(Locale.US, MINIMESSAGE)
        );
    }

    @Test
    void testNested() {
        MiniMessageI18N i18n = createI18N();
        assertEquals(text("", NamedTextColor.GRAY)
            .append(text("Placeholder: "))
            .append(text("A constant")),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.line(CONSTANT))));
        assertEquals(text("", NamedTextColor.GRAY)
            .append(text("Placeholder: "))
            .append(text("A styled constant", NamedTextColor.BLUE)),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.line(STYLED_CONSTANT))));
        assertEquals(text("", NamedTextColor.GRAY)
            .append(text("Styled placeholder: "))
            .append(text("A styled constant", NamedTextColor.BLUE)),
            i18n.line(Locale.US, PLACEHOLDERS_STYLED,
                c -> c.of("target", () -> c.line(STYLED_CONSTANT))));
    }

    @Test
    void testAdHoc() {
        MiniMessageI18N i18n = createI18N();
        assertEquals(text("Ad hoc message"),
            i18n.line(Locale.US, AD_HOC));
    }

    @Test
    void testSideEffects() {
        MiniMessageI18N i18n = createI18N();

        // The `basic` key doesn't have the <target> template
        // So the side effect code isn't ran
        AtomicBoolean flag = new AtomicBoolean();
        i18n.line(Locale.US, BASIC,
            c -> c.of("target", () -> {
                flag.set(true);
                return text("target");
            }));
        assertFalse(flag.get());

        // The `placeholders` key DOES have the <target> template
        // So the side effect code IS ran
        i18n.line(Locale.US, PLACEHOLDERS,
            c -> c.of("target", () -> {
                flag.set(true);
                return text("target");
            }));
        assertTrue(flag.get());
    }
}
