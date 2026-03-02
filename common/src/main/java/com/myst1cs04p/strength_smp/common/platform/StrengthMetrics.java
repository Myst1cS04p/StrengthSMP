package com.myst1cs04p.strength_smp.common.platform;

/**
 * Allows common logic to record metric events without knowing about bStats.
 * The bukkit layer implements this by wiring calls to bStats custom charts.
 */
public interface StrengthMetrics {

    /**
     * Called whenever a player's strength level changes.
     * Used to feed the strength distribution chart.
     *
     * @param newLevel the level the player just moved to
     */
    void recordStrengthChange(int newLevel);

    /**
     * Called whenever a strength token is consumed via right-click.
     */
    void recordTokenConsumed();

    /**
     * Called whenever a strength token is dropped into the world
     * (killer was at max strength).
     */
    void recordTokenDropped();

    /**
     * Called whenever a player uses /strength withdraw.
     *
     * @param amount number of tokens withdrawn
     */
    void recordWithdraw(int amount);
}
