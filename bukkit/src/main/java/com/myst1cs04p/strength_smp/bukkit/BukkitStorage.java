package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.common.platform.StrengthStorage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * YAML-backed persistence. All file I/O lives here, away from common.
 */
public class BukkitStorage implements StrengthStorage {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration data;

    public BukkitStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        file = new File(plugin.getDataFolder(), "strength.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("[StrengthSMP] Could not create strength.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public int load(UUID uuid) {
        return data.getInt(uuid.toString(), 0);
    }

    @Override
    public void save(UUID uuid, int value) {
        data.set(uuid.toString(), value);
        flush();
    }

    @Override
    public void saveAll(Map<UUID, Integer> map) {
        map.forEach((uuid, value) -> data.set(uuid.toString(), value));
        flush();
    }

    private void flush() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("[StrengthSMP] Failed to save strength.yml: " + e.getMessage());
        }
    }

    /** Re-read from disk (useful after external edits or reload). */
    public void reload() {
        data = YamlConfiguration.loadConfiguration(file);
    }
}
