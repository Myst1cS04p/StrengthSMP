package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.common.messaging.Messages;
import com.myst1cs04p.strength_smp.common.updater.VersionNotifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Sends a targeted update nag to any op or strength.admin player when they join,
 * if a newer version has already been cached by the {@link VersionNotifier}.
 *
 * No HTTP call happens here = we only read the in-memory cache, so this is free.
 */
public class AdminJoinListener implements Listener {

    private static final String ADMIN_PERMISSION = "strength.admin";

    private final VersionNotifier notifier;

    public AdminJoinListener(VersionNotifier notifier) {
        this.notifier = notifier;
    }

    @EventHandler
    public void onAdminJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission(ADMIN_PERMISSION) && !player.isOp()) return;
        if (!notifier.isUpdateCached()) return;

        player.sendMessage(Messages.updateNagJoin(
            notifier.getCurrentVersion(),
            notifier.getCachedLatestVersion()
        ));
    }
}
