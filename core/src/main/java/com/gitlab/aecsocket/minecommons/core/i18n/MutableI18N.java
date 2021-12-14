package com.gitlab.aecsocket.minecommons.core.i18n;

import net.kyori.adventure.text.format.Style;

/**
 * An i18n service which can have elements registered to it.
 */
public interface MutableI18N extends I18N {
    /**
     * Clears all registrations.
     */
    void clear();

    /**
     * Registers a style.
     * @param key The key.
     * @param style The style.
     */
    void registerStyle(String key, Style style);

    /**
     * Registers a format.
     * @param key The key.
     * @param format The format.
     */
    void registerFormat(String key, Format format);

    /**
     * Registers a translation under the locale {@link Translation#locale()}.
     * @param translation The translation.
     */
    void registerTranslation(Translation translation);
}
