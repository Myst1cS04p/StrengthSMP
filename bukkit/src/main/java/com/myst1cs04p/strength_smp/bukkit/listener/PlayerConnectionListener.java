package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.bukkit.BukkitMain;
import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final StrengthEngine engine;
    private final BukkitMain plugin;

    public PlayerConnectionListener(StrengthEngine engine, BukkitMain plugin) {
        this.engine = engine;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        engine.load(plugin.wrap(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        engine.flushPlayer(plugin.wrap(event.getPlayer()));
    }
}