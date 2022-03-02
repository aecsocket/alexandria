package com.github.aecsocket.minecommons.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.aecsocket.minecommons.core.i18n.I18N;
import com.github.aecsocket.minecommons.core.i18n.Renderable;
import com.github.aecsocket.minecommons.core.i18n.Translation;

import static org.junit.jupiter.api.Assertions.*;
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

    I18N createI18N() {
        I18N i18n = new I18N(MiniMessage.miniMessage(), Locale.US);

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

    void assertComponentEquals(Component expected, Component actual) {
        Assertions.assertEquals(
            GsonComponentSerializer.gson().serialize(expected),
            GsonComponentSerializer.gson().serialize(actual)
        );
    }

    @Test
    void testBasic() {
        I18N i18n = createI18N();
        assertComponentEquals(text("", GRAY).append(text("Basic message")), i18n.line(Locale.US, BASIC));
        assertComponentEquals(text("", GRAY).append(text("UK basic message")), i18n.line(Locale.UK, BASIC));
    }

    @Test
    void testFallbacks() {
        I18N i18n = createI18N();

        assertComponentEquals(text("", GRAY).append(text("Fallback message")), i18n.line(Locale.US, FALLBACK));
        assertComponentEquals(text("", GRAY).append(text("Fallback message")), i18n.line(Locale.UK, FALLBACK));

        assertComponentEquals(text(UNKNOWN_KEY), i18n.line(Locale.US, UNKNOWN_KEY));
        assertComponentEquals(text(UNKNOWN_KEY), i18n.line(Locale.UK, UNKNOWN_KEY));

        assertEquals(Optional.empty(), i18n.orLines(Locale.US, UNKNOWN_KEY));
        assertEquals(Optional.empty(), i18n.orLine(Locale.US, UNKNOWN_KEY));
    }

    @Test
    void testPlaceholder() {
        I18N i18n = createI18N();
        assertComponentEquals(text("", GRAY)
            .append(text("Placeholder: One")),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> text("One"))));
        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Placeholder: "))
                .append(text("Two", RED))),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> text("Two", RED))));
    }

    @Test
    void testRenderable() {
        I18N i18n = createI18N();
        Renderable rr = (i18n1, locale) -> i18n1.line(locale, RENDERABLE);

        assertComponentEquals(text("Renderable"),
            rr.render(i18n, Locale.US));
        assertComponentEquals(text("UK renderable"),
            rr.render(i18n, Locale.UK));

        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Placeholder: "))
                .append(text("Renderable"))),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.rd(rr))));
    }

    record Dummy(int value) {}

    @Test
    void testPlaceholderFormat() {
        I18N i18n = createI18N();
        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Placeholder: "))
                .append(text("1,234.5"))),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.rd("%,.1f", 1234.54))));
        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Placeholder: "))
                .append(text("1.234,5"))),
            i18n.line(Locale.GERMAN, PLACEHOLDERS,
                c -> c.of("target", () -> c.rd("%,.1f", 1234.54))));

        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Placeholder: "))
                .append(text("Dummy[value=3]"))),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> text(""+new Dummy(3)))));
    }

    @Test
    void testPlaceholderStyling() {
        I18N i18n = createI18N();
        assertComponentEquals(text("", NamedTextColor.GRAY)
            .append(text("")
                .append(text("Styled placeholder: "))
                .append(text("Blue", NamedTextColor.BLUE))),
            i18n.line(Locale.US, PLACEHOLDERS_STYLED,
                    c -> c.of("target", () -> text("Blue"))));
        assertComponentEquals(text("", NamedTextColor.GRAY)
            .append(text("")
                .append(text("Styled placeholder: "))
                .append(text("Green", NamedTextColor.GREEN))),
            i18n.line(Locale.US, PLACEHOLDERS_STYLED,
                    c -> c.of("target", () -> text("Green", NamedTextColor.GREEN))));
    }

    @Test
    void testMiniMessage() {
        I18N i18n = createI18N();
        assertComponentEquals(text("", NamedTextColor.GRAY)
            .append(text("")
                .append(text("MiniMessage test: "))
                .append(text("bold").decorate(BOLD))),
            i18n.line(Locale.US, MINIMESSAGE)
        );
    }

    @Test
    void testNested() {
        I18N i18n = createI18N();
        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Placeholder: "))
                .append(text("A constant"))),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.line(CONSTANT))));
        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Placeholder: "))
                .append(text("", BLUE)
                    .append(text("A styled constant")))),
            i18n.line(Locale.US, PLACEHOLDERS,
                c -> c.of("target", () -> c.line(STYLED_CONSTANT))));
        assertComponentEquals(text("", GRAY)
            .append(text("")
                .append(text("Styled placeholder: "))
                .append(text("", BLUE)
                    .append(text("A styled constant")))),
            i18n.line(Locale.US, PLACEHOLDERS_STYLED,
                c -> c.of("target", () -> c.line(STYLED_CONSTANT))));
    }

    @Test
    void testAdHoc() {
        I18N i18n = createI18N();
        assertComponentEquals(text("Ad hoc message"),
            i18n.line(Locale.US, AD_HOC));
    }

    @Test
    void testSideEffects() {
        I18N i18n = createI18N();

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
