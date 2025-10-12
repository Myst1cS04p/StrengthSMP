package com.myst1cs04p.strength_smp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private StrengthManager strengthManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        strengthManager = new StrengthManager(this);
        strengthManager.loadStrength();

        StrengthCommand strengthCommand = new StrengthCommand(this, strengthManager);
        if (getCommand("strength") != null) {
            getCommand("strength").setExecutor(strengthCommand);
            getCommand("strength").setTabCompleter(strengthCommand);
        }

        getServer().getPluginManager().registerEvents(new StrengthListener(this, strengthManager), this);
        getServer().getPluginManager().registerEvents(new StrengthItemListener(strengthManager), this);

        getLogger().info("\n\u001B[31m" + 
                        "███████╗████████╗██████╗ ███████╗███╗   ██╗ ██████╗████████╗██╗  ██╗\r\n" + //
                        "██╔════╝╚══██╔══╝██╔══██╗██╔════╝████╗  ██║██╔════╝╚══██╔══╝██║  ██║\r\n" + //
                        "███████╗   ██║   ██████╔╝█████╗  ██╔██╗ ██║██║  ███╗  ██║   ███████║\r\n" + //
                        "╚════██║   ██║   ██╔══██╗██╔══╝  ██║╚██╗██║██║   ██║  ██║   ██╔══██║\r\n" + //
                        "███████║   ██║   ██║  ██║███████╗██║ ╚████║╚██████╔╝  ██║   ██║  ██║\r\n" + //
                        "╚══════╝   ╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝   ╚═╝   ╚═╝  ╚═╝\r\n" + //
                        "                                                                    \r\n" + //
                        "                    ███████╗███╗   ███╗██████╗                      \r\n" + //
                        "                    ██╔════╝████╗ ████║██╔══██╗                     \r\n" + //
                        "                    ███████╗██╔████╔██║██████╔╝                     \r\n" + //
                        "                    ╚════██║██║╚██╔╝██║██╔═══╝                      \r\n" + //
                        "                    ███████║██║ ╚═╝ ██║██║                          \r\n" + //
                        "                    ╚══════╝╚═╝     ╚═╝╚═╝                          \r\n" + //
                        "                                                                    \u001B[0m");
        getLogger().info("\n\u001B[36m"+
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXX0occccccccccckXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.           cKXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.           cKXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.           cKXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.           cKXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.           cKXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.           ,oddddddddddkKXXXXXXXXXXXXXXXXXXXXXXXXKkdddddddddddddddddddddddxKXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.                       .kNXXXXXXXXXXXXXXXXXXXXXXNk.                       .kXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.                       .kNXXXXXXXXXXXXXXXXXXXXXXXk.                       .kXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.                       .kXXXXXXXXXXXXXXXXXXXXXXXXk.                       .kXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.                       .kXXXXXXXXXXXXXXXXXXXXXXXXk.                       .kXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk.                       .kXXXXXXXXXXXXXXXXXXXXXXXXk.                       .kXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXk,...........            .kXXXXXXXXXXXXXXXXXXXXXXXNk,.......................,OXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXX0OOOOOOOOOOk;           .kXXXXXXXXXXXXXXXXXXXXXXXXX0OOOOOOOOOOOOOOOOOOOOOOO0XXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXKc           .kXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXKc           .kXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXKc           .kXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXKc           .kXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXKc           'kXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXOdddddddddddkKXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\r\n" + //
                        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX \u001B[0m");
        getLogger().info("\n\u001B[33m"+
                "╻ ╻┏━╸┏━┓     ╺┳╸╻ ╻╻┏━┓   ╻ ╻┏━┓┏━┓\r\n" + //
                "┗┳┛┣╸ ┗━┓      ┃ ┣━┫┃┗━┓   ┃╻┃┣━┫┗━┓\r\n" + //
                " ╹ ┗━╸┗━┛ ┛    ╹ ╹ ╹╹┗━┛   ┗┻┛╹ ╹┗━┛\r\n" + //
                "┏━┓┏┓ ┏━┓┏━┓╻  ╻ ╻╺┳╸┏━╸╻  ╻ ╻      \r\n" + //
                "┣━┫┣┻┓┗━┓┃ ┃┃  ┃ ┃ ┃ ┣╸ ┃  ┗┳┛      \r\n" + //
                "╹ ╹┗━┛┗━┛┗━┛┗━╸┗━┛ ╹ ┗━╸┗━╸ ╹       \r\n" + //
                "┏┓╻┏━╸┏━╸┏━╸┏━┓┏━┓┏━┓┏━┓╻ ╻         \r\n" + //
                "┃┗┫┣╸ ┃  ┣╸ ┗━┓┗━┓┣━┫┣┳┛┗┳┛         \r\n" + //
                "╹ ╹┗━╸┗━╸┗━╸┗━┛┗━┛╹ ╹╹┗╸ ╹          \u001B[0m");

        registerRecipe(); // new part
    }

    @Override
    public void onDisable() {
        if (strengthManager != null)
            strengthManager.saveAll();
    }

    public void registerRecipe() {
    if (!getConfig().getBoolean("strength-item.enabled", true)) return;

    var itemSection = getConfig().getConfigurationSection("strength-item");
    if (itemSection == null) return;

    var recipeSection = itemSection.getConfigurationSection("recipe");
    if (recipeSection == null) {
        getLogger().warning("strength-item.recipe section missing in config.yml");
        return;
    }

    String keyName = recipeSection.getString("key", "strength_token");
    NamespacedKey nsKey = new NamespacedKey(this, keyName);

    // Result must be the token with metadata so crafted items are correct
    ItemStack result = StrengthItem.createStrengthToken();

    // Read shape from config (preferred). If not present, default to ABC/DEF/GHI
    List<String> shape = recipeSection.getStringList("shape");
    if (shape == null || shape.size() != 3) {
        shape = Arrays.asList("ABC", "DEF", "GHI");
    }

    // Create shaped recipe
    ShapedRecipe recipe = new ShapedRecipe(nsKey, result);
    recipe.shape(shape.get(0), shape.get(1), shape.get(2));

    // Gather unique chars used in shape
    Set<Character> used = new HashSet<>();
    for (String row : shape) {
        for (char c : row.toCharArray()) {
            if (!Character.isWhitespace(c)) used.add(c);
        }
    }

    // Map each used char to a material from config (e.g., A: DIAMOND)
    for (char c : used) {
        String key = String.valueOf(c);
        String matName = recipeSection.getString(key);
        if (matName == null) {
            getLogger().warning("No material configured for recipe key '" + key + "'. Recipe won't register fully.");
            continue;
        }
        try {
            Material mat = Material.valueOf(matName.toUpperCase(Locale.ROOT));
            recipe.setIngredient(c, mat);
        } catch (IllegalArgumentException ex) {
            getLogger().warning("Invalid material '" + matName + "' for recipe key '" + key + "'");
        }
    }

    // Remove previous recipe with same key if present, then add
    try {
        // removeRecipe exists on Bukkit; it's OK if nothing was removed
        getServer().removeRecipe(nsKey);
    } catch (Exception e) {
        // ignore if server implementation doesn't support removal in some edge cases
    }

    boolean added = getServer().addRecipe(recipe);
    if (!added) {
        getLogger().warning("Failed to add strength recipe (Bukkit returned false). Check config and server logs.");
    } else {
        getLogger().info("Registered strength recipe: " + keyName);
    }
}


    public StrengthManager getStrengthManager() {
        return strengthManager;
    }
}