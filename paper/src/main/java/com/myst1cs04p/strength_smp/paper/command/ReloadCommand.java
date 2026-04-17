package com.myst1cs04p.strength_smp.paper.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.paper.PaperStrengthPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ReloadCommand {

    /**
     * /strength reload
     * Requires the strength.reload permission. Permission check is handled
     * inside {@link StrengthCommandLogic#handleReload} so we don't double-check here.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> create(StrengthCommandLogic logic) {
        return Commands.literal("reload")
            .executes(ctx -> {
                PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                logic.handleReload(sender);
                return 1;
            });
    }
}