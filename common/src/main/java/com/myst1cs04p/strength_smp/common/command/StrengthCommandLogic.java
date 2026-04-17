package com.myst1cs04p.strength_smp.common.command;

import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import com.myst1cs04p.strength_smp.common.messaging.Messages;
import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.function.Function;

/**
 * Pure command logic with zero Minecraft coupling.
 *
 * The bukkit layer's CommandExecutor and the Paper Brigadier commands both
 * delegate to this class. Player lookup ({@code playerResolver}) is injected
 * so this class never calls Bukkit.getPlayer() itself.
 */
public class StrengthCommandLogic {

    private final StrengthEngine engine;
    private final String pluginVersion;

    /** Injected by the platform layer — resolves a name to an online StrengthPlayer, if present. */
    private final Function<String, Optional<StrengthPlayer>> playerResolver;

    /** Injected by the platform layer — triggers config reload on the platform side. */
    private final Runnable reloadCallback;

    public StrengthCommandLogic(
            StrengthEngine engine,
            String pluginVersion,
            Function<String, Optional<StrengthPlayer>> playerResolver,
            Runnable reloadCallback) {
        this.engine = engine;
        this.pluginVersion = pluginVersion;
        this.playerResolver = playerResolver;
        this.reloadCallback = reloadCallback;
    }

    /**
     * Execute a strength command. Used by the Bukkit CommandExecutor path.
     *
     * @param sender          the player or console issuing the command
     * @param isConsoleSender true when the sender is not a player
     * @param label           the command alias used (for usage strings)
     * @param args            command arguments
     */
    public void execute(StrengthPlayer sender, boolean isConsoleSender, String label, String[] args) {

        if (args.length == 0) {
            if (isConsoleSender) {
                boolean hasSet = sender != null && sender.hasPermission("strength.set");
                sendMessage(sender, Messages.usageRoot(label, hasSet));
            } else {
                sendMessage(sender, Messages.yourStrength(engine.getStrength(sender)));
            }
            return;
        }

        switch (args[0].toLowerCase()) {
            case "help"              -> handleHelp(sender, label);
            case "get"               -> handleGet(sender, isConsoleSender, label, args);
            case "set"               -> handleSet(sender, label, args);
            case "withdraw"          -> handleWithdraw(sender, isConsoleSender, label, args);
            case "reload"            -> handleReload(sender);
            case "version", "ver"    -> sendMessage(sender, Messages.version(pluginVersion));
            default                  -> sendMessage(sender, Messages.unknownSubcommand());
        }
    }

    // -----------------------------------------------------------------------
    // Public subcommand entry points (called directly by Paper Brigadier commands)
    // -----------------------------------------------------------------------

    public void handleHelp(StrengthPlayer sender, String label) {
        sendMessage(sender, Messages.helpHeader());
        sendMessage(sender, Messages.helpLine(label, "", "show your strength"));

        if (sender == null || sender.hasPermission("strength.get")) {
            sendMessage(sender, Messages.helpLine(label, "get [player]", "check someone else's strength"));
        }
        if (sender != null && sender.hasPermission("strength.set")) {
            sendMessage(sender, Messages.helpLine(label, "set <player> <amount>", "set strength (op)"));
        }
        sendMessage(sender, Messages.helpLine(label, "withdraw <amount>", "convert strength into tokens"));
        if (sender == null || sender.hasPermission("strength.reload")) {
            sendMessage(sender, Messages.helpLine(label, "reload", "reload the plugin config"));
        }
        sendMessage(sender, Messages.helpLine(label, "version", "display the plugin version"));
    }

    public void handleGet(StrengthPlayer sender, boolean isConsoleSender, String label, String[] args) {
        if (args.length == 1) {
            if (isConsoleSender) {
                sendMessage(sender, Messages.usageGet(label));
                return;
            }
            sendMessage(sender, Messages.yourStrength(engine.getStrength(sender)));
            return;
        }

        if (sender != null && !sender.hasPermission("strength.get")) {
            sendMessage(sender, Messages.noPermissionCheckOthers());
            return;
        }

        Optional<StrengthPlayer> target = playerResolver.apply(args[1]);
        if (target.isEmpty()) {
            sendMessage(sender, Messages.playerNotOnline());
            return;
        }

        sendMessage(sender, Messages.targetStrength(target.get().getName(), engine.getStrength(target.get())));
    }

    public void handleSet(StrengthPlayer sender, String label, String[] args) {
        if (sender != null && !sender.hasPermission("strength.set")) {
            sendMessage(sender, Messages.noPermission());
            return;
        }

        if (args.length != 3) {
            sendMessage(sender, Messages.usageSet(label));
            return;
        }

        Optional<StrengthPlayer> target = playerResolver.apply(args[1]);
        if (target.isEmpty()) {
            sendMessage(sender, Messages.playerNotOnline());
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sendMessage(sender, Messages.notANumber());
            return;
        }

        engine.setStrength(target.get(), amount);
        sendMessage(sender, Messages.strengthSet(target.get().getName(), amount));
        target.get().sendMessage(Messages.yourStrengthWasSet(amount));
    }

    public void handleWithdraw(StrengthPlayer sender, boolean isConsoleSender, String label, String[] args) {
        if (isConsoleSender) {
            sendMessage(sender, Messages.playerOnly());
            return;
        }

        if (args.length != 2) {
            sendMessage(sender, Messages.usageWithdraw(label));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sendMessage(sender, Messages.notANumber());
            return;
        }

        StrengthEngine.WithdrawResult result = engine.withdraw(sender, amount);
        switch (result) {
            case SUCCESS             -> sendMessage(sender, Messages.withdrew(amount));
            case INVALID_AMOUNT      -> sendMessage(sender, Messages.invalidWithdrawAmount());
            case INSUFFICIENT_STRENGTH -> sendMessage(sender, Messages.notEnoughStrength());
        }
    }

    public void handleReload(StrengthPlayer sender) {
        if (sender != null && !sender.hasPermission("strength.reload")) {
            sendMessage(sender, Messages.noPermission());
            return;
        }
        reloadCallback.run();
        sendMessage(sender, Messages.reloadSuccess());
    }

    public void handleVersion(StrengthPlayer sender) {
        sendMessage(sender, Messages.version(pluginVersion));
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private void sendMessage(StrengthPlayer sender, Component message) {
        if (sender != null) {
            sender.sendMessage(message);
        }
    }
}