package com.gitlab.aecsocket.minecommons.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Duration(long ms) {
    private static final long second = 1000;
    private static final long minute = second * 60;
    private static final long hour = minute * 60;
    private static final long day = hour * 24;

    private static final Pattern pattern = Pattern.compile(
            "(?:([0-9]+)d)?(?:([0-9]+)h)?(?:([0-9]+)m)?(?:([0-9]+(?:[.,][0-9]+)?)?s)?",
            Pattern.CASE_INSENSITIVE);

    public static Duration duration(long ms) {
        return new Duration(ms);
    }

    public static Duration duration(long days, long hours, long minutes, long seconds, long ms) {
        return new Duration(days*day + hours*hour + minutes*minute + seconds*second + ms);
    }

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

    public long exclMs() { return ms % 1000; }

    public long seconds() { return ms / 1000; }
    public long exclSeconds() { return seconds() % 60; }

    public long minutes() { return ms / (1000 * 60); }
    public long exclMinutes() { return minutes() % 60; }

    public long hours() { return ms / (1000 * 60 * 60); }
    public long exclHours() { return hours() % 24; }

    public long days() { return ms / (1000 * 60 * 60 * 24); }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        long days = days();
        if (days > 0) result.append(days).append("d");
        long hours = exclHours();
        if (hours > 0) result.append(hours).append("h");
        long minutes = exclMinutes();
        if (minutes > 0) result.append(minutes).append("m");
        long ms = this.ms % (1000 * 60);
        if (ms > 0 || result.isEmpty()) result.append(ms / 1000d).append("s");
        return result.toString();
    }
}
