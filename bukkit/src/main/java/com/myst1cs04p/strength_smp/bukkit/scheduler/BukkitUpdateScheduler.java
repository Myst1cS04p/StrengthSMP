package com.myst1cs04p.strength_smp.bukkit.scheduler;

import com.myst1cs04p.strength_smp.common.updater.VersionNotifier;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Schedules the version check using Bukkit's repeating task.
 * Fires once on startup and then every 12 hours.
 *
 * Paper overrides this with {@code PaperUpdateScheduler} which uses
 * Paper's async scheduler instead.
 */
public class BukkitUpdateScheduler {

    /** 12 hours in ticks: 12 * 60 * 60 * 20 = 864_000 */
    private static final long INTERVAL_TICKS = 864_000L;

    private final JavaPlugin plugin;
    private final VersionNotifier notifier;

    public BukkitUpdateScheduler(JavaPlugin plugin, VersionNotifier notifier) {
        this.plugin = plugin;
        this.notifier = notifier;
    }

    /**
     * Start the repeating check. Safe to call once from onEnable.
     */
    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                notifier.checkOnce();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, INTERVAL_TICKS);
    }
}
