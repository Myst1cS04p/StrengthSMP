package com.myst1cs04p.strength_smp.common.platform;

import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;

/**
 * Anything that touches Minecraft internals lives here as an abstraction.
 * Common tells the platform WHAT to do; the platform decides HOW.
 */
public interface StrengthPlatform {

    /**
     * Apply (or re-apply) the attack damage attribute modifier for this player
     * at the given strength level with the given multiplier per level.
     */
    void applyDamageModifier(StrengthPlayer player, int strengthLevel, float damageMultiplier);

    /**
     * Drop {@code amount} strength tokens at the given player's current location.
     * Used when a killer is already at max strength.
     */
    void dropStrengthToken(StrengthPlayer player, int amount);

    /**
     * Add {@code amount} strength tokens directly to the player's inventory.
     * Used by the withdraw command.
     */
    void giveStrengthToken(StrengthPlayer player, int amount);

    /**
     * Broadcast a Component message to every online player.
     * Used for server-wide update nag notifications.
     */
    void broadcastMessage(net.kyori.adventure.text.Component message);

    /**
     * Send a Component message to every online player who has the given permission.
     */
    void broadcastToPermission(String permission, net.kyori.adventure.text.Component message);
}
