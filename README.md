# TLM Chest Compat

A Forge mod for Minecraft 1.20.1 that extends [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) with inventory management, baubles, and Sophisticated Backpacks integration.

## Items

All items are available in the creative tab **TLM Chest Compat**.

| Item | Recipe | Function | Usage |
|---|---|---|---|
| **Info Scanner** | Compass + Sugar | View/set maid stats, configure HUD and slot locks | Right-click maid: GUI panel. Sneak+right-click maid (creative): +9000 all stats. Right-click air: HUD config. |
| **Backpack Storage Core** | Ender Pearl + Blaze Rod (diagonal) | Auto-store items & auto-feed from backpack | Put in maid's bauble slot. Requires Sophisticated Backpack in maid's Curios **back** slot. |
| **Kill-Proof Charm** (for maids) | Immortal Charm (`vefc:maid_immortal_charm`) + Nether Star | Maid immune to ALL damage including `/kill` | Put in maid's bauble slot. |
| **Effect Immune Charm** | Milk Bucket + Fermented Spider Eye + Ender Pearl + Sugar | Removes blindness, mining fatigue, slowness | Put in maid's bauble slot. |
| **Storage Marker** | Ender Chest + Ender Pearl | Bind any container for maid to auto-deposit | Sneak+right-click container to bind. Right-click air to open filter GUI. Put in maid's bauble slot. |
| **Player Immortal Charm** | Creative / Command only | Player immune to ALL damage including `/kill` | Hold in hand or place in Curios charm/belt/back slot. Right-click air: config screen (flight, night vision, slow falling, reflect). |
| **Maid Reflect Bauble** | Creative / Command only | Maid immune to ALL damage, negative effects, and knockback. Reflects configurable damage (0-10x). | Put in maid's bauble slot. Right-click air with item in hand: config reflect multiplier. |

## Features

### Info Scanner
- **Right-click maid**: Opens GUI panel showing UUID, HP, Armor, Toughness, Damage, all growth stats, and eat count.
- **Sneak+right-click maid (creative only)**: Boosts all growth stats by +9000 (applied to entity attributes, not just NBT).
- **Right-click air**: Opens HUD configuration screen.
- **Buttons in GUI**: HUD Settings, Copy UUID, Slot Lock config.
- **HUD Tracking**: Scanning a maid automatically tracks her on the HUD. Click the [Track] button in the GUI to toggle tracking.

### Backpack Storage Core
- Requires a **Sophisticated Backpack** in the maid's Curios **back** slot.
- **Auto-store**: Every 1 second, transfers items from maid's inventory into the backpack. Skips locked slots.
- **Auto-feed**: When the maid's hunger is low and no food is in inventory, extracts food from the backpack.
- **Filter**: No filter — stores all non-bauble items.
- **Backpack full fallback**: If the backpack is full and a Storage Marker is also bound, falls through to the container.

### Kill-Proof Charm (for maids)
- Uses TLM API `IMaidBauble.onDeath()` — intercepts at `die()` first line, returns `true` to block ALL death including `/kill`.
- `onTick()` heartbeat — restores health to max if somehow reduced to 0 or below.
- No `BYPASSES_INVULNERABILITY` check — blocks even custom/penetrating damage from modded weapons.
- **LivingHurtEvent** — cancels all damage directly (added layer).

### Effect Immune Charm
- Every tick, removes blindness (`BLINDNESS`), mining fatigue (`DIG_SLOWDOWN`), and slowness (`MOVEMENT_SLOWDOWN`).

### Storage Marker
- **Bind**: Sneak+right-click any container (chest, barrel, etc.) that has an inventory capability. Already bound? Unbinds.
- **Unbind**: Sneak+right-click the same container again, or sneak+right-click air.
- **Filter GUI**: Right-click air to open. 3x3 grid. Shift+click items to add/remove from whitelist/blacklist.
- **Auto-deposit**: Every 3 seconds, transfers items from maid's inventory into the bound container. Skips locked slots.
- **Auto-feed**: When hungry, extracts food from the container.
- **Item restock**: Pulls filtered items from the container when the maid has fewer than 4 in inventory (up to 16).
- **Event-driven pull**: Listens to `MaidRequestItemEvent` — when the maid's AI needs seeds, arrows, or other items, pulls them from the container on demand.
- **Backpack priority**: If Backpack Storage Core is also equipped, only activates when the backpack is full.

### Player Immortal Charm
- **LivingHurtEvent**: Cancels all damage to the player.
- Works in Curios charm, belt, or back slot, or held in main/off hand.
- **Right-click air**: Opens config screen with flight toggle, speed (0.5x-10x), slow falling, night vision, and reflect multiplier (0-10x).
- Creative / Command only — no recipe.

