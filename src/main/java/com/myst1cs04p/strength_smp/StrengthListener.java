package com.myst1cs04p.strength_smp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class StrengthListener implements Listener {

    private final Main plugin;
    private final StrengthManager manager;

    public StrengthListener(Main plugin, StrengthManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Decrease victim's strength if above min
        int victimStrength = manager.getStrength(victim);

        // exit function if victim has no str left to give
        if(victimStrength <= manager.getMinStrength()){
            return;
        }

        manager.decreaseStrength(victim);

        // Increase killer's strength if below max
        int killerStrength = manager.getStrength(killer);
        if (killerStrength < manager.getMaxStrength()) {
            manager.increaseStrength(killer);
        } else {
            // Drop a "strength item" (can customize this)
            ItemStack strengthToken = new ItemStack(Material.BLAZE_POWDER, 1);
            killer.getWorld().dropItemNaturally(killer.getLocation(), strengthToken);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        int strength = manager.getStrength(damager);

        // Custom damage multiplier (e.g., +0.5 per strength)
        double baseDamage = event.getDamage();
        double newDamage = baseDamage + (strength * manager.getDamageMultiplier()); // Adjust scaling as needed
        event.setDamage(newDamage);
    }
}
