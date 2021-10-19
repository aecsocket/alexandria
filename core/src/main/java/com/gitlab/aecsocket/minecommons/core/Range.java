package com.gitlab.aecsocket.minecommons.core;

/**
 * An object representing a range between two values, with a minimum and a maximum.
 */
public sealed interface Range
    permits Range.Integer, Range.Long, Range.Float, Range.Double {
    /**
     * Creates an integer range.
     * @param min The minimum.
     * @param max The maximum.
     * @return The range.
     */
    static Integer ofInteger(int min, int max) { return new Integer(min, max); }

    /**
     * A range of two integers.
     */
    record Integer(int min, int max) implements Range {}

    /**
     * Creates a long range.
     * @param min The minimum.
     * @param max The maximum.
     * @return The range.
     */
    static Long ofLong(long min, long max) { return new Long(min, max); }

    /**
     * A range of two longs.
     */
    record Long(long min, long max) implements Range {}

    /**
     * Creates a float range.
     * @param min The minimum.
     * @param max The maximum.
     * @return The range.
     */
    static Float ofFloat(float min, float max) { return new Float(min, max); }

    /**
     * A range of two floats.
     */
    record Float(float min, float max) implements Range {}

    /**
     * Creates a double range.
     * @param min The minimum.
     * @param max The maximum.
     * @return The range.
     */
    static Double ofDouble(double min, double max) { return new Double(min, max); }

    /**
     * A range of two doubles.
     */
    record Double(double min, double max) implements Range {}
}
