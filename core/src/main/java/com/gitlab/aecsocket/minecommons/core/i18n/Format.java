package com.gitlab.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/**
 * Styling options for a localized message.
 */
public sealed interface Format permits FormatImpl {
    /**
     * Gets the style of the message.
     * @return The style.
     */
    @Nullable Style style();

    /**
     * Gets the style of placeholders in the message.
     * @return The template styles.
     */
    Map<String, Style> templates();
}
