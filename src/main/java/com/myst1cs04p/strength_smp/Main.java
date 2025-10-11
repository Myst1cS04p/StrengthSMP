package com.myst1cs04p.strength_smp;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private StrengthManager strengthManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        strengthManager = new StrengthManager(this);
        getServer().getPluginManager().registerEvents(new StrengthListener(this, strengthManager), this);
    }

    public StrengthManager getStrengthManager() {
        return strengthManager;
    }
}