package com.github.aecsocket.minecommons.core;

/**
 * Utilities for numbers.
 */
public final class Numbers {
    private Numbers() {}

    /**
     * Shorthand for squaring a number, equivalent to {@code v * v}.
     * @param v The value.
     * @return The squared value.
     */
    public static double sqr(double v) { return v * v; }

    /**
     * Checks if a value is between a minimum and a maximum, inclusive.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The result.
     */
    public static boolean in(int v, int min, int max) { return v >= min && v <= max; }

    /**
     * Checks if a value is between a minimum and a maximum, inclusive.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The result.
     */
    public static boolean in(long v, long min, long max) { return v >= min && v <= max; }

    /**
     * Checks if a value is between a minimum and a maximum, inclusive.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The result.
     */
    public static boolean in(float v, float min, float max) { return v >= min && v <= max; }

    /**
     * Checks if a value is between a minimum and a maximum, inclusive.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The result.
     */
    public static boolean in(double v, double min, double max) { return v >= min && v <= max; }

    /**
     * Clamps a value between the minimum and maximum.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The clamped value.
     */
    public static int clamp(int v, int min, int max) {
        return Math.min(max, Math.max(min, v));
    }

    /**
     * Clamps a value between the minimum and maximum.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The clamped value.
     */
    public static long clamp(long v, long min, long max) {
        return Math.min(max, Math.max(min, v));
    }

    /**
     * Clamps a value between the minimum and maximum.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The clamped value.
     */
    public static float clamp(float v, float min, float max) {
        return Math.min(max, Math.max(min, v));
    }

    /**
     * Clamps a value between the minimum and maximum.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The clamped value.
     */
    public static double clamp(double v, double min, double max) {
        return Math.min(max, Math.max(min, v));
    }


    /**
     * Clamps a value between 0 and 1.
     * @param v The value.
     * @return The clamped value.
     */
    public static int clamp01(int v) { return clamp(v, 0, 1); }

    /**
     * Clamps a value between 0 and 1.
     * @param v The value.
     * @return The clamped value.
     */
    public static long clamp01(long v) { return clamp(v, 0, 1); }

    /**
     * Clamps a value between 0 and 1.
     * @param v The value.
     * @return The clamped value.
     */
    public static float clamp01(float v) { return clamp(v, 0, 1); }

    /**
     * Clamps a value between 0 and 1.
     * @param v The value.
     * @return The clamped value.
     */
    public static double clamp01(double v) { return clamp(v, 0, 1); }

    /**
     * Wraps a value around a minimum and a maximum, doing over/underflow.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The wrapped value.
     */
    public static int wrap(int v, int min, int max) {
        v = ((v - min) % (max - min + 1));
        return (v < 0 ? max : min) + v;
    }

    /**
     * Wraps a value around a minimum and a maximum, doing over/underflow.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The wrapped value.
     */
    public static long wrap(long v, long min, long max) {
        v = ((v - min) % (max - min + 1));
        return (v < 0 ? max : min) + v;
    }

    /**
     * Wraps a value around a minimum and a maximum, doing over/underflow.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The wrapped value.
     */
    public static float wrap(float v, float min, float max) {
        v = ((v - min) % (max - min + 1));
        return (v < 0 ? max : min) + v;
    }
    /**
     * Wraps a value around a minimum and a maximum, doing over/underflow.
     * @param v The value.
     * @param min The minimum.
     * @param max The maximum.
     * @return The wrapped value.
     */
    public static double wrap(double v, double min, double max) {
        v = ((v - min) % (max - min + 1));
        return (v < 0 ? max : min) + v;
    }

    /**
     * Linearly interpolates between two values, by a factor, such that
     * where f = 0, return value = a.
     * @param a Value one.
     * @param b Value two.
     * @param f The factor.
     * @return The interpolated value.
     */
    public static int lerp(int a, int b, int f) {
        return a + (b - a) * f;
    }

    /**
     * Linearly interpolates between two values, by a factor, such that
     * where f = 0, return value = a.
     * @param a Value one.
     * @param b Value two.
     * @param f The factor.
     * @return The interpolated value.
     */
    public static long lerp(long a, long b, long f) {
        return a + (b - a) * f;
    }

    /**
     * Linearly interpolates between two values, by a factor, such that
     * where f = 0, return value = a.
     * @param a Value one.
     * @param b Value two.
     * @param f The factor.
     * @return The interpolated value.
     */
    public static float lerp(float a, float b, float f) {
        return a + (b - a) * f;
    }

    /**
     * Linearly interpolates between two values, by a factor, such that
     * where f = 0, return value = a.
     * @param a Value one.
     * @param b Value two.
     * @param f The factor.
     * @return The interpolated value.
     */
    public static double lerp(double a, double b, double f) {
        return a + (b - a) * f;
    }
}
