package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.bukkit.BukkitStrengthPlayer;
import com.myst1cs04p.strength_smp.bukkit.StrengthItem;
import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import com.myst1cs04p.strength_smp.common.messaging.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Handles strength token right-click consumption.
 */
public class StrengthItemListener implements Listener {

    private final StrengthEngine engine;

    public StrengthItemListener(StrengthEngine engine) {
        this.engine = engine;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        // Suppress off-hand double-fires
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (!StrengthItem.isStrengthToken(item)) return;

        Player player = event.getPlayer();
        BukkitStrengthPlayer wrapped = new BukkitStrengthPlayer(player);

        boolean consumed = engine.consumeToken(wrapped);

        if (consumed) {
            player.sendMessage(Messages.tokenAbsorbed(engine.getStrength(wrapped)));
            item.setAmount(item.getAmount() - 1);
        } else {
            player.sendMessage(Messages.alreadyMaxStrength());
        }
    }
}
