package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.bukkit.BukkitMain;
import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StrengthListener implements Listener {

    private final StrengthEngine engine;
    private final BukkitMain plugin;

    public StrengthListener(StrengthEngine engine, BukkitMain plugin) {
        this.engine = engine;
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(victim.getKiller() instanceof Player killer)) return;
        engine.handleKill(plugin.wrap(killer), plugin.wrap(victim));
    }
}