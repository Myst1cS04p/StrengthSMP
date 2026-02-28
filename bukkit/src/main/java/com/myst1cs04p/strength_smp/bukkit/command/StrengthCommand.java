package com.myst1cs04p.strength_smp.bukkit.command;

import com.myst1cs04p.strength_smp.bukkit.BukkitStrengthPlayer;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Bukkit glue layer for the /strength command.
 * Converts Bukkit types -> common types, delegates everything to {@link StrengthCommandLogic},
 * then handles console output (which needs a plain-text path since console isn't a StrengthPlayer).
 */
public class StrengthCommand implements CommandExecutor, TabCompleter {

    private final StrengthCommandLogic logic;

    public StrengthCommand(StrengthCommandLogic logic) {
        this.logic = logic;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            logic.execute(new BukkitStrengthPlayer(player), false, label, args);
        } else {
            // Console sender: pass null StrengthPlayer, isConsole = true
            // Common logic routes console-appropriate responses back via the console path
            logic.execute(new ConsoleSenderAdapter(sender), true, label, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(List.of("get", "withdraw", "help", "version"));
            if (sender.hasPermission("strength.set"))    subs.add("set");
            if (sender.hasPermission("strength.reload")) subs.add("reload");

            String partial = args[0].toLowerCase();
            return subs.stream().filter(s -> s.startsWith(partial)).toList();
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            boolean isGet = sub.equals("get") && sender.hasPermission("strength.get");
            boolean isSet = sub.equals("set") && sender.hasPermission("strength.set");
            if (isGet || isSet) {
                List<String> names = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
                return names;
            }
        }

        return Collections.emptyList();
    }

    // -----------------------------------------------------------------------
    // Console adapter = wraps CommandSender as a StrengthPlayer so common logic
    // can call sendMessage on it uniformly.
    // -----------------------------------------------------------------------

    private record ConsoleSenderAdapter(CommandSender sender) implements StrengthPlayer {

        @Override
        public java.util.UUID getUniqueId() {
            return new java.util.UUID(0, 0); // console has no UUID
        }

        @Override
        public String getName() {
            return "CONSOLE";
        }

        @Override
        public void sendMessage(Component message) {
            // Serialize Component to plain text for console
            sender.sendMessage(PlainTextComponentSerializer.plainText().serialize(message));
        }

        @Override
        public boolean hasPermission(String permission) {
            return sender.hasPermission(permission);
        }

        @Override
        public boolean isOp() {
            return true; // console is always op
        }
    }
}
