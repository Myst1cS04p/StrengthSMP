import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.RGBLike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrengthItem {

    public static ItemStack createStrengthToken() {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right Click to consume")
        .decoration(TextDecoration.BOLD, true)
        .color(TextColor.color(255, 0, 0)));

        if (meta != null) {
            meta.displayName(Component.text("Strength"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isStrengthToken(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_POWDER)
            return false;
        if (!item.hasItemMeta())
            return false;

        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.displayName().equals("Strength");
    }
}
