package com.myst1cs04p.strength_smp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrengthCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final StrengthManager manager;

    public StrengthCommand(Main plugin, StrengthManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (sender instanceof Player p) {
                sender.sendMessage(Component.text("[Strength] ", NamedTextColor.RED)
                        .append(Component.text("Your strength: ", NamedTextColor.GREEN))
                        .append(Component.text(manager.getStrength(p), NamedTextColor.GOLD)));
            } else {
                if (sender.hasPermission("strength.set")) {
                    sender.sendMessage(
                            Component.text("Usage: /" + label + " <get|set|withdraw|reload|ver>", NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text("Usage: /" + label + " <get|withdraw|ver>", NamedTextColor.YELLOW));
                }
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "help" -> {
                sender.sendMessage(Component.text("----- Strength Commands -----", NamedTextColor.YELLOW));

                sender.sendMessage(Component.text("/" + label, NamedTextColor.AQUA)
                        .append(Component.text(" - show your strength", NamedTextColor.WHITE)));

                if (sender.hasPermission("strength.get")) {
                    sender.sendMessage(Component.text("/" + label, NamedTextColor.AQUA)
                            .append(Component.text(" check [player]", NamedTextColor.WHITE))
                            .append(Component.text(" - check someone else's", NamedTextColor.WHITE)));
                } else {
                    sender.sendMessage(Component.text("/" + label, NamedTextColor.AQUA)
                            .append(Component.text(" check", NamedTextColor.WHITE)));
                }

                if(sender.hasPermission("strength.set")){
                    sender.sendMessage(Component.text("/" + label, NamedTextColor.AQUA)
                        .append(Component.text(" set <player> <amount>", NamedTextColor.AQUA))
                        .append(Component.text(" - set strength (op)", NamedTextColor.WHITE)));
                }

                sender.sendMessage(Component.text("/" + label, NamedTextColor.AQUA)
                    .append(Component.text(" withdraw <amount>",NamedTextColor.AQUA))
                    .append(Component.text(" - convert strength into tokens", NamedTextColor.WHITE)));

                if(sender.hasPermission("strength.reload")){
                    sender.sendMessage(Component.text("/" + label + " reload", NamedTextColor.AQUA)
                        .append(Component.text(" - Reloads the plugin config", NamedTextColor.WHITE)));
                }

                sender.sendMessage(Component.text("/" + label + " version", NamedTextColor.AQUA)
                    .append(Component.text(" - Displays the plugin version", NamedTextColor.WHITE)));


                return true;
            }

            case "get" -> {
                if (args.length == 1) {
                    if (!(sender instanceof Player p)) {
                        sender.sendMessage(Component.text("Specify a player gng",NamedTextColor.RED));
                        return true;
                    }
                    sender.sendMessage(Component.text("[Strength] ", NamedTextColor.GRAY)
                        .append(Component.text("Your strength: ", NamedTextColor.GREEN))
                        .append(Component.text(manager.getStrength(p), NamedTextColor.GOLD)));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Component.text("Bro isn't even online online 😭", NamedTextColor.RED));
                    return true;
                }

                sender.sendMessage(Component.text("[Strength] ", NamedTextColor.GRAY)
                    .append(Component.text(target.getName() + "'s strength: ", NamedTextColor.GREEN))
                    .append(Component.text(manager.getStrength(target), NamedTextColor.GOLD)));
                return true;
            }

            case "set" -> {
                if (!sender.hasPermission("strength.set")) {
                    sender.sendMessage(Component.text("You lack permission.", NamedTextColor.RED));
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage(Component.text("Usage: /" + label + " set <player> <amount>", NamedTextColor.RED));
                    return true;
                }

                Player target2 = Bukkit.getPlayer(args[1]);
                if (target2 == null) {
                    sender.sendMessage(Component.text("Bro isn't even online online 😭", NamedTextColor.RED));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Component.text("gng that wasnt a number 🙏 go back to school bro 😭", NamedTextColor.RED));
                    return true;
                }

                manager.setStrength(target2, amount);
                sender.sendMessage(
                        Component.text("Set " + target2.getName() + "'s strength to " + amount, NamedTextColor.GREEN));
                target2.sendMessage(Component.text("Your strength was set to " + amount, NamedTextColor.YELLOW));
                return true;
            }

            case "withdraw" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Component.text("Chat only players can withdraw ts 🙏", NamedTextColor.RED));
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(Component.text("Usage: /" + label + " withdraw <amount>", NamedTextColor.RED));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Whatever amount u js tried to withdraw wasn't a number gng. Go back to school.", NamedTextColor.RED));
                    return true;
                }

                int current = manager.getStrength(player);
                if (amount <= 0) {
                    player.sendMessage(Component.text("You can't withdraw nothing.", NamedTextColor.RED));
                    return true;
                }
                if (current - amount < manager.getMinStrength()) {
                    player.sendMessage(Component.text("You ain't got that much strength twin 🥀",
                            NamedTextColor.RED));
                    return true;
                }

                // Decrease player's strength
                manager.setStrength(player, current - amount);

                // Give strength tokens
                ItemStack token = StrengthItem.createStrengthToken();
                token.setAmount(amount);
                player.getInventory().addItem(token);

                player.sendMessage(Component.text("Withdrew ", NamedTextColor.GREEN)
                        .append(Component.text(amount, NamedTextColor.GOLD))
                        .append(Component.text(" strength and received ", NamedTextColor.GREEN))
                        .append(Component.text(amount, NamedTextColor.GOLD))
                        .append(Component.text(" tokens.", NamedTextColor.GREEN)));
                return true;
            }
            case "reload" -> {
                if(!sender.hasPermission("strength.reload")){
                    sender.sendMessage(Component.text("You lack the perms to reload ts", NamedTextColor.RED));
                    return false;
                }
                manager.loadStrength();
                plugin.registerRecipe();
                return true;
            }
            case "version" -> {
                sender.sendMessage(Component.text("You are running version ", NamedTextColor.GREEN)
                        .append(Component.text(plugin.getDescription().getVersion(), NamedTextColor.GOLD)
                                .append(Component.text(" of ", NamedTextColor.GREEN))
                                .append(Component.text("Myst1c's Strength SMP", NamedTextColor.RED))));
                return true;
            }

            default -> {
                sender.sendMessage(Component.text("Idk this command. Try /help", NamedTextColor.RED));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>();
            subs.add("get");
            if(sender.hasPermission("strength.set")) subs.add("set");
            subs.add("withdraw");
            subs.add("help");
            subs.add("version");
            if(sender.hasPermission("strength.reload")) subs.add("reload");
            String partial = args[0].toLowerCase();
            List<String> out = new ArrayList<>();
            for (String s : subs)
                if (s.startsWith(partial))
                    out.add(s);
            return out;
        }

        if (args.length == 2 && ((args[0].equalsIgnoreCase("get") && sender.hasPermission("strength.get")) || (args[0].equalsIgnoreCase("set") && sender.hasPermission("strength.set")))) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers())
                names.add(p.getName());
            return names;
        }

        return Collections.emptyList();
    }
}
