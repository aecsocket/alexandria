package com.github.aecsocket.minecommons.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.TextDecoration.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * Utilities for components.
 */
public final class Components {
    private Components() {}

    /**
     * A component which acts as a "reset", with white text color and no italics.
     * <p>
     * Intended to be used as a parent to item names or lore.
     */
    public static final Component BLANK = empty()
        .color(WHITE)
        .decoration(ITALIC, false);

    /**
     * Repeats a component a specified amount of times.
     * @param component The component to repeat.
     * @param amount The amount of times to repeat, >= 0.
     * @return The repeated component.
     */
    public static Component repeat(Component component, int amount) {
        Validation.greaterThanEquals("amount", amount, 0);
        var result = text();
        for (int i = 0; i < amount; i++) {
            result.append(component);
        }
        return result.build();
    }

    /**
     * A section of a rendered bar.
     * @param value The percentage this section takes up.
     * @param color The color of this section.
     */
    public record BarSection(double value, TextColor color) {}

    /**
     * Creates a section of a rendered bar.
     * @param value The percentage this section takes up.
     * @param color The color of this section.
     * @return The section.
     */
    public static BarSection barSection(double value, TextColor color) {
        return new BarSection(value, color);
    }

    /**
     * Renders a limited bar from striked-through components and a placeholder character, with multiple sections.
     * @param length The length of the bar, in characters.
     * @param placeholder The placeholder character.
     * @param fill The fill for unused sections of the bar.
     * @param sections The sections in the bar.
     * @return The bar.
     */
    public static Component bar(int length, String placeholder, TextColor fill, Iterable<? extends BarSection> sections) {
        Validation.greaterThanEquals("length", length, 0);
        TextComponent[] chars = new TextComponent[length];
        Arrays.fill(chars, text(placeholder, fill, STRIKETHROUGH));
        int i = 0;
        for (var section : sections) {
            int end = Math.min(length, (int) (i + section.value() * length));
            for (; i < end; i++)
                chars[i] = chars[i].color(section.color());
        }
        return join(JoinConfiguration.noSeparators(), chars);
    }

    /**
     * Renders a bar from striked-through components and a placeholder character, with multiple sections.
     * @param length The length of the bar, in characters.
     * @param placeholder The placeholder character.
     * @param fill The fill for unused sections of the bar. If null, will not render unused sections.
     * @param sections The sections in the bar.
     * @return The bar.
     */
    public static Component bar(int length, String placeholder, @Nullable TextColor fill, BarSection... sections) {
        return bar(length, placeholder, fill, Arrays.asList(sections));
    }
}
