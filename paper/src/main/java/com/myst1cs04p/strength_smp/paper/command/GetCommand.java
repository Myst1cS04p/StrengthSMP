package com.myst1cs04p.strength_smp.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.paper.PaperStrengthPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;

public class GetCommand {

    /**
     * /strength get              — show your own strength (player only)
     * /strength get <player>     — show another player's strength (requires strength.get)
     */
    public static LiteralArgumentBuilder<CommandSourceStack> create(StrengthCommandLogic logic) {
        return Commands.literal("get")

            // /strength get  (no argument — show self)
            .executes(ctx -> {
                PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                logic.handleGet(sender, !sender.isPlayer(), "strength", new String[]{"get"});
                return 1;
            })

            // /strength get <player>
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    Bukkit.getOnlinePlayers().forEach(p -> builder.suggest(p.getName()));
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                    String targetName = StringArgumentType.getString(ctx, "player");
                    logic.handleGet(sender, !sender.isPlayer(), "strength",
                            new String[]{"get", targetName});
                    return 1;
                })
            );
    }
}