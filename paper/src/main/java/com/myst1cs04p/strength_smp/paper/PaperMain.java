package com.myst1cs04p.strength_smp.paper;

import com.myst1cs04p.strength_smp.bukkit.BukkitMain;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.paper.command.GetCommand;
import com.myst1cs04p.strength_smp.paper.command.HelpCommand;
import com.myst1cs04p.strength_smp.paper.command.ReloadCommand;
import com.myst1cs04p.strength_smp.paper.command.SetCommand;
import com.myst1cs04p.strength_smp.paper.command.VersionCommand;
import com.myst1cs04p.strength_smp.paper.command.WithdrawCommand;
import com.myst1cs04p.strength_smp.paper.scheduler.PaperUpdateScheduler;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Optional;

/**
 * Paper entry point. Extends {@link BukkitMain} and overrides:
 *
 *  1. {@link #startUpdateScheduler()} — uses Paper's AsyncScheduler instead of BukkitRunnable.
 *  2. {@link #registerPaperCommands()} — registers a full Brigadier /strength command tree
 *     that replaces the plugin.yml CommandExecutor command on Paper servers.
 *
 * Everything else (engine, storage, metrics, listeners) is inherited from BukkitMain.
 *
 * IMPORTANT: LifecycleEvents.COMMANDS fires at bootstrap time, before onEnable.
 * We must therefore register the lifecycle handler here in the constructor so
 * Paper picks it up during the bootstrap phase. The command lambdas capture
 * 'this' and read 'engine' lazily at execution time (not at registration time),
 * so engine will always be initialised when a player actually runs a command.
 */
@SuppressWarnings("UnstableApiUsage")
public class PaperMain extends BukkitMain {

    private PaperUpdateScheduler paperScheduler;

    public PaperMain() {
        // Register the Brigadier command tree during the bootstrap/construction
        // phase so Paper's lifecycle system picks it up correctly.
        // The StrengthCommandLogic is built lazily inside the event handler
        // (after onEnable has run and engine is non-null).
        registerCommands();
    }

    @Override
    public void onEnable() {
        // BukkitMain wires storage, engine, metrics, listeners, and the
        // fallback Bukkit CommandExecutor (harmlessly ignored on Paper).
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (paperScheduler != null) {
            paperScheduler.stop();
        }
    }

    // -----------------------------------------------------------------------
    // Brigadier command registration
    // -----------------------------------------------------------------------

    @Override
    protected void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {

            // Build StrengthCommandLogic here — inside the handler — so it runs
            // after onEnable has initialised 'engine'. The handler itself fires
            // at bootstrap, but the lambda body executes when Paper processes
            // the registration, which is after the plugin is fully enabled.
            StrengthCommandLogic logic = new StrengthCommandLogic(
                engine,
                getPluginMeta().getVersion(),
                name -> Optional.ofNullable(Bukkit.getPlayer(name)).map(this::wrap),
                this::performReload
            );

            event.registrar().register(
                Commands.literal("strength")
                    .then(GetCommand.create(logic))
                    .then(SetCommand.create(logic))
                    .then(WithdrawCommand.create(logic))
                    .then(ReloadCommand.create(logic))
                    .then(VersionCommand.create(logic))
                    .then(HelpCommand.create(logic))
                    // Root /strength with no args — show own strength
                    .executes(ctx -> {
                        PaperStrengthPlayer sender = new PaperStrengthPlayer(ctx.getSource().getSender());
                        logic.handleGet(sender, !sender.isPlayer(), "strength", new String[]{"get"});
                        return 1;
                    })
                    .build(),
                "View or manage strength",
                List.of("str")
            );
        });
    }

    // -----------------------------------------------------------------------
    // Scheduler override
    // -----------------------------------------------------------------------

    /**
     * Override the scheduler hook to use Paper's async scheduler.
     * Called by {@code BukkitMain#onEnable()} after {@code versionNotifier} is initialized.
     */
    @Override
    protected void startUpdateScheduler() {
        paperScheduler = new PaperUpdateScheduler(this, versionNotifier);
        paperScheduler.start();
    }
}