package com.myst1cs04p.strength_smp.common.model;

import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * Platform-agnostic representation of a player.
 * Common logic never touches org.bukkit.entity.Player directly - only this.
 */
public interface StrengthPlayer {

    UUID getUniqueId();

    String getName();

    /**
     * Send a rich-text Component message to this player.
     * The platform layer is responsible for dispatching it via whatever Adventure
     * adapter is available (native on Paper, via Bukkit adapter on Spigot).
     */
    void sendMessage(Component message);

    /**
     * Returns true if this player has the given permission node.
     */
    boolean hasPermission(String permission);

    /**
     * Returns true if this player is a server operator.
     */
    boolean isOp();
}
