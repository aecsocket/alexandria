package com.gitlab.aecsocket.minecommons.core.color;

import org.checkerframework.common.value.qual.IntRange;

/**
 * A red, green, blue component provider.
 */
public interface RGB {
    /**
     * Gets the packed integer value.
     * @return The value.
     */
    int value();

    /**
     * Gets the red component.
     * @return The component.
     */
    default @IntRange(from = 0x0, to = 0xff) int r() { return (value() >> 16) & 0xff; }

    /**
     * Gets the green component.
     * @return The component.
     */
    default @IntRange(from = 0x0, to = 0xff) int g() { return (value() >> 8) & 0xff; }

    /**
     * Gets the blue component.
     * @return The component.
     */
    default @IntRange(from = 0x0, to = 0xff) int b() { return value() & 0xff; }

    /**
     * Gets the string representation of this as its packed integer format.
     * @return The formatted value.
     */
    default String asValue() { return Integer.toString(value()); }

    /**
     * Gets the string representation of this as its hex format.
     * @return The formatted value.
     */
    default String asHex() { return "#%6x".formatted(value()); }

    /**
     * Gets the string representation of this as its red, green, blue components.
     * @return The formatted value.
     */
    default String asRGB() { return "%d, %d, %d".formatted(r(), g(), b()); }

    /**
     * Creates a color from a packed integer value.
     * @param value The value.
     * @return The color.
     */
    static RGB of(int value) { return new RGBImpl(value); }

    /**
     * Creates a color from the red, green, blue components.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return The color.
     */
    static RGB of(
            @IntRange(from = 0x00, to = 0xff) int r,
            @IntRange(from = 0x00, to = 0xff) int g,
            @IntRange(from = 0x00, to = 0xff) int b
    ) {
        return of(
                (r & 0xff) << 16
                | (g & 0xff) << 8
                | (b & 0xff)
        );
    }
}
