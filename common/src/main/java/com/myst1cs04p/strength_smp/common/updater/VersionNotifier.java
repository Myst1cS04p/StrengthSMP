package com.myst1cs04p.strength_smp.common.updater;

import com.myst1cs04p.strength_smp.common.messaging.Messages;
import com.myst1cs04p.strength_smp.common.platform.StrengthPlatform;

import java.util.logging.Logger;

/**
 * Platform-agnostic version notifier.
 *
 * The scheduler (BukkitRunnable or Paper async scheduler) lives in the platform
 * layer and calls {@link #checkOnce()} on its interval. This class only holds
 * the check logic and notification dispatch.
 *
 * On an update being found:
 *  - A loud banner is logged to console.
 *  - The update Component is broadcast to ALL online players (maximally annoying).
 *  - Admins also get a per-join nag via {@link AdminJoinNotifier} in bukkit.
 */
public class VersionNotifier {

    private final VersionChecker checker;
    private final String currentVersion;
    private final StrengthPlatform platform;
    private final Logger logger;

    public VersionNotifier(Logger logger, String owner, String repo, String currentVersion, StrengthPlatform platform) {
        this.checker = new VersionChecker(logger, owner, repo);
        this.currentVersion = currentVersion;
        this.platform = platform;
        this.logger = logger;
    }

    /**
     * Perform one version check asynchronously.
     * Call this from whatever repeating scheduler the platform provides.
     * Safe to call from any thread - the HTTP fetch is already async.
     */
    public void checkOnce() {
        checker.fetchLatestVersion().thenAccept(latest -> {
            if (latest == null) return;
            if (!checker.isNewerVersion(latest, currentVersion)) return;

            // Console banner - plain text since Logger doesn't do Components
            logger.warning("========================================");
            logger.warning("  StrengthSMP is OUT OF DATE!");
            logger.warning("  Running: " + currentVersion + "  ->  Latest: " + latest);
            logger.warning("  Update at: https://github.com/Myst1cS04p/StrengthSMP/releases");
            logger.warning("========================================");

            // Broadcast rich Component to every online player
            platform.broadcastMessage(Messages.updateAvailable(currentVersion, latest));
        });
    }

    /**
     * Convenience method for the AdminJoinNotifier - checks if there is a
     * cached newer version without triggering a fresh HTTP call.
     */
    public boolean isUpdateCached() {
        return checker.isNewerVersion(checker.getCachedLatestVersion(), currentVersion);
    }

    public String getCachedLatestVersion() {
        return checker.getCachedLatestVersion();
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public VersionChecker getChecker() {
        return checker;
    }
}
