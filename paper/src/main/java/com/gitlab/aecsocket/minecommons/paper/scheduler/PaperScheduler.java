package com.gitlab.aecsocket.minecommons.paper.scheduler;

import com.gitlab.aecsocket.minecommons.core.Ticks;
import com.gitlab.aecsocket.minecommons.core.scheduler.Scheduler;
import com.gitlab.aecsocket.minecommons.core.scheduler.Task;
import com.gitlab.aecsocket.minecommons.core.scheduler.TaskContext;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A scheduler which uses a {@link org.bukkit.scheduler.BukkitScheduler} to run tasks.
 */
public final class PaperScheduler implements Scheduler {
    private final Plugin plugin;
    private final List<Integer> tasks = new ArrayList<>();

    public PaperScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin plugin() { return plugin; }

    private void schedule(Runnable runnable, long delay) {
        AtomicInteger id = new AtomicInteger(0);
        id.set(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            runnable.run();
            tasks.remove((Object) id.get());
        }, delay));
        tasks.add(id.get());
    }

    private void runnable(Task task, long start, long last, long interval, int iteration) {
        long time = System.currentTimeMillis();
        TaskContext ctx = new TaskContext(this, time - start, time - last, iteration);
        task.action().run(ctx);
        if (!ctx.cancelled() && interval > 0) {
            schedule(() -> runnable(task, start, time, interval, iteration + 1), Ticks.ticks(task.interval()));
        }
    }

    @Override
    public void run(Task task) {
        long start = System.currentTimeMillis();
        schedule(() -> runnable(task, start, start, Ticks.ticks(task.interval()), 0), Ticks.ticks(task.interval()));
    }

    @Override
    public void cancel() {
        tasks.forEach(Bukkit.getScheduler()::cancelTask);
        tasks.clear();
    }
}
