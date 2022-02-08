package com.github.aecsocket.minecommons.core;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collection;
import java.util.Map;

import static net.kyori.adventure.text.Component.*;

/**
 * Utilities for quickly printing objects to an audience (to use color coding).
 */
public final class Debug {
    private Debug() {}

    private static final TextColor SEPARATOR = NamedTextColor.DARK_GRAY;
    private static final TextColor KEY = NamedTextColor.GRAY;
    private static final TextColor VALUE = NamedTextColor.WHITE;

    /**
     * Sends a map's key/value pairs.
     * @param audience The audience.
     * @param map The map.
     */
    public static void send(Audience audience, Map<?, ?> map) {
        if (map.isEmpty())
            audience.sendMessage(text("{}", SEPARATOR));
        else {
            audience.sendMessage(text("{", SEPARATOR));
            for (var entry : map.entrySet()) {
                audience.sendMessage(
                    text("  ")
                        .append(text(""+entry.getKey(), KEY))
                        .append(text(": ", SEPARATOR))
                        .append(text(""+entry.getValue(), VALUE))
                );
            }
            audience.sendMessage(text("}", SEPARATOR));
        }
    }

    /**
     * Sends a collection's elements.
     * @param audience The audience.
     * @param col The collection.
     */
    public static void send(Audience audience, Collection<?> col) {
        if (col.isEmpty())
            audience.sendMessage(text("[]", SEPARATOR));
        else {
            audience.sendMessage(text("[", SEPARATOR));
            for (var elem : col) {
                audience.sendMessage(
                    text("  ")
                            .append(text(""+elem, VALUE))
                );
            }
            audience.sendMessage(text("]", SEPARATOR));
        }
    }

    /**
     * Sends a configuration node.
     * @param audience The audience.
     * @param node The node.
     */
    public static void send(Audience audience, ConfigurationNode node) {
        for (var line : ConfigurationNodes.render(node, ConfigurationNodes.RenderOptions.DEFAULT, true)) {
            audience.sendMessage(line);
        }
    }
}
