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
import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import com.myst1cs04p.strength_smp.common.updater.VersionNotifier;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import java.util.List;
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
    public BukkitAudiences audiences;

    private BukkitConfigLoader configLoader;
    private BukkitStorage storage;
    private BukkitPlatform platform;
    private RecipeRegistrar recipeRegistrar;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configLoader = new BukkitConfigLoader(this);
        storage      = new BukkitStorage(this);
        platform     = new BukkitPlatform(this);
        audiences    = platform.getAudiences();

        StrengthConfig config = configLoader.load();

        BukkitMetrics metrics = new BukkitMetrics(this);

        engine = new StrengthEngine(storage, platform, metrics, config);

        // Pre-load already-online players (relevant on reload)
        for (Player online : Bukkit.getOnlinePlayers()) {
            engine.load(wrap(online));
        }

        // Commands
        registerCommands();

        // Events
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerConnectionListener(engine, this), this);
        pm.registerEvents(new StrengthListener(engine, this), this);
        pm.registerEvents(new StrengthItemListener(engine, this), this);

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
        pm.registerEvents(new AdminJoinListener(versionNotifier, audiences), this);
        startUpdateScheduler();

        printBanner();
    }

    protected void registerCommands() {
        StrengthCommandLogic logic = new StrengthCommandLogic(
            engine,
            getDescription().getVersion(),
            name -> Optional.ofNullable(Bukkit.getPlayer(name)).map(this::wrap),
            this::performReload
        );
        StrengthCommand command = new StrengthCommand(logic, audiences);
        if (getCommand("strength") != null) {
            getCommand("strength").setExecutor(command);
            getCommand("strength").setTabCompleter(command);
        }
    }


    @Override
    public void onDisable() {
        if (engine != null) {
            // Strip modifiers from all online players first so attributes don't
            // persist if the plugin is removed between restarts.
            List<StrengthPlayer> online = Bukkit.getOnlinePlayers().stream()
                    .map(this::wrap)
                    .collect(java.util.stream.Collectors.toList());
            engine.removeAllModifiers(online);
            engine.saveAll();
        }
        if (platform != null) platform.close();
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    public BukkitStrengthPlayer wrap(Player player) {
        return new BukkitStrengthPlayer(player, audiences);
    }

    // -----------------------------------------------------------------------
    // Reload
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
    }
}