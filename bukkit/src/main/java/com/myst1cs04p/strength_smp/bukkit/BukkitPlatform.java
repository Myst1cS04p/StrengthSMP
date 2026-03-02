package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import com.myst1cs04p.strength_smp.common.platform.StrengthPlatform;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Bukkit implementation of {@link StrengthPlatform}.
 * Every call into Minecraft's API happens here.
 */
public class BukkitPlatform implements StrengthPlatform {

    /** Stable UUID for the attack damage attribute modifier across all players. */
    private static final UUID STRENGTH_MODIFIER_UUID =
            UUID.fromString("a91e682b-1306-4b5e-a63e-8a561c807b3f");

    private final JavaPlugin plugin;

    public BukkitPlatform(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void applyDamageModifier(StrengthPlayer player, int strengthLevel, float damageMultiplier) {
        Player bukkit = unwrap(player);
        if (bukkit == null) return;

        AttributeInstance attribute = bukkit.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attribute == null) return;

        // Remove existing modifier so we never stack duplicates
        attribute.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(STRENGTH_MODIFIER_UUID))
                .forEach(attribute::removeModifier);

        double bonus = (double) strengthLevel * damageMultiplier;
        AttributeModifier modifier = new AttributeModifier(
                STRENGTH_MODIFIER_UUID,
                "custom_strength_multiplier",
                bonus,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
        );
        attribute.addModifier(modifier);
    }

    @Override
    public void dropStrengthToken(StrengthPlayer player, int amount) {
        Player bukkit = unwrap(player);
        if (bukkit == null) return;

        ItemStack token = StrengthItem.createStrengthToken();
        token.setAmount(Math.min(amount, token.getMaxStackSize()));
        bukkit.getWorld().dropItemNaturally(bukkit.getLocation(), token);
    }

    @Override
    public void giveStrengthToken(StrengthPlayer player, int amount) {
        Player bukkit = unwrap(player);
        if (bukkit == null) return;

        ItemStack token = StrengthItem.createStrengthToken();
        token.setAmount(amount);
        bukkit.getInventory().addItem(token);
    }

    @Override
    public void broadcastMessage(Component message) {
        // Send to every online player
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
        // Also log a plain-text version to console
        Bukkit.getConsoleSender().sendMessage(
            net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacySection().serialize(message)
        );
    }

    @Override
    public void broadcastToPermission(String permission, Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private Player unwrap(StrengthPlayer player) {
        if (player instanceof BukkitStrengthPlayer bsp) {
            return bsp.getHandle();
        }
        return Bukkit.getPlayer(player.getUniqueId());
    }
}