package com.myst1cs04p.strength_smp.paper.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.paper.PaperStrengthPlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class WithdrawCommand {

    /**
     * /strength withdraw <amount>
     * Player-only — console is rejected inside handleWithdraw.
     */
    public static LiteralArgumentBuilder<CommandSourceStack> create(StrengthCommandLogic logic) {
        return Commands.literal("withdraw")
            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                .executes(ctx -> {
                    PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                    logic.handleWithdraw(sender, !sender.isPlayer(), "strength",
                            new String[]{"withdraw", String.valueOf(amount)});
                    return 1;
                })
            );
    }
}