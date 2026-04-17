package com.myst1cs04p.strength_smp.bukkit;

import com.myst1cs04p.strength_smp.common.platform.StrengthMetrics;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * bStats integration. Registers custom charts and implements the
 * {@link StrengthMetrics} interface so common logic can record events
 * without knowing bStats exists.
 */
public class BukkitMetrics implements StrengthMetrics {

    private static final int PLUGIN_ID = 29827;

    private final AtomicInteger tokensConsumed  = new AtomicInteger(0);
    private final AtomicInteger tokensDropped   = new AtomicInteger(0);
    private final AtomicInteger totalWithdrawn  = new AtomicInteger(0);

    // Distribution map for the strength level chart
    private final Map<String, AtomicInteger> strengthDistribution = new HashMap<>();

    public BukkitMetrics(JavaPlugin plugin) {
        try {
            Metrics metrics = new Metrics(plugin, PLUGIN_ID);
            registerCharts(metrics);
        } catch (Exception e) {
            plugin.getLogger().warning("[StrengthSMP] bStats failed to initialise (invalid plugin ID?). Metrics disabled. " + e.getMessage());
        }
    }

    private void registerCharts(Metrics metrics) {

        // How many tokens have been consumed since last report interval
        metrics.addCustomChart(new SingleLineChart("tokens_consumed", () -> {
            return tokensConsumed.getAndSet(0);
        }));

        // How many tokens have been dropped (killer at max strength)
        metrics.addCustomChart(new SingleLineChart("tokens_dropped", () -> {
            return tokensDropped.getAndSet(0);
        }));

        // Total strength withdrawn via /strength withdraw
        metrics.addCustomChart(new SingleLineChart("strength_withdrawn", () -> {
            return totalWithdrawn.getAndSet(0);
        }));

        // Distribution of strength levels across players (pie chart)
        metrics.addCustomChart(new AdvancedPie("strength_distribution", () -> {
            Map<String, Integer> snapshot = new HashMap<>();
            synchronized (strengthDistribution) {
                strengthDistribution.forEach((level, count) -> {
                    int value = count.getAndSet(0);
                    if (value > 0) snapshot.put(level, value);
                });
            }
            return snapshot;
        }));
    }

    // -----------------------------------------------------------------------
    // StrengthMetrics Implementation
    // -----------------------------------------------------------------------

    @Override
    public void recordStrengthChange(int newLevel) {
        String key = "Level " + newLevel;
        synchronized (strengthDistribution) {
            strengthDistribution
                .computeIfAbsent(key, k -> new AtomicInteger(0))
                .incrementAndGet();
        }
    }

    @Override
    public void recordTokenConsumed() {
        tokensConsumed.incrementAndGet();
    }

    @Override
    public void recordTokenDropped() {
        tokensDropped.incrementAndGet();
    }

    @Override
    public void recordWithdraw(int amount) {
        totalWithdrawn.addAndGet(amount);
    }
}