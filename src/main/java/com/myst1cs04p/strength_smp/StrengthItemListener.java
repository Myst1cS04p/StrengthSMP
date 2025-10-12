package com.myst1cs04p.strength_smp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.block.Action;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class StrengthItemListener implements Listener {

    private final StrengthManager manager;

    public StrengthItemListener(StrengthManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return; // prevent off-hand double fires

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = event.getItem();
        if (!StrengthItem.isStrengthToken(item))
            return;

        Player player = event.getPlayer();

        int current = manager.getStrength(player);
        if (current >= manager.getMaxStrength()) {
            player.sendMessage(Component.text("You're already at max strength!", NamedTextColor.RED));
            return;
        }

        manager.increaseStrength(player);
        player.sendMessage(
            Component.text("You absorbed strength! Your level is now ", NamedTextColor.GREEN)
            .append(Component.text(manager.getStrength(player), NamedTextColor.GOLD)));

        // Consume 1 token
        item.setAmount(item.getAmount() - 1);
    }
}
