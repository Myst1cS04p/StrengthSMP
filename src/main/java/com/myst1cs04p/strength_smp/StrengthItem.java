package com.myst1cs04p.strength_smp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StrengthItem {

    private static final Component DISPLAY_NAME = Component.text("Strength Token")
            .color(TextColor.color(255, 85, 85))
            .decoration(TextDecoration.BOLD, true);

    public static ItemStack createStrengthToken() {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return item;

        meta.displayName(DISPLAY_NAME);
        meta.lore(List.of(
                Component.text("Right-click to absorb strength.")
                        .color(TextColor.color(255, 0, 0))
                        .decoration(TextDecoration.ITALIC, false)));

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isStrengthToken(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_POWDER)
            return false;
        if (!item.hasItemMeta())
            return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName())
            return false;

        return meta.displayName().equals(DISPLAY_NAME);
    }
}
