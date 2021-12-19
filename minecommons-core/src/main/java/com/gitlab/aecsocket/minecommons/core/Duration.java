package com.gitlab.aecsocket.minecommons.core;

import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A period of time specified in milliseconds.
 */
public record Duration(long ms) {
    private static final long second = 1000;
    private static final long minute = second * 60;
    private static final long hour = minute * 60;
    private static final long day = hour * 24;

    private static final Pattern pattern = Pattern.compile(
            "(?:([0-9]+)d(?:\\W+)?)?(?:([0-9]+)h(?:\\W+)?)?(?:([0-9]+)m(?:\\W+)?)?(?:([0-9]+(?:[.,][0-9]+)?)?s(?:\\W+)?)?",
            Pattern.CASE_INSENSITIVE);

    /**
     * Creates a duration from its millisecond value.
     * @param ms The milliseconds.
     * @return The duration.
     */
    public static Duration duration(long ms) {
        return new Duration(ms);
    }

    /**
     * Creates a duration from its individual time components.
     * @param days The days.
     * @param hours The hours.
     * @param minutes The minutes.
     * @param seconds The seconds.
     * @param ms The milliseconds.
     * @return The duration.
     */
    public static Duration duration(long days, long hours, long minutes, long seconds, long ms) {
        return new Duration(days*day + hours*hour + minutes*minute + seconds*second + ms);
    }

    /**
     * Parses a duration from a string, in the format {@code 1d2h3m4.5s}
     * @param str The string.
     * @return The duration.
     */
    public static Duration duration(String str) {
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid duration format: " + str);
        String m = matcher.group(1); long days = m == null ? 0 : Long.parseLong(m);
        m = matcher.group(2); long hours = m == null ? 0 : Long.parseLong(m);
        m = matcher.group(3); long minutes = m == null ? 0 : Long.parseLong(m);
        m = matcher.group(4); long ms = m == null ? 0 : (long) (Double.parseDouble(m) * 1000);
        return new Duration(days*day + hours*hour + minutes*minute + ms);
    }

    /**
     * The amount of exclusive milliseconds in this duration.
     * @return The value.
     */
    public long exclMs() { return ms % 1000; }

    /**
     * The amount of total seconds in this duration.
     * @return The value.
     */
    public long seconds() { return ms / 1000; }

    /**
     * The amount of total ticks in this duration.
     * @return The value.
     */
    public long ticks() { return ms / Ticks.MSPT; }

    /**
     * The amount of exclusive ticks in this duration.
     * @return The value.
     */
    public long exclTicks() { return ticks() % Ticks.TPS; }

    /**
     * The amount of exclusive seconds in this duration.
     * @return The value.
     */
    public long exclSeconds() { return seconds() % 60; }

    /**
     * The amount of total minutes in this duration.
     * @return The value.
     */
    public long minutes() { return ms / (1000 * 60); }

    /**
     * The amount of exclusive minutes in this duration.
     * @return The value.
     */
    public long exclMinutes() { return minutes() % 60; }

    /**
     * The amount of total hours in this duration.
     * @return The value.
     */
    public long hours() { return ms / (1000 * 60 * 60); }

    /**
     * The amount of exclusive hours in this duration.
     * @return The value.
     */
    public long exclHours() { return hours() % 24; }

    /**
     * The amount of total days in this duration.
     * @return The value.
     */
    public long days() { return ms / (1000 * 60 * 60 * 24); }

    /**
     * Returns this duration as a displayed string.
     * @param locale The locale to format numbers with.
     * @return The string form.
     */
    public String asString(Locale locale) {
        StringJoiner result = new StringJoiner(" ");
        long days = days();
        if (days > 0) result.add(String.format(locale, "%,dd", days));
        long hours = exclHours();
        if (hours > 0) result.add(String.format(locale, "%,dh", hours));
        long minutes = exclMinutes();
        if (minutes > 0) result.add(String.format(locale, "%,dm", minutes));
        long ms = this.ms % (1000 * 60);
        if (ms > 0 || result.length() == 0) result.add(String.format(locale, "%,.3fs", ms / 1000d));
        return result.toString();
    }

    @Override
    public String toString() { return asString(Locale.ROOT); }
}
