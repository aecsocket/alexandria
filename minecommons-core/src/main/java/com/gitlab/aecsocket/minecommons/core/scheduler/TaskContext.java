package com.gitlab.aecsocket.minecommons.core.scheduler;

/**
 * The running context for a {@link Task}.
 */
public final class TaskContext {
    private final Scheduler scheduler;
    private final long elapsed;
    private final long delta;
    private final int iteration;
    private boolean cancelled;

    public TaskContext(Scheduler scheduler, long elapsed, long delta, int iteration) {
        this.scheduler = scheduler;
        this.elapsed = elapsed;
        this.delta = delta;
        this.iteration = iteration;
    }

    /**
     * The scheduler that is running the task.
     * @return The value.
     */
    public Scheduler scheduler() { return scheduler; }

    /**
     * The milliseconds elapsed since task start.
     * @return The value.
     */
    public long elapsed() { return elapsed; }

    /**
     * The milliseconds elapsed since the last iteration.
     * @return The value.
     */
    public long delta() { return delta; }

    /**
     * The iteration that this task is on.
     * @return The value.
     */
    public int iteration() { return iteration; }

    /**
     * Gets if this task has been cancelled.
     * @return The state.
     */
    public boolean cancelled() { return cancelled; }

    /**
     * Sets if this task has been cancelled.
     * @param cancelled The state.
     */
    public void cancelled(boolean cancelled) { this.cancelled = cancelled; }

    /**
     * Sets this task to be cancelled.
     */
    public void cancel() { cancelled = true; }

    /**
     * Runs another task using this context.
     * <p>
     * Does not affect the super-context's (this instance) cancelled status.
     * @param task The task to run.
     */
    public void run(Task task) {
        task.action().run(new TaskContext(scheduler, elapsed, delta, iteration));
    }
}
