package com.myst1cs04p.strength_smp.paper.scheduler;

import com.myst1cs04p.strength_smp.common.updater.VersionNotifier;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

/**
 * Paper-native async scheduler for the version check.
 * Replaces {@code BukkitUpdateScheduler} by using Paper's
 * {@code AsyncScheduler} which is not tied to the server tick thread.
 *
 * Fires immediately on start, then repeats every 12 hours.
 */
public class PaperUpdateScheduler {

    private static final long INTERVAL_HOURS = 12L;

    private final JavaPlugin plugin;
    private final VersionNotifier notifier;
    private Object task; 

    public PaperUpdateScheduler(JavaPlugin plugin, VersionNotifier notifier) {
        this.plugin = plugin;
        this.notifier = notifier;
    }

    /**
     * Start the repeating async check. Safe to call once from onEnable.
     */
    public void start() {
        task = plugin.getServer().getAsyncScheduler().runAtFixedRate(
            plugin,
            scheduledTask -> notifier.checkOnce(),
            0L,
            INTERVAL_HOURS,
            TimeUnit.HOURS
        );
    }

    /**
     * Cancel the task cleanly on plugin disable.
     */
    public void stop() {
        // task is a ScheduledTask but we avoid importing the type directly
        // to stay compatible with both Paper and non-Folia builds
        if (task != null) {
            try {
                task.getClass().getMethod("cancel").invoke(task);
            } catch (Exception ignored) {}
        }
    }
}