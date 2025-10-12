package com.myst1cs04p.strength_smp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class StrengthData {
    private final Main plugin;
    private File file;
    private FileConfiguration data;

    public StrengthData(Main plugin) {
        this.plugin = plugin;
        createFile();
    }

    private void createFile() {
        file = new File(plugin.getDataFolder(), "strength.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create strength.yml!");
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
    }

    public int getStrength(UUID uuid) {
        return data.getInt(uuid.toString(), 0);
    }

    public void setStrength(UUID uuid, int value) {
        data.set(uuid.toString(), value);
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save strength.yml!");
        }
    }

    public FileConfiguration getData() {
        return data;
    }
}
