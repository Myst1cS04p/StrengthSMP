package com.myst1cs04p.strength_smp.paper.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.paper.PaperStrengthPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;

public class SetCommand {

    /**
     * /strength set <player> <amount>
     * Requires the strength.set permission.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> create(StrengthCommandLogic logic) {
        return Commands.literal("set")
            .then(Commands.argument("player", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    Bukkit.getOnlinePlayers().forEach(p -> builder.suggest(p.getName()));
                    return builder.buildFuture();
                })
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                    .executes(ctx -> {
                        PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                        String targetName = StringArgumentType.getString(ctx, "player");
                        int amount = IntegerArgumentType.getInteger(ctx, "amount");

                        // Reuse the logic layer, passing pre-parsed args as strings
                        logic.handleSet(sender, "strength",
                                new String[]{"set", targetName, String.valueOf(amount)});
                        return 1;
                    })
                )
            );
    }
}