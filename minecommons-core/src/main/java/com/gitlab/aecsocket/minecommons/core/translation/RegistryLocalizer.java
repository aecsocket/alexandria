package com.gitlab.aecsocket.minecommons.core.translation;


import java.util.Locale;

/**
 * A localizer which can have {@link Translation}s registered and unregistered from it.
 */
public interface RegistryLocalizer extends Localizer {
    /**
     * Gets a translation for a specific locale.
     * @param locale The locale.
     * @return The translation.
     */
    Translation translation(Locale locale);

    /**
     * Registers all of a translation's entries into this localizer.
     * @param translation The translation.
     */
    void register(Translation translation);

    /**
     * Unregisters all of a translation's keys from this localizer.
     * @param translation The translation.
     */
    void unregister(Translation translation);

    /**
     * Unregisters all translations for a specific locale.
     * @param locale The locale.
     */
    void unregister(Locale locale);
}
