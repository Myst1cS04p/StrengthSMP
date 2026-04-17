package com.myst1cs04p.strength_smp.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Required bootstrapper for Paper plugins that use LifecycleEvents.COMMANDS.
 *
 * Paper only allows getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, ...)
 * to be called from a plugin that declares a bootstrapper: in paper-plugin.yml.
 * Without this, Paper throws an IllegalStateException on startup and no Brigadier
 * commands are registered.
 *
 * createPlugin() returns a PaperMain instance. PaperMain's constructor calls
 * registerPaperCommands(), which registers the lifecycle handler at the correct
 * bootstrap phase. The handler body runs after onEnable, so engine is guaranteed
 * to be initialised when StrengthCommandLogic is constructed inside it.
 */
@SuppressWarnings("UnstableApiUsage")
public class StrengthBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        // No bootstrap-time work needed.
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new PaperMain();
    }
}