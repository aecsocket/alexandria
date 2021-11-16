package com.gitlab.aecsocket.minecommons.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

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
     * Creates a bar from striked-through components and a placeholder character.
     * @param length The length of the bar, in characters.
     * @param value The percentage that the bar is full.
     * @param placeholder The placeholder character.
     * @param full The color of the full part.
     * @param empty The color of the empty part.
     * @return The bar.
     */
    public static Component bar(int length, double value, String placeholder, TextColor full, TextColor empty) {
        TextComponent[] components = new TextComponent[length];
        Arrays.fill(components, text(placeholder, empty, STRIKETHROUGH));
        for (int i = 0; i < Math.min(length, value * length); i++) {
            components[i] = components[i].color(full);
        }
        return join(JoinConfiguration.noSeparators(), components);
    }
}
