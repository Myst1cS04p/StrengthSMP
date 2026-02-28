package com.myst1cs04p.strength_smp.common.platform;

import java.util.Map;
import java.util.UUID;

/**
 * Persistence contract. Common only calls this - it never touches YAML or files directly.
 * Bukkit implements this with YamlConfiguration.
 */
public interface StrengthStorage {

    /**
     * Load the strength value for the given UUID.
     * Returns 0 if no record exists.
     */
    int load(UUID uuid);

    /**
     * Persist a single player's strength value immediately.
     */
    void save(UUID uuid, int value);

    /**
     * Persist every entry in the map at once (used on shutdown).
     */
    void saveAll(Map<UUID, Integer> data);
}
