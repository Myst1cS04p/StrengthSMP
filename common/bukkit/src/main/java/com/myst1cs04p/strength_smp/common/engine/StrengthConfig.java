package com.myst1cs04p.strength_smp.common.engine;

import java.util.List;
import java.util.Map;

/**
 * Immutable snapshot of config.yml values.
 * The bukkit layer reads the YAML and constructs one of these to hand to the engine.
 * Common never reads files directly.
 */
public final class StrengthConfig {

    private final int minStrength;
    private final int maxStrength;
    private final float damageMultiplier;
    private final boolean strengthItemEnabled;
    private final RecipeConfig recipe;

    public StrengthConfig(
            int minStrength,
            int maxStrength,
            float damageMultiplier,
            boolean strengthItemEnabled,
            RecipeConfig recipe) {
        this.minStrength = minStrength;
        this.maxStrength = maxStrength;
        this.damageMultiplier = damageMultiplier;
        this.strengthItemEnabled = strengthItemEnabled;
        this.recipe = recipe;
    }

    public int getMinStrength() { return minStrength; }
    public int getMaxStrength() { return maxStrength; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public boolean isStrengthItemEnabled() { return strengthItemEnabled; }
    public RecipeConfig getRecipe() { return recipe; }

    /**
     * Flat data class representing the recipe block in config.yml.
     * Material names are kept as Strings here - bukkit resolves them to Material enums.
     */
    public static final class RecipeConfig {

        private final String key;
        private final List<String> shape;
        private final Map<Character, String> ingredients; // char -> material name

        public RecipeConfig(String key, List<String> shape, Map<Character, String> ingredients) {
            this.key = key;
            this.shape = List.copyOf(shape);
            this.ingredients = Map.copyOf(ingredients);
        }

        public String getKey() { return key; }
        public List<String> getShape() { return shape; }
        public Map<Character, String> getIngredients() { return ingredients; }
    }
}