### Maid Reflect Bauble
- **Full immunity**: Cancels all damage including `/kill` via `LivingHurtEvent`.
- **Effect immunity**: Every tick removes 11 negative effects (poison, wither, slowness, mining fatigue, blindness, hunger, weakness, levitation, bad luck, bad omen, darkness).
- **Knockback resistance**: 100% knockback resistance forced every tick.
- **Configurable reflect**: Right-click air opens config screen. Reflects incoming damage back to the attacker at 0.5x-10x multiplier (default 1x). Also sets attacker on fire (multiplier × 2 seconds).
- Put in maid's bauble slot. Creative / Command only — no recipe.

### SopChestExtension (Wireless IO)
- Registers Sophisticated Backpacks/Core blocks as valid chest types for TLM's Wireless IO system using `@LittleMaidExtension` and `IChestType`.

### HUD Overlay
- **Always visible**: Shows TPS, FPS, real computer time, and config info even without a maid nearby.
- **When maid detected**: Shows the tracked or nearest maid's name, HP bar, armor value, and eat count.
- **Tracking**: Scans a maid with the Info Scanner → auto-tracks that maid. Toggle via [Track] button in the scanner GUI.
- **Configuration**: Right-click air with Info Scanner → HUD settings.
  - ON/OFF toggle
  - 5 position presets (Top-Left, Top-Center, Top-Right, Bottom-Left, Bottom-Right)
  - Scale (0.5x ~ 2.0x)
  - Opacity (0% ~ 100%)
  - Update interval (3s ~ 600s)
  - X/Y offset (-200 ~ 200)

### Slot Locking
- Lock specific slots in the maid's 36-slot inventory to prevent them from being stored by Backpack Storage Core or Storage Marker.
- **Config**: Info Scanner GUI → [Slot] button → 6x6 grid → click to toggle lock.
- **Stored on**: The maid's `persistentData` (shared between both baubles).
- **Security**: Only the owner, creative mode players, or OP level 2+ can modify locks.
- **Not affected**: Wireless IO has its own independent slot locking.

### Commands (OP level 2)

| Command | Description |
|---|---|
| `/maidadd <attr> <value> [uuid]` | Add attribute value to nearest/specified maid |
| `/maidaddslot <slotType> [count]` | Add permanent Curios slot (default 1) |
| `/maidaddslot <slotType> near <range> [count]` | Add slots to nearest maid within range |
| `/maidaddslot <slotType> <uuid> [count]` | Add slots to specified maid |

Supported attributes: `hp`, `armor`, `tough`, `dmg`, `speed`, `reach`, `dig`, `resist`, `explosion`, `thorns`, `crit`, `regen`

### Maid Priority Chain
```
Backpack Core + Backpack in back slot (highest priority)
  ↓ (backpack full / no backpack)
Storage Marker bound container
  ↓ (container full / no binding)
Items stay in maid inventory (TLM handles overflow)
```

### Maid AI Item Access
The mod listens to `MaidRequestItemEvent` — when the maid's AI needs items (seeds for farming, arrows for archery, etc.) and can't find them in inventory, the mod automatically pulls them from:
1. The Sophisticated Backpack in the back slot (if Backpack Storage Core is equipped)
2. The bound container (if Storage Marker is equipped)

Items are pulled on demand — no polling, no waste.

## Dependencies

- [Minecraft Forge](https://files.minecraftforge.net/) 47.3.33+
- [Touhou Little Maid](https://www.curseforge.com/minecraft/mc-mods/touhou-little-maid) 1.5.3+
- [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) 5.14.1+
- [Sophisticated Backpacks](https://www.curseforge.com/minecraft/mc-mods/sophisticated-backpacks) (optional)

## Building

Requirements: JDK 17, Gradle 7.6.3 (wrapper included)

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "C:\Program Files\Java\jdk-17\bin;$env:Path"
.\gradlew.bat build
```

Output: `build/libs/tlmchestcompat-1.0.0.jar`

## Credits & References

- [TartaricAcid](https://github.com/TartaricAcid) for Touhou Little Maid — `@LittleMaidExtension`, `IMaidBauble`, `MaidRequestItemEvent`, Wireless IO slot locking
- [TheIllusiveC4](https://github.com/TheIllusiveC4) for Curios API — `ICurioItem`, slot types, player curios integration
- [P3pp3rF1y](https://github.com/P3pp3rF1y) for Sophisticated Backpacks/Core — backpack inventory capability
- [yimeng261](https://github.com/yimeng261) for Touhou Little Maid Spell — reference for `@LittleMaidExtension` registration pattern and bauble structure
