package com.myst1cs04p.strength_smp.paper;

import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Wraps a Paper {@link CommandSender} as a {@link StrengthPlayer} for use
 * inside Brigadier command handlers. On Paper, CommandSender already supports
 * Adventure natively, so no BukkitAudiences bridge is needed.
 *
 * If the sender is a {@link Player}, their UUID and name are used directly.
 * For console senders, a zero UUID and the name "CONSOLE" are used.
 */
public class PaperStrengthPlayer implements StrengthPlayer {

    private final CommandSender sender;

    public PaperStrengthPlayer(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public UUID getUniqueId() {
        return (sender instanceof Player p) ? p.getUniqueId() : new UUID(0, 0);
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public void sendMessage(Component message) {
        // Paper's CommandSender natively supports Adventure Components
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isOp() {
        return sender.isOp();
    }

    public CommandSender getHandle() {
        return sender;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }
}