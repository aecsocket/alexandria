package com.gitlab.aecsocket.minecommons.core;

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
}
