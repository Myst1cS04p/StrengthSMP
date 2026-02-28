package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.bukkit.BukkitStrengthPlayer;
import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listens for player deaths and delegates kill logic to {@link StrengthEngine}.
 */
public class StrengthListener implements Listener {

    private final StrengthEngine engine;

    public StrengthListener(StrengthEngine engine) {
        this.engine = engine;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;

        Player victim = event.getEntity();

        engine.handleKill(
            new BukkitStrengthPlayer(killer),
            new BukkitStrengthPlayer(victim)
        );
    }
}
