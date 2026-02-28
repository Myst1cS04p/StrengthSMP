package com.myst1cs04p.strength_smp.paper;

import com.myst1cs04p.strength_smp.bukkit.BukkitMain;
import com.myst1cs04p.strength_smp.paper.scheduler.PaperUpdateScheduler;

/**
 * Paper entry point. Extends {@link BukkitMain} and overrides only the parts
 * where Paper provides a better alternative.
 *
 * Current Paper-specific overrides:
 *  - {@link #startUpdateScheduler()} -> uses Paper's AsyncScheduler instead of BukkitRunnable.
 *
 * Everything else (engine, storage, metrics, listeners, commands) is inherited
 * from BukkitMain unchanged until there's a Paper-specific reason to override it.
 */
public class PaperMain extends BukkitMain {

    private PaperUpdateScheduler paperScheduler;

    /**
     * Override the scheduler hook to use Paper's async scheduler.
     * Called by {@code BukkitMain#onEnable()} after {@code versionNotifier} is initialized.
     */
    @Override
    protected void startUpdateScheduler() {
        paperScheduler = new PaperUpdateScheduler(this, versionNotifier);
        paperScheduler.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (paperScheduler != null) {
            paperScheduler.stop();
        }
    }
}
