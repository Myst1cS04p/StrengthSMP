package com.myst1cs04p.strength_smp.common.engine;

import com.myst1cs04p.strength_smp.common.model.StrengthPlayer;
import com.myst1cs04p.strength_smp.common.platform.StrengthMetrics;
import com.myst1cs04p.strength_smp.common.platform.StrengthPlatform;
import com.myst1cs04p.strength_smp.common.platform.StrengthStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Core strength logic.
 * This class knows nothing about Bukkit, Paper, or any Minecraft API.
 * It operates on interfaces only and is the single source of truth for
 * all strength calculations.
 */
public class StrengthEngine {

    private final HashMap<UUID, Integer> cache = new HashMap<>();

    private final StrengthStorage storage;
    private final StrengthPlatform platform;
    private final StrengthMetrics metrics;

    private StrengthConfig config;

    public StrengthEngine(StrengthStorage storage, StrengthPlatform platform, StrengthMetrics metrics, StrengthConfig config) {
        this.storage = storage;
        this.platform = platform;
        this.metrics = metrics;
        this.config = config;
    }

    // -----------------------------------------------------------------------
    // Config hot-reload
    // -----------------------------------------------------------------------

    /**
     * Replace the active config (called on /strength reload).
     * Clears the in-memory cache so all values are re-read with new bounds.
     */
    public void reloadConfig(StrengthConfig newConfig) {
        this.config = newConfig;
        cache.clear();
    }

    // -----------------------------------------------------------------------
    // Data lifecycle
    // -----------------------------------------------------------------------

    /**
     * Preload a player's strength from storage and apply their damage modifier.
     * Call this on player join.
     */
    public void load(StrengthPlayer player) {
        if (!cache.containsKey(player.getUniqueId())) {
            int value = storage.load(player.getUniqueId());
            cache.put(player.getUniqueId(), value);
        }
        // Always re-apply the modifier on join — covers the case where the
        // plugin was reloaded or the player rejoined after a crash.
        platform.applyDamageModifier(player, cache.get(player.getUniqueId()), config.getDamageMultiplier());
    }

    /**
     * Flush all cached values to storage. Call this on server shutdown.
     */
    public void saveAll() {
        storage.saveAll(Map.copyOf(cache));
    }

    /**
     * Strip the strength modifier from every online player.
     * Call this in onDisable BEFORE saveAll so attributes are clean if the
     * plugin is removed from the server between restarts.
     */
    public void removeAllModifiers(List<StrengthPlayer> onlinePlayers) {
        for (StrengthPlayer player : onlinePlayers) {
            platform.removeDamageModifier(player);
        }
    }

    /**
     * Persist, strip the modifier, and evict a single player from the cache.
     * Call this on player quit.
     */
    public void flushPlayer(StrengthPlayer player) {
        Integer value = cache.remove(player.getUniqueId());
        if (value != null) {
            storage.save(player.getUniqueId(), value);
        }
        // Remove the modifier regardless — if they rejoin while the plugin
        // is still loaded, load() will re-apply it.
        platform.removeDamageModifier(player);
    }

    // -----------------------------------------------------------------------
    // Strength reads
    // -----------------------------------------------------------------------

    public int getStrength(StrengthPlayer player) {
        return cache.computeIfAbsent(player.getUniqueId(), storage::load);
    }

    public int getMinStrength() { return config.getMinStrength(); }
    public int getMaxStrength() { return config.getMaxStrength(); }
    public float getDamageMultiplier() { return config.getDamageMultiplier(); }
    public StrengthConfig getConfig() { return config; }

    // -----------------------------------------------------------------------
    // Strength writes
    // -----------------------------------------------------------------------

    public void increaseStrength(StrengthPlayer player) {
        setStrength(player, getStrength(player) + 1);
    }

    public void decreaseStrength(StrengthPlayer player) {
        setStrength(player, getStrength(player) - 1);
    }

    /**
     * Set a player's strength, clamping to [min, max], persisting to storage,
     * re-applying the damage modifier, and recording the metric event.
     */
    public void setStrength(StrengthPlayer player, int amount) {
        int clamped = Math.max(config.getMinStrength(), Math.min(config.getMaxStrength(), amount));
        cache.put(player.getUniqueId(), clamped);
        storage.save(player.getUniqueId(), clamped);
        platform.applyDamageModifier(player, clamped, config.getDamageMultiplier());
        metrics.recordStrengthChange(clamped);
    }

    // -----------------------------------------------------------------------
    // Kill logic (called from platform event listeners)
    // -----------------------------------------------------------------------

    /**
     * Handle a PvP kill. Decreases victim strength (if above min) and either
     * increases killer strength or drops a token if killer is already at max.
     */
    public void handleKill(StrengthPlayer killer, StrengthPlayer victim) {
        int victimStrength = getStrength(victim);

        if (victimStrength <= config.getMinStrength()) {
            // Victim has nothing left to give so killer gets nothing
            return;
        }

        decreaseStrength(victim);

        int killerStrength = getStrength(killer);
        if (killerStrength < config.getMaxStrength()) {
            increaseStrength(killer);
        } else {
            platform.dropStrengthToken(killer, 1);
            metrics.recordTokenDropped();
        }
    }

    // -----------------------------------------------------------------------
    // Token consumption (called from item interact listener)
    // -----------------------------------------------------------------------

    /**
     * Attempt to apply a strength token to a player.
     *
     * @return true if the token was consumed, false if player was already at max
     */
    public boolean consumeToken(StrengthPlayer player) {
        if (getStrength(player) >= config.getMaxStrength()) {
            return false;
        }
        increaseStrength(player);
        metrics.recordTokenConsumed();
        return true;
    }

    // -----------------------------------------------------------------------
    // Withdraw logic
    // -----------------------------------------------------------------------

    /**
     * Attempt to withdraw {@code amount} strength as tokens.
     *
     * @return a {@link WithdrawResult} describing what happened
     */
    public WithdrawResult withdraw(StrengthPlayer player, int amount) {
        if (amount <= 0) return WithdrawResult.INVALID_AMOUNT;

        int current = getStrength(player);
        if (current - amount < config.getMinStrength()) {
            return WithdrawResult.INSUFFICIENT_STRENGTH;
        }

        setStrength(player, current - amount);
        platform.giveStrengthToken(player, amount);
        metrics.recordWithdraw(amount);
        return WithdrawResult.SUCCESS;
    }

    public enum WithdrawResult {
        SUCCESS,
        INVALID_AMOUNT,
        INSUFFICIENT_STRENGTH
    }
}