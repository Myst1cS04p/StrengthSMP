package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.common.messaging.Messages;
import com.myst1cs04p.strength_smp.common.updater.VersionNotifier;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AdminJoinListener implements Listener {

    private static final String ADMIN_PERMISSION = "strength.admin";

    private final VersionNotifier notifier;
    private final BukkitAudiences audiences;

    public AdminJoinListener(VersionNotifier notifier, BukkitAudiences audiences) {
        this.notifier = notifier;
        this.audiences = audiences;
    }

    @EventHandler
    public void onAdminJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(ADMIN_PERMISSION) && !player.isOp()) return;
        if (!notifier.isUpdateCached()) return;

        audiences.player(player).sendMessage(Messages.updateNagJoin(
            notifier.getCurrentVersion(),
            notifier.getCachedLatestVersion()
        ));
    }
}