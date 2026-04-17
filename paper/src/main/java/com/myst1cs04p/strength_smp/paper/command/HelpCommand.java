package com.myst1cs04p.strength_smp.paper.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.paper.PaperStrengthPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class HelpCommand {

    /**
     * /strength help
     * Delegates entirely to {@link StrengthCommandLogic#handleHelp} which
     * already filters lines based on the sender's permissions.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> create(StrengthCommandLogic logic) {
        return Commands.literal("help")
            .executes(ctx -> {
                PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                logic.handleHelp(sender, "strength");
                return 1;
            });
    }
}