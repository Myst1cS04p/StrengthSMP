package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.bukkit.command.StrengthCommand;
import com.myst1cs04p.strength_smp.bukkit.listener.AdminJoinListener;
import com.myst1cs04p.strength_smp.bukkit.listener.PlayerConnectionListener;
import com.myst1cs04p.strength_smp.bukkit.listener.StrengthItemListener;
import com.myst1cs04p.strength_smp.bukkit.listener.StrengthListener;
import com.myst1cs04p.strength_smp.bukkit.recipe.RecipeRegistrar;
import com.myst1cs04p.strength_smp.bukkit.scheduler.BukkitUpdateScheduler;
import com.myst1cs04p.strength_smp.common.command.StrengthCommandLogic;
import com.myst1cs04p.strength_smp.common.engine.StrengthConfig;
import com.myst1cs04p.strength_smp.common.engine.StrengthEngine;
import com.myst1cs04p.strength_smp.common.updater.VersionNotifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * Bukkit plugin entry point.
 * Constructs all dependencies and wires them together.
 * Paper extends this via {@code PaperMain}.
 */
public class BukkitMain extends JavaPlugin {

    protected StrengthEngine engine;
    protected VersionNotifier versionNotifier;

    private BukkitConfigLoader configLoader;
    private BukkitStorage storage;
    private BukkitPlatform platform;
    private RecipeRegistrar recipeRegistrar;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configLoader   = new BukkitConfigLoader(this);
        storage        = new BukkitStorage(this);
        platform       = new BukkitPlatform(this);

        StrengthConfig config = configLoader.load();

        BukkitMetrics metrics = new BukkitMetrics(this);

        engine = new StrengthEngine(storage, platform, metrics, config);

        // Load any pre-existing data from strength.yml into the cache
        for (Player online : Bukkit.getOnlinePlayers()) {
            engine.load(new BukkitStrengthPlayer(online));
        }

        // Commands
        StrengthCommandLogic logic = new StrengthCommandLogic(
            engine,
            getDescription().getVersion(),
            name -> Optional.ofNullable(Bukkit.getPlayer(name)).map(BukkitStrengthPlayer::new),
            this::performReload
        );
        StrengthCommand command = new StrengthCommand(logic);
        if (getCommand("strength") != null) {
            getCommand("strength").setExecutor(command);
            getCommand("strength").setTabCompleter(command);
        }

        // Events
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerConnectionListener(engine), this);
        pm.registerEvents(new StrengthListener(engine), this);
        pm.registerEvents(new StrengthItemListener(engine), this);

        // Recipe
        recipeRegistrar = new RecipeRegistrar(this);
        recipeRegistrar.register(config);

        // Version notifier
        versionNotifier = new VersionNotifier(
            getLogger(),
            "Myst1cS04p",
            "StrengthSMP",
            getDescription().getVersion(),
            platform
        );
        pm.registerEvents(new AdminJoinListener(versionNotifier), this);
        startUpdateScheduler();

        printBanner();
    }

    @Override
    public void onDisable() {
        if (engine != null) engine.saveAll();
    }

    // -----------------------------------------------------------------------
    // Reload (called by command logic via lambda)
    // -----------------------------------------------------------------------

    protected void performReload() {
        storage.reload();
        StrengthConfig newConfig = configLoader.load();
        engine.reloadConfig(newConfig);
        recipeRegistrar.register(newConfig);
        getLogger().info("[StrengthSMP] Config reloaded.");
    }

    // -----------------------------------------------------------------------
    // Scheduler hook
    // -----------------------------------------------------------------------

    /**
     * Start the version update scheduler.
     * Overridden by {@code PaperMain} to use Paper's async scheduler instead.
     */
    protected void startUpdateScheduler() {
        new BukkitUpdateScheduler(this, versionNotifier).start();
    }

    // -----------------------------------------------------------------------
    // ASCII banner
    // -----------------------------------------------------------------------

    private void printBanner() {
        getLogger().info("\n\u001B[31m" + 
                        "███████╗████████╗██████╗ ███████╗███╗   ██╗ ██████╗████████╗██╗  ██╗\r\n" + //
                        "██╔════╝╚══██╔══╝██╔══██╗██╔════╝████╗  ██║██╔════╝╚══██╔══╝██║  ██║\r\n" + //
                        "███████╗   ██║   ██████╔╝█████╗  ██╔██╗ ██║██║  ███╗  ██║   ███████║\r\n" + //
                        "╚════██║   ██║   ██╔══██╗██╔══╝  ██║╚██╗██║██║   ██║  ██║   ██╔══██║\r\n" + //
                        "███████║   ██║   ██║  ██║███████╗██║ ╚████║╚██████╔╝  ██║   ██║  ██║\r\n" + //
                        "╚══════╝   ╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝   ╚═╝   ╚═╝  ╚═╝\r\n" + //
                        "                                                                    \r\n" + //
                        "                    ███████╗███╗   ███╗██████╗                      \r\n" + //
                        "                    ██╔════╝████╗ ████║██╔══██╗                     \r\n" + //
                        "                    ███████╗██╔████╔██║██████╔╝                     \r\n" + //
                        "                    ╚════██║██║╚██╔╝██║██╔═══╝                      \r\n" + //
                        "                    ███████║██║ ╚═╝ ██║██║                          \r\n" + //
                        "                    ╚══════╝╚═╝     ╚═╝╚═╝                          \r\n" + //
                        "                                                                    \u001B[0m");
        getLogger().info("""
                \u001B[35m
                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@@
                @@@@@         @@@@@@@@@         @@@@@
                @@@@@         @@@@@@@@@         @@@@@
                @@@@@         @@@@@@@@@         @@@@@
                @@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@
                @@@@@@@@@     @@@@@@@@@@@@@@@@@@@@@@@
                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                \u001B[0m
                """);
        getLogger().info("\n\u001B[33m"+
                "╻ ╻┏━╸┏━┓     ╺┳╸╻ ╻╻┏━┓   ╻ ╻┏━┓┏━┓\r\n" + //
                "┗┳┛┣╸ ┗━┓      ┃ ┣━┫┃┗━┓   ┃╻┃┣━┫┗━┓\r\n" + //
                " ╹ ┗━╸┗━┛ ┛    ╹ ╹ ╹╹┗━┛   ┗┻┛╹ ╹┗━┛\r\n" + //
                "┏━┓┏┓ ┏━┓┏━┓╻  ╻ ╻╺┳╸┏━╸╻  ╻ ╻      \r\n" + //
                "┣━┫┣┻┓┗━┓┃ ┃┃  ┃ ┃ ┃ ┣╸ ┃  ┗┳┛      \r\n" + //
                "╹ ╹┗━┛┗━┛┗━┛┗━╸┗━┛ ╹ ┗━╸┗━╸ ╹       \r\n" + //
                "┏┓╻┏━╸┏━╸┏━╸┏━┓┏━┓┏━┓┏━┓╻ ╻         \r\n" + //
                "┃┗┫┣╸ ┃  ┣╸ ┗━┓┗━┓┣━┫┣┳┛┗┳┛         \r\n" + //
                "╹ ╹┗━╸┗━╸┗━╸┗━┛┗━┛╹ ╹╹┗╸ ╹          \u001B[0m");

        registerRecipe(); // new part
    }
}