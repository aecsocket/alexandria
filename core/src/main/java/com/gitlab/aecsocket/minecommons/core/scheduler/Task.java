package com.gitlab.aecsocket.minecommons.core.scheduler;

import com.gitlab.aecsocket.minecommons.core.Validation;

/**
 * A task that can be registered to a {@link Scheduler}. Defines timings.
 */
public record Task(Action action, long delay, long interval) {
    @FunctionalInterface
    public interface Action {
        void run(TaskContext ctx);
    }

    public Task {
        Validation.greaterThanEquals("delay", delay, 0);
    }

    /**
     * Creates a repeating task with a set interval and delay.
     * @param action The action to run.
     * @param interval The interval in milliseconds.
     * @param delay The delay.
     * @return The task.
     */
    public static Task repeating(Action action, long interval, long delay) {
        Validation.greaterThan("interval", interval, 0);
        return new Task(action, delay, interval);
    }

    /**
     * Creates a repeating task with a set interval.
     * @param action The action to run.
     * @param interval The interval in milliseconds.
     * @return The task.
     */
    public static Task repeating(Action action, long interval) {
        Validation.greaterThan("interval", interval, 0);
        return new Task(action, 0, interval);
    }

    /**
     * Creates a one-off task with a set delay.
     * @param action The action to run.
     * @param delay The delay in milliseconds.
     * @return The task.
     */
    public static Task single(Action action, long delay) {
        return new Task(action, delay, 0);
    }
}
