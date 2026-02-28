package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.common.engine.StrengthConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads config.yml and produces an immutable {@link StrengthConfig} for common.
 * All YAML API calls are isolated here.
 */
public class BukkitConfigLoader {

    private final JavaPlugin plugin;

    public BukkitConfigLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public StrengthConfig load() {
        plugin.reloadConfig();
        var cfg = plugin.getConfig();

        int min        = cfg.getInt("min-strength", -3);
        int max        = cfg.getInt("max-strength", 5);
        float mult     = (float) cfg.getDouble("damage-multiplier", 0.3);
        boolean enabled = cfg.getBoolean("strength-item.enabled", true);

        StrengthConfig.RecipeConfig recipe = loadRecipe(cfg.getConfigurationSection("strength-item.recipe"));

        return new StrengthConfig(min, max, mult, enabled, recipe);
    }

    private StrengthConfig.RecipeConfig loadRecipe(ConfigurationSection section) {
        if (section == null) {
            plugin.getLogger().warning("[StrengthSMP] strength-item.recipe section missing = using defaults.");
            return defaultRecipe();
        }

        String key = section.getString("key", "strength_token");

        List<String> shape = section.getStringList("shape");
        if (shape.size() != 3) {
            plugin.getLogger().warning("[StrengthSMP] Recipe shape invalid = using defaults.");
            return defaultRecipe();
        }

        Map<Character, String> ingredients = new HashMap<>();
        for (String row : shape) {
            for (char c : row.toCharArray()) {
                if (!Character.isWhitespace(c) && !ingredients.containsKey(c)) {
                    String matName = section.getString(String.valueOf(c));
                    if (matName != null) {
                        ingredients.put(c, matName);
                    } else {
                        plugin.getLogger().warning("[StrengthSMP] No material for recipe key '" + c + "'.");
                    }
                }
            }
        }

        return new StrengthConfig.RecipeConfig(key, shape, ingredients);
    }

    private StrengthConfig.RecipeConfig defaultRecipe() {
        return new StrengthConfig.RecipeConfig(
            "strength_token",
            Arrays.asList("ABA", "BCB", "ADA"),
            Map.of(
                'A', "DIAMOND",
                'B', "BLAZE_POWDER",
                'C', "NETHER_STAR",
                'D', "BLAZE_ROD"
            )
        );
    }
}
