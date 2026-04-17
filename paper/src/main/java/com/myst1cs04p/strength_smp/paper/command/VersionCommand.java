package com.myst1cs04p.strength_smp.paper.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.paper.PaperStrengthPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class VersionCommand {

    /**
     * /strength version
     * Requires the strength.version permission.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> create(StrengthCommandLogic logic) {
        return Commands.literal("version")
            .executes(ctx -> {
                PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                if (!sender.hasPermission("strength.version")) return 0;
                logic.handleVersion(sender);
                return 1;
            });
    }
}