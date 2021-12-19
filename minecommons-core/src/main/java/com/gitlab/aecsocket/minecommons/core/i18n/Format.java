package com.gitlab.aecsocket.minecommons.core.i18n;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/**
 * Styling options for a localized message.
 */
public sealed interface Format permits FormatImpl {
    /**
     * Gets the key of the style of the message.
     * @return The style.
     */
    @Nullable String style();

    /**
     * Gets the keys of the styles of placeholders in the message.
     * @return The template styles.
     */
    Map<String, String> templates();
}
