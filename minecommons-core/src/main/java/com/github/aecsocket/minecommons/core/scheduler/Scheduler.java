package com.github.aecsocket.minecommons.core.scheduler;

/**
 * An object which can queue and run {@link Task}s.
 */
public interface Scheduler {
    /**
     * Schedules and runs a task.
     * @param task The task.
     */
    void run(Task task);

    /**
     * Cancels all tasks.
     */
    void cancel();
}
