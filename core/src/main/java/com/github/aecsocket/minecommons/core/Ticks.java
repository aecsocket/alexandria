package com.github.aecsocket.minecommons.core;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

/**
 * Utilities for Minecraft time, measured in ticks.
 */
public final class Ticks {
    private Ticks() {}

    /** Ticks per second. */
    public static final int TPS = 20;
    /** Milliseconds per tick. */
    public static final int MSPT = 50;
    /** Ticks per Minecraft day. */
    public static final int PER_DAY = 24000;

    private record TimeFormat(Pattern pattern, long multiplier) {
        private static TimeFormat of(String pattern, long multiplier) {
            return new TimeFormat(Pattern.compile(pattern), multiplier);
        }
    }

    private static final List<TimeFormat> formats = ImmutableList.<TimeFormat>builder()
        .add(TimeFormat.of("([0-9.]+)(s)", TPS))
        .add(TimeFormat.of("([0-9.]+)(m)", 60L * TPS))
        .add(TimeFormat.of("([0-9.]+)(h)", 60L * 60L * TPS))
        .add(TimeFormat.of("([0-9.]+)(d)", 60L * 60L * 24L * TPS))
        .build();

    /**
     * Converts ticks to milliseconds.
     * @param ticks The ticks.
     * @return The milliseconds.
     */
    public static long ms(long ticks) { return ticks * MSPT; }

    /**
     * Converts milliseconds to ticks.
     * @param ms The milliseconds.
     * @return The ticks.
     */
    public static long ticks(long ms) { return ms / MSPT; }

    /**
     * Converts a string sequence into ticks, using the patterns:
     *
     * <table class="striped">
     * <caption>Time formats</caption>
     *  <thead>
     *  <tr>
     *    <th scope="col">Name</th>
     *    <th scope="col">Pattern</th>
     *    <th scope="col">Multiplier</th>
     *  </tr>
     *  </thead>
     *  <tbody>
     *  <tr>
     *    <th scope="row">Seconds</th>
     *    <td>{@code ([0-9.]+)(s)}</td>
     *    <td>{@code TPS}</td>
     *  </tr>
     *  <tr>
     *    <th scope="row">Minutes</th>
     *    <td>{@code ([0-9.]+)(m)}</td>
     *    <td>{@code 60 * TPS}</td>
     *  </tr>
     *  <tr>
     *    <th scope="row">Hours</th>
     *    <td>{@code ([0-9.]+)(h)}</td>
     *    <td>{@code 60 * 60 * TPS}</td>
     *  </tr>
     *  <tr>
     *    <th scope="row">Days</th>
     *    <td>{@code ([0-9.]+)(d)}</td>
     *    <td>{@code 60 * 60 * 24 * TPS}</td>
     *  </tr>
     *  </tbody>
     * </table>
     * @param text The input.
     * @return The ticks.
     * @throws NumberFormatException If a number could not be parsed.
     */
    public static long ticks(String text) throws NumberFormatException {
        for (TimeFormat format : formats) {
            Matcher match = format.pattern.matcher(text);
            if (match.find()) {
                return (long) (Double.parseDouble(match.group(1)) * format.multiplier);
            }
        }
        return (long) Double.parseDouble(text);
    }
}
