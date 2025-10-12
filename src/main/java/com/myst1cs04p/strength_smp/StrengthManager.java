package com.myst1cs04p.strength_smp;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StrengthManager {

    private final HashMap<UUID, Integer> strengthMap = new HashMap<>();
    private final Main plugin;
    private final StrengthData data;

    private float damageMultiplier;
    private int minStrength;
    private int maxStrength;

    private static final UUID STRENGTH_MODIFIER_UUID = UUID.fromString("a91e682b-1306-4b5e-a63e-8a561c807b3f");

    public StrengthManager(Main plugin) {
        this.plugin = plugin;
        this.data = new StrengthData(plugin);
    }

    public void loadStrength() {
        minStrength = plugin.getConfig().getInt("min-strength");
        maxStrength = plugin.getConfig().getInt("max-strength");
        damageMultiplier = (float) plugin.getConfig().getDouble("damage-multiplier");

        for (String key : data.getData().getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int value = data.getData().getInt(key);
                strengthMap.put(uuid, value);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in strength.yml: " + key);
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Integer> entry : strengthMap.entrySet()) {
            data.setStrength(entry.getKey(), entry.getValue());
        }
        data.save();
    }

    public void increaseStrength(Player player) {
        setStrength(player, getStrength(player) + 1);
    }

    public void decreaseStrength(Player player) {
        setStrength(player, getStrength(player) - 1);
    }

    public int getStrength(Player player) {
        return strengthMap.getOrDefault(player.getUniqueId(), data.getStrength(player.getUniqueId()));
    }

    public void setStrength(Player player, int amount) {
        amount = Math.max(minStrength, Math.min(maxStrength, amount));
        strengthMap.put(player.getUniqueId(), amount);
        data.setStrength(player.getUniqueId(), amount);
        updatePlayerStrength(player, amount, damageMultiplier);
        data.save();
    }

    public static void updatePlayerStrength(Player player, int strengthLevel, float damageMultiplier) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute == null)
            return;

        // remove old modifier
        attribute.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(STRENGTH_MODIFIER_UUID))
                .forEach(attribute::removeModifier);

        // Now we’ll use a MULTIPLIER instead of ADD_NUMBER
        // Each strength level increases total damage by (1 + (strengthLevel *
        // damageMultiplier))
        double bonusPercent = strengthLevel * damageMultiplier;

        AttributeModifier modifier = new AttributeModifier(
                STRENGTH_MODIFIER_UUID,
                "custom_strength_multiplier",
                bonusPercent,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1 // <— multiplier instead of flat add
        );

        attribute.addModifier(modifier);
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public int getMinStrength() {
        return minStrength;
    }

    public int getMaxStrength() {
        return maxStrength;
    }
}
