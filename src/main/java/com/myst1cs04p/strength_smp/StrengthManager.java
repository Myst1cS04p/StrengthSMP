package com.myst1cs04p.strength_smp;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class StrengthManager {

    private final HashMap<UUID, Integer> strengthMap = new HashMap<>();
    private final Main plugin;
    private float damageMuliplier;
    private int minStrength;
    private int maxStrength;

    private static final UUID STRENGTH_MODIFIER_UUID = UUID.fromString("a91e682b-1306-4b5e-a63e-8a561c807b3f");
    
    public StrengthManager(Main plugin) {
        this.plugin = plugin;
    }
    
    public void loadStrength(){
        minStrength = plugin.getConfig().getInt("min-strength");
        maxStrength = plugin.getConfig().getInt("max-strength");
        damageMuliplier = plugin.getConfig().getInt("damage-multiplier");
    }

    public void increaseStrength(Player player){
        setStrength(player, getStrength(player) + 1);
    }
    
    public void decreaseStrength(Player player) {
        setStrength(player, getStrength(player) - 1);
    }

    public int getStrength(Player player) {
        return strengthMap.getOrDefault(player.getUniqueId(), 0);
    }

    public void setStrength(Player player, int amount) {
        amount = Math.max(minStrength, Math.min(maxStrength, amount));
        updatePlayerStrength(player, amount, getDamageMultiplier());
        strengthMap.put(player.getUniqueId(), amount);
    }

    public static void updatePlayerStrength(Player player, int strengthLevel, float damageMultiplier) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute == null)
            return;

        // First, remove any existing modifier
        attribute.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(STRENGTH_MODIFIER_UUID))
                .forEach(attribute::removeModifier);

        // Each strength level adds e.g., +0.5 damage (tweak as needed)
        double bonusDamage = strengthLevel * damageMultiplier;

        AttributeModifier modifier = new AttributeModifier(
                STRENGTH_MODIFIER_UUID,
                "custom_strength",
                bonusDamage,
                AttributeModifier.Operation.ADD_NUMBER);

        attribute.addModifier(modifier);
    }

    public float getDamageMultiplier(){
        return damageMuliplier;
    }

    public int getMinStrength() {
        return minStrength;
    }

    public int getMaxStrength() {
        return maxStrength;
    }
}
