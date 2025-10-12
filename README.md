# StrengthSMP
---
# Features

## Kill-based progression
- Killing another player increases your strength.
- Dying to another player decreases it.
- Both limits (min/max) are fully configurable.

## Damage Multiplier System
- Strength is applied as a **total damage multiplier**, not a flat bonus.
- This can be configured in the `config.yml` file.

## Persistence
- Strength values save automatically to disk and reload on startup.
- No database setup. Simple `.yml` file.

## Craftable Strength Tokens
- Configurable 3×3 recipe via `config.yml`.
- Uses Bukkit’s recipe system (**no client mods required**).
- Tokens are consumable with right-click to permanently gain +1 Strength.

## Overflow Rewards
- Killing a player at max strength drops a strength token instead of wasting the strength.

## Commands for mortals and gods
- `/strength get` — See your current strength level.
- `/strength set <player> <amount>` — (OP only) Manually set someone’s strength.
- `/strength withdraw <amount>` — Convert part of your strength into tradable tokens.
- `/strength reload` — Reload config without restarting.

## Offline tracking
- Player data persists even when they’re offline.
- No resets between restarts unless you delete the data file yourself.

## Custom crafting
Recipe shape and materials configurable in `config.yml`.

Example:
```yaml
strength-item:
  enabled: true
  recipe:
    key: strength_token
    shape:
      - "ABA"
      - "BCB"
      - "ADA"
    A: DIAMOND
    B: BLAZE_POWDER
    C: NETHER_STAR
    D: BLAZE_ROD
```
# Permissions
- `strength.get` — Allows you to view other player's strength (default: op)
- `strength.set` — Allows you to set any player's strength (default: op)

# API/Integration
Other plugins can hook into the system via:
```java
StrengthManager manager = ((Main) Bukkit.getPluginManager().getPlugin("Strength_SMP")).getStrengthManager();
int current = manager.getStrength(player);
manager.setStrength(player, newAmount);
```
# Requirements
- Java 17

# Credits
- Author: Myst1cS04p(me)
- Contributors: ChatGPT(the goat)
- License: MIT(who really even cares lol?)

  `note for anyone that attempts to modify the plugin: my code is kinda buns, so gl with that.`
