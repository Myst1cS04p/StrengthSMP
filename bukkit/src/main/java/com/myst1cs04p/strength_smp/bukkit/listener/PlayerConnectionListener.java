package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.bukkit.BukkitStrengthPlayer;
import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Pre-loads player strength into the engine cache on join, and flushes on quit.
 * Ensures the cache is always warm before any other event fires on that player.
 * LOWEST priority on join so all subsequent listeners see a populated cache.
 */
public class PlayerConnectionListener implements Listener {

    private final StrengthEngine engine;

    public PlayerConnectionListener(StrengthEngine engine) {
        this.engine = engine;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        engine.load(new BukkitStrengthPlayer(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        // Per-quit flush means data survives crashes between sessions.
        engine.flushPlayer(new BukkitStrengthPlayer(event.getPlayer()));
    }
}