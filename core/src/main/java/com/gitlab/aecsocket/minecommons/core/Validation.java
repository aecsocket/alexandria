package com.gitlab.aecsocket.minecommons.core;

/**
 * Utilities for validating conditions.
 */
public final class Validation {
    private Validation() {}

    /**
     * Errors if an expression is true.
     * @param expr The expression.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void assertNot(boolean expr, E thrown) throws E {
        if (expr) throw thrown;
    }

    /**
     * Errors if an expression is true.
     * @param expr The expression.
     * @param message The message to error with.
     */
    public static void assertNot(boolean expr, String message) {
        assertNot(expr, new IllegalArgumentException(message));
    }

    /**
     * Errors if an expression is false.
     * @param expr The expression.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void assertIs(boolean expr, E thrown) throws E {
        if (!expr) throw thrown;
    }

    /**
     * Errors if an expression is false.
     * @param message The message to error with.
     * @param expr The expression.
     */
    public static void assertIs(String message, boolean expr) {
        assertIs(expr, new IllegalArgumentException(message));
    }

    /**
     * Errors if an object is null.
     * @param obj The object.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void notNull(Object obj, E thrown) throws E {
        if (obj == null) throw thrown;
    }

    /**
     * Errors if an object is null.
     * <p>
     * Throws a NullPointerException.
     * @param obj The object.
     * @param name The name of the argument.
     */
    public static void notNull(String name, Object obj) {
        notNull(obj, new NullPointerException(name));
    }

    /**
     * Errors if a number is not greater than another value.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void greaterThan(double actual, double target, E thrown) throws E {
        if (!(actual > target)) throw thrown;
    }

    /**
     * Errors if a number is not greater than another value.
     * <p>
     * Throws {@code [name]: failed condition `[actual] > [target]`}.
     * @param name The name of the argument.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     */
    public static void greaterThan(String name, double actual, double target) {
        greaterThan(actual, target, new IllegalArgumentException("%s: failed condition `%f > %f`".formatted(name, actual, target)));
    }

    /**
     * Errors if a number is not greater than or equal to another value.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void greaterThanEquals(double actual, double target, E thrown) throws E {
        if (!(actual >= target)) throw thrown;
    }

    /**
     * Errors if a number is not greater than or equal to another value.
     * <p>
     * Throws {@code [name]: failed condition `[actual] >= [target]`}.
     * @param name The name of the argument.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     */
    public static void greaterThanEquals(String name, double actual, double target) {
        greaterThanEquals(actual, target, new IllegalArgumentException("%s: failed condition `%f >= %f`".formatted(name, actual, target)));
    }

    /**
     * Errors if a number is not lower than another value.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void lowerThan(double actual, double target, E thrown) throws E {
        if (!(actual < target)) throw thrown;
    }

    /**
     * Errors if a number is not lower than another value.
     * <p>
     * Throws {@code [name]: failed condition `[actual] < [target]`}.
     * @param name The name of the argument.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     */
    public static void lowerThan(String name, double actual, double target) {
        lowerThan(actual, target, new IllegalArgumentException("%s: failed condition `%f < %f`".formatted(name, actual, target)));
    }

    /**
     * Errors if a number is not lower than or equal to another value.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void lowerThanEquals(double actual, double target, E thrown) throws E {
        if (!(actual <= target)) throw thrown;
    }

    /**
     * Errors if a number is not lower than or equal to another value.
     * <p>
     * Throws {@code [name]: failed condition `[actual] <= [target]`}.
     * @param name The name of the argument.
     * @param actual The actual number provided.
     * @param target The target number that the actual number must be compared against.
     */
    public static void lowerThanEquals(String name, double actual, double target) {
        lowerThanEquals(actual, target, new IllegalArgumentException("%s: failed condition `%f <= %f`".formatted(name, actual, target)));
    }

    /**
     * Errors if a number is not between two values, inclusive.
     * @param actual The actual number provided.
     * @param min The minimum value.
     * @param max The maximum value.
     * @param thrown The exception to throw.
     * @param <E> The exception type.
     * @throws E The exception to throw.
     */
    public static <E extends Throwable> void in(double actual, double min, double max, E thrown) throws E {
        if (!Numbers.in(actual, min, max)) throw thrown;
    }

    /**
     * Errors if a number is not between two values, inclusive.
     * <p>
     * Throws {@code [name]: failed condition `[min] <= [actual] <= [max]`}.
     * @param name The name of the argument.
     * @param actual The actual number provided.
     * @param min The minimum value.
     * @param max The maximum value.
     */
    public static void in(String name, double actual, double min, double max) {
        in(actual, min, max, new IllegalArgumentException("%s: failed condition `%f <= %f <= %f`".formatted(name, min, actual, max)));
    }
}
