package com.gitlab.aecsocket.minecommons.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringJoiner;

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
    public static final Component BLANK = Component.empty()
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, false);

    /**
     * Repeats a component a specified amount of times.
     * @param component The component to repeat.
     * @param amount The amount of times to repeat, >= 0.
     * @return The repeated component.
     */
    public static Component repeat(Component component, int amount) {
        Validation.greaterThanEquals("amount", amount, 0);
        TextComponent.Builder result = Component.text();
        for (int i = 0; i < amount; i++) {
            result.append(component);
        }
        return result.build();
    }
}
