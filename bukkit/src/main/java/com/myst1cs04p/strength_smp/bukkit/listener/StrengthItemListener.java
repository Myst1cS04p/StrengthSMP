package com.myst1cs04p.strength_smp.bukkit.listener;

import com.myst1cs04p.strength_smp.bukkit.BukkitMain;
import com.myst1cs04p.strength_smp.bukkit.BukkitStrengthPlayer;
import com.myst1cs04p.strength_smp.bukkit.StrengthItem;
import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import com.myst1cs04p.strength_smp.common.messaging.Messages;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class StrengthItemListener implements Listener {

    private final StrengthEngine engine;
    private final BukkitMain plugin;

    public StrengthItemListener(StrengthEngine engine, BukkitMain plugin) {
        this.engine = engine;
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (!StrengthItem.isStrengthToken(item)) return;

        Player player = event.getPlayer();
        BukkitStrengthPlayer wrapped = plugin.wrap(player);
        BukkitAudiences audiences = plugin.audiences;

        boolean consumed = engine.consumeToken(wrapped);
        if (consumed) {
            audiences.player(player).sendMessage(Messages.tokenAbsorbed(engine.getStrength(wrapped)));
            item.setAmount(item.getAmount() - 1);
        } else {
            audiences.player(player).sendMessage(Messages.alreadyMaxStrength());
        }
    }
}