package com.gitlab.aecsocket.minecommons.core.vector.cartesian;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * An abstract vector which has numerical values.
 */
public interface NumericalVector {
    /** The default decimal format for {@link #asString(DecimalFormat)}. */
    DecimalFormat DEFAULT_FORMAT = new DecimalFormat("0.#####", DecimalFormatSymbols.getInstance(Locale.ROOT));

    /**
     * Formats this vector using a decimal format.
     * @param format The format.
     * @return The formatted string.
     */
    String asString(DecimalFormat format);

    /**
     * Formats this vector using {@link String#format(String, Object...)}.
     * @param locale The locale.
     * @param format The format.
     * @return The formatted string.
     */
    String asString(Locale locale, String format);
}
