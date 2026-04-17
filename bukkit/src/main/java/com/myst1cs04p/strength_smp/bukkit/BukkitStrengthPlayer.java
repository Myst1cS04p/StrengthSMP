package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Wraps a Bukkit {@link Player} as a {@link StrengthPlayer}.
 * Sends messages via BukkitAudiences so Components work on Spigot and Paper alike.
 */
public class BukkitStrengthPlayer implements StrengthPlayer {

    private final Player handle;
    private final BukkitAudiences audiences;

    public BukkitStrengthPlayer(Player handle, BukkitAudiences audiences) {
        this.handle = handle;
        this.audiences = audiences;
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
        audiences.player(handle).sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return handle.isOp();
    }

    public Player getHandle() {
        return handle;
    }
}