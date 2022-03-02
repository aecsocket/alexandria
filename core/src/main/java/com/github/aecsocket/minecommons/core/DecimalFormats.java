package com.github.aecsocket.minecommons.core;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Global utility to get and cache {@link DecimalFormat}s for different {@link Locale}s.
 */
public final class DecimalFormats {
    private DecimalFormats() {}

    private static final Map<Locale, DecimalFormat> FORMATS = new HashMap<>();

    /**
     * Gets a formatter for the specified locale.
     * <p>
     * This will use the pattern {@code 0.###}.
     * @param locale The locale.
     * @return The formatter.
     */
    public static DecimalFormat formatter(Locale locale) {
        return FORMATS.computeIfAbsent(locale, k -> new DecimalFormat("0.###", DecimalFormatSymbols.getInstance(k)));
    }
}
