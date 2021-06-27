package com.gitlab.aecsocket.minecommons.core.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A scheduler which runs tasks using an {@link Executor}, on another thread.
 */
public class ThreadScheduler implements Scheduler {
    private final Executor executor;
    private final AtomicInteger cancelled = new AtomicInteger();

    public ThreadScheduler(Executor executor) {
        this.executor = executor;
    }

    public Executor executor() { return executor; }

    @Override
    public void run(Task task) {
        int lastCancelled = cancelled.get();
        executor.execute(() -> {
            try {
                long start = System.currentTimeMillis();
                if (task.delay() > 0) {
                    synchronized (this) { wait(task.delay()); }
                }
                int iteration = 0;
                long last = System.currentTimeMillis();
                while (true) {
                    if (cancelled.get() > lastCancelled)
                        break;
                    long time = System.currentTimeMillis();
                    TaskContext ctx = new TaskContext(this, time - start, time - last, iteration);
                    last = System.currentTimeMillis();
                    task.action().run(ctx);
                    if (ctx.cancelled() || task.interval() <= 0)
                        break;
                    synchronized (this) { wait(task.interval()); }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void cancel() {
        cancelled.incrementAndGet();
    }
}
