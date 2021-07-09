package com.gitlab.aecsocket.minecommons.core.translation;

import com.gitlab.aecsocket.minecommons.core.Validation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A translation, mapping a string key to a string array value.
 */
public class Translation extends HashMap<String, String> {
    /** The locale that this translation is for. */
    private final Locale locale;

    /**
     * Creates a translation.
     * @param locale The locale.
     */
    public Translation(Locale locale) {
        this.locale = locale;
    }

    /**
     * Creates a translation.
     * @param m The existing translations.
     * @param locale The locale.
     */
    public Translation(Map<? extends String, ? extends String> m, Locale locale) {
        super(m);
        Validation.notNull("locale", locale);
        this.locale = locale;
    }

    /**
     * Creates an empty translation map.
     * @return The translation map.
     */
    public static Translation empty() {
        return new Translation(Locale.ROOT);
    }

    /**
     * The locale that this translation is for.
     * @return The locale.
     */
    public Locale locale() { return locale; }
}
