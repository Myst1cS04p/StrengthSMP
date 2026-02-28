package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Wraps a Bukkit {@link Player} as a {@link StrengthPlayer}.
 * Common logic always receives this - never a raw Player.
 */
public class BukkitStrengthPlayer implements StrengthPlayer {

    private final Player handle;

    public BukkitStrengthPlayer(Player handle) {
        this.handle = handle;
    }

    @Override
    public UUID getUniqueId() {
        return handle.getUniqueId();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public void sendMessage(Component message) {
        handle.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return handle.isOp();
    }

    /** Direct access to the underlying Bukkit Player for platform-side use only. */
    public Player getHandle() {
        return handle;
    }
}
