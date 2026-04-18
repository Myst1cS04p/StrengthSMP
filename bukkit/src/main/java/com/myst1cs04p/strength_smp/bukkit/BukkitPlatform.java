package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import com.myst1cs04p.strength_smp.common.platform.StrengthPlatform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit implementation of {@link StrengthPlatform}.
 * Uses adventure-platform-bukkit to bridge Adventure Components to Spigot.
 *
 * Attributes are looked up via {@link Registry#ATTRIBUTE} rather than the
 * old enum-style static fields (e.g. Attribute.GENERIC_ATTACK_DAMAGE) which
 * were removed in 1.21.4.
 */
public class BukkitPlatform implements StrengthPlatform {

    private static final NamespacedKey STRENGTH_MODIFIER_KEY =
            new NamespacedKey("strengthsmp", "strength_modifier");
    private static final Attribute ATTACK_DAMAGE = resolveAttackDamage();

    private static Attribute resolveAttackDamage() {
        // 1.21.1 and below use "generic.attack_damage"
        Attribute a = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.attack_damage"));
        if (a != null) return a;
        // 1.21.2+ renamed it to just "attack_damage"
        return Registry.ATTRIBUTE.get(NamespacedKey.minecraft("attack_damage"));
    }
    private final BukkitAudiences audiences;

    public BukkitPlatform(JavaPlugin plugin) {
        this.audiences = BukkitAudiences.create(plugin);
    }

    @Override
    public void applyDamageModifier(StrengthPlayer player, int strengthLevel, float damageMultiplier) {
        Player bukkit = unwrap(player);
        if (bukkit == null) return;
        if (ATTACK_DAMAGE == null) return;

        AttributeInstance attribute = bukkit.getAttribute(ATTACK_DAMAGE);
        if (attribute == null) return;

        attribute.getModifiers().stream()
                .filter(mod -> STRENGTH_MODIFIER_KEY.equals(mod.getKey()))
                .toList()                              
                .forEach(attribute::removeModifier);

        double bonus = (double) strengthLevel * damageMultiplier;
        AttributeModifier modifier = new AttributeModifier(
                STRENGTH_MODIFIER_KEY,
                bonus,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                EquipmentSlotGroup.ANY
        );
        attribute.addModifier(modifier);
    }

    @Override
    public void removeDamageModifier(StrengthPlayer player) {
        Player bukkit = unwrap(player);
        if (bukkit == null) return;
        if (ATTACK_DAMAGE == null) return;

        AttributeInstance attribute = bukkit.getAttribute(ATTACK_DAMAGE);
        if (attribute == null) return;

        attribute.getModifiers().stream()
                .filter(mod -> STRENGTH_MODIFIER_KEY.equals(mod.getKey()))
                .toList()
                .forEach(attribute::removeModifier);
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            audiences.player(player).sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(
            LegacyComponentSerializer.legacySection().serialize(message)
        );
    }

    @Override
    public void broadcastToPermission(String permission, Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                audiences.player(player).sendMessage(message);
            }
        }
    }

    public Audience audience(StrengthPlayer player) {
        Player bukkit = unwrap(player);
        return bukkit != null ? audiences.player(bukkit) : Audience.empty();
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    /** Close the Adventure platform adapter. Call from onDisable. */
    public void close() {
        audiences.close();
    }

    private Player unwrap(StrengthPlayer player) {
        if (player instanceof BukkitStrengthPlayer bsp) {
            return bsp.getHandle();
        }
        return Bukkit.getPlayer(player.getUniqueId());
    }
}