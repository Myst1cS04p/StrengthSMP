package com.myst1cs04p.strength_smp.bukkit.recipe;

import com.myst1cs04p.strength_smp.bukkit.StrengthItem;
import com.myst1cs04p.strength_smp.common.engine.StrengthConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Handles shaped recipe registration and removal for the Strength Token.
 * Isolated here so Main stays clean.
 */
public class RecipeRegistrar {

    private final JavaPlugin plugin;

    public RecipeRegistrar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register (or re-register) the strength token recipe using the current config.
     * Safe to call multiple times = removes the old recipe first.
     */
    public void register(StrengthConfig config) {
        if (!config.isStrengthItemEnabled()) return;

        StrengthConfig.RecipeConfig rc = config.getRecipe();
        if (rc == null) {
            plugin.getLogger().warning("[StrengthSMP] strength-item.recipe section missing in config.yml");
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, rc.getKey());

        // Remove existing recipe to allow hot-reload
        try {
            plugin.getServer().removeRecipe(key);
        } catch (Exception ignored) {}

        List<String> shape = rc.getShape();
        if (shape.size() != 3) {
            plugin.getLogger().warning("[StrengthSMP] Recipe shape must have exactly 3 rows.");
            return;
        }

        ItemStack result = StrengthItem.createStrengthToken();
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(shape.get(0), shape.get(1), shape.get(2));

        for (Map.Entry<Character, String> entry : rc.getIngredients().entrySet()) {
            char c = entry.getKey();
            String matName = entry.getValue();
            try {
                Material mat = Material.valueOf(matName.toUpperCase(Locale.ROOT));
                recipe.setIngredient(c, mat);
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("[StrengthSMP] Invalid material '" + matName + "' for recipe key '" + c + "'");
            }
        }

        boolean added = plugin.getServer().addRecipe(recipe);
        if (added) {
            plugin.getLogger().info("[StrengthSMP] Registered strength token recipe: " + rc.getKey());
        } else {
            plugin.getLogger().warning("[StrengthSMP] Failed to register strength token recipe.");
        }
    }
}
