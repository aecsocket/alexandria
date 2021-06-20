package com.gitlab.aecsocket.minecommons.core.translation;

import com.gitlab.aecsocket.minecommons.core.Validation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A translation, mapping a string key to a string array value.
 */
public class Translation extends HashMap<String, String> {
    private final Locale locale;

    public Translation(int initialCapacity, float loadFactor, Locale locale) {
        super(initialCapacity, loadFactor);
        Validation.notNull(locale, "locale");
        this.locale = locale;
    }

    public Translation(int initialCapacity, Locale locale) {
        super(initialCapacity);
        Validation.notNull(locale, "locale");
        this.locale = locale;
    }

    public Translation(Locale locale) {
        this.locale = locale;
    }

    public Translation(Map<? extends String, ? extends String> m, Locale locale) {
        super(m);
        Validation.notNull(locale, "locale");
        this.locale = locale;
    }

    /**
     * The locale that this translation is for.
     * @return The locale.
     */
    public Locale locale() { return locale; }

    public static Translation empty() {
        return new Translation(null);
    }
}
