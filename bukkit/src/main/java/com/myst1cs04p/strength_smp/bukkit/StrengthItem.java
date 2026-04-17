package com.myst1cs04p.strength_smp.bukkit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Factory and identity check for the Strength Token ItemStack.
 * Uses legacy string display names since Spigot's ItemMeta does not expose
 * Adventure Component methods — Paper does, but we need Spigot compatibility here.
 */
public final class StrengthItem {

    /** Sentinel value used to identify strength tokens. Must be stable. */
    private static final String DISPLAY_NAME = "\u00a7c\u00a7lStrength Token";

    private StrengthItem() {}

    public static ItemStack createStrengthToken() {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(DISPLAY_NAME);
        meta.setLore(List.of("\u00a7cRight-click to absorb strength."));
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isStrengthToken(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_POWDER) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && DISPLAY_NAME.equals(meta.getDisplayName());
    }
}