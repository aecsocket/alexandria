package com.gitlab.aecsocket.minecommons.core.scheduler;

import com.gitlab.aecsocket.minecommons.core.Validation;

/**
 * A task that can be registered to a {@link Scheduler}. Defines timings.
 * @param action The action that this task runs.
 * @param delay The delay, in ms, to start this task.
 * @param interval The delay, in ms, between iterations of this task.
 */
public record Task(Action action, long delay, long interval) {
    /**
     * An action to run for a task.
     */
    @FunctionalInterface
    public interface Action {
        /**
         * Runs the action.
         * @param ctx The task context.
         */
        void run(TaskContext ctx);
    }

    /**
     * Creates an instance.
     * @param action The action that this task runs.
     * @param delay The delay, in ms, to start this task.
     * @param interval The delay, in ms, between iterations of this task.
     */
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
