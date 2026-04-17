package com.myst1cs04p.strength_smp.common.messaging;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Central factory for every user-facing Component.
 * Nothing outside this class should be constructing chat messages.
 * All strings live here - change wording in one place.
 */
public final class Messages {

    private Messages() {}

    // Brand accent colours
    private static final TextColor BRAND_RED    = TextColor.color(255, 85, 85);
    private static final TextColor BRAND_ORANGE = TextColor.color(255, 165, 0);

    // -----------------------------------------------------------------------
    // Strength info
    // -----------------------------------------------------------------------

    public static Component yourStrength(int level) {
        return prefix()
            .append(Component.text("Your strength: ", NamedTextColor.GREEN))
            .append(Component.text(level, NamedTextColor.GOLD));
    }

    public static Component targetStrength(String name, int level) {
        return prefix()
            .append(Component.text(name + "'s strength: ", NamedTextColor.GREEN))
            .append(Component.text(level, NamedTextColor.GOLD));
    }

    public static Component strengthSet(String name, int level) {
        return Component.text("Set " + name + "'s strength to " + level, NamedTextColor.GREEN);
    }

    public static Component yourStrengthWasSet(int level) {
        return Component.text("Your strength was set to " + level, NamedTextColor.YELLOW);
    }

    // -----------------------------------------------------------------------
    // Token events
    // -----------------------------------------------------------------------

    public static Component tokenAbsorbed(int newLevel) {
        return Component.text("You absorbed strength! Your level is now ", NamedTextColor.GREEN)
            .append(Component.text(newLevel, NamedTextColor.GOLD));
    }

    public static Component alreadyMaxStrength() {
        return Component.text("You're already at max strength!", NamedTextColor.RED);
    }

    public static Component withdrew(int amount) {
        return Component.text("Withdrew ", NamedTextColor.GREEN)
            .append(Component.text(amount, NamedTextColor.GOLD))
            .append(Component.text(" strength and received ", NamedTextColor.GREEN))
            .append(Component.text(amount, NamedTextColor.GOLD))
            .append(Component.text(" tokens.", NamedTextColor.GREEN));
    }

    public static Component notEnoughStrength() {
        return Component.text("You ain't got that much strength twin 🥀", NamedTextColor.RED);
    }

    public static Component invalidWithdrawAmount() {
        return Component.text("You can't withdraw nothing.", NamedTextColor.RED);
    }

    // -----------------------------------------------------------------------
    // Error messages
    // -----------------------------------------------------------------------

    public static Component noPermission() {
        return Component.text("You lack permission.", NamedTextColor.RED);
    }

    public static Component noPermissionCheckOthers() {
        return Component.text("You don't have perms to check other people's strength.", NamedTextColor.RED);
    }

    public static Component playerNotOnline() {
        return Component.text("Bro isn't even online 😭", NamedTextColor.RED);
    }

    public static Component consoleOnly() {
        return Component.text("You can't run this from console.", NamedTextColor.RED);
    }

    public static Component playerOnly() {
        return Component.text("Only players can do this 🙏", NamedTextColor.RED);
    }

    public static Component notANumber() {
        return Component.text("That wasn't a number gng. Go back to school bro 😭", NamedTextColor.RED);
    }

    public static Component unknownSubcommand() {
        return Component.text("Unknown command. Try /strength help", NamedTextColor.RED);
    }

    public static Component usageSet(String label) {
        return Component.text("Usage: /" + label + " set <player> <amount>", NamedTextColor.RED);
    }

    public static Component usageWithdraw(String label) {
        return Component.text("Usage: /" + label + " withdraw <amount>", NamedTextColor.RED);
    }

    public static Component usageRoot(String label, boolean hasSet) {
        String subs = hasSet ? "<get|set|withdraw|reload|ver>" : "<get|withdraw|ver>";
        return Component.text("Usage: /" + label + " " + subs, NamedTextColor.YELLOW);
    }

    public static Component reloadSuccess() {
        return Component.text("Strength plugin reloaded.", NamedTextColor.GREEN);
    }

    public static Component usageGet(String label) {
        return Component.text("Usage: /" + label + " get <player>", NamedTextColor.RED);
    }


    // -----------------------------------------------------------------------
    // Version / update nag
    // -----------------------------------------------------------------------

    /**
     * Shown in console and broadcast to all online players every 12 hours when
     * a newer version is available. Intentionally loud.
     */
    public static Component updateAvailable(String currentVersion, String latestVersion) {
        return Component.empty()
            .append(Component.text("========================================", BRAND_RED))
            .append(Component.newline())
            .append(Component.text(" ⚠  StrengthSMP is OUT OF DATE  ⚠", BRAND_RED)
                .decorate(TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text(" Running: ", NamedTextColor.GRAY))
            .append(Component.text(currentVersion, NamedTextColor.RED))
            .append(Component.text("  ->  Latest: ", NamedTextColor.GRAY))
            .append(Component.text(latestVersion, NamedTextColor.GREEN))
            .append(Component.newline())
            .append(Component.text(" Update at: ", NamedTextColor.GRAY))
            .append(Component.text("https://github.com/Myst1cS04p/StrengthSMP/releases", BRAND_ORANGE))
            .append(Component.newline())
            .append(Component.text("========================================", BRAND_RED));
    }

    /**
     * Shown to admins/ops when they join and the server is outdated.
     */
    public static Component updateNagJoin(String currentVersion, String latestVersion) {
        return Component.text("[StrengthSMP] ", BRAND_RED)
            .append(Component.text("Update available! ", NamedTextColor.YELLOW))
            .append(Component.text(currentVersion, NamedTextColor.RED))
            .append(Component.text(" -> ", NamedTextColor.GRAY))
            .append(Component.text(latestVersion, NamedTextColor.GREEN))
            .append(Component.text(" | github.com/Myst1cS04p/StrengthSMP/releases", NamedTextColor.AQUA));
    }

    public static Component version(String pluginVersion) {
        return Component.text("You are running version ", NamedTextColor.GREEN)
            .append(Component.text(pluginVersion, NamedTextColor.GOLD))
            .append(Component.text(" of ", NamedTextColor.GREEN))
            .append(Component.text("Myst1c's Strength SMP", BRAND_RED));
    }

    // -----------------------------------------------------------------------
    // Help menu
    // -----------------------------------------------------------------------

    public static Component helpHeader() {
        return Component.text("----- Strength Commands -----", NamedTextColor.YELLOW);
    }

    public static Component helpLine(String label, String args, String description) {
        return Component.text("/" + label, NamedTextColor.AQUA)
            .append(Component.text(args.isEmpty() ? "" : " " + args, NamedTextColor.WHITE))
            .append(Component.text(" - " + description, NamedTextColor.WHITE));
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private static Component prefix() {
        return Component.text("[Strength] ", BRAND_RED);
    }
}
