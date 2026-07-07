# TLM Chest Compat

A Forge mod for Minecraft 1.20.1 that extends [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) with inventory management, baubles, and Sophisticated Backpacks integration.

## Items (Creative Tab: TLM Chest Compat)

| Item | Recipe | Function |
|---|---|---|
| **Info Scanner** | Compass + Sugar | Right-click maid: GUI panel with stats. Sneak+click: +9000 all. Right-click air: HUD config. |
| **Backpack Storage Core** | Ender Pearl + Blaze Rod (diagonal) | Auto-store items into backpack in maid's Curios back slot. Auto-feed when hungry. |
| **Kill-Proof Charm** | Immortal Charm + Nether Star | Immune to ALL death including `/kill`. Dual protection: `onDeath()` + tick heartbeat. |
| **Effect Immune Charm** | Milk + Fermented Eye + Ender Pearl + Sugar | Removes blindness, mining fatigue, slowness every tick. |
| **Storage Marker** | Ender Chest + Ender Pearl | Bind any container (sneak+click). Maid auto-deposits items. Shift+click to set filter. |
| **Player Immortal Charm** | Creative/Command only | Player is immune to ALL damage including `/kill`. Works in curios slot or hand. |

## Features

### Info Scanner
- **Right-click maid** → GUI panel showing UUID, HP, Armor, Toughness, Damage, all growth stats
- **Sneak+right-click maid** → All growth stats +9000 (applied to entity attributes, not just NBT)
- **Right-click air** → HUD settings
- **Copy UUID** button in GUI
- Chat display with clickable UUID (copies to clipboard)

### Backpack Storage Core
- Requires Sophisticated Backpack in maid's Curios **back** slot
- Auto-transfers items from maid inventory into the backpack
- Auto-extracts food when maid is hungry
- If backpack is full, Storage Marker (if bound) takes over

### Kill-Proof Charm (True Immortal Bauble)
- Uses TLM API `onDeath()` → intercepts at `die()` first line, blocks ALL death including `/kill`
- `onTick()` heartbeat → restores health if somehow reduced to 0
- No `BYPASSES_INVULNERABILITY` check → blocks even custom/penetrating damage

### Effect Immune Charm
- Removes blindness, mining fatigue, slowness every tick

### Storage Marker
- **Sneak+right-click container** → bind/unbind toggle
- **Right-click air** → open filter GUI with 3x3 item grid
- **Shift+click** items to add/remove from filter
- **Whitelist/Blacklist** toggle button
- Maid auto-deposits items into bound container (backpack has priority)
- Auto-extracts food when hungry
- Container destroyed → binding auto-cleared

### SopChestExtension (Wireless IO)
- Registers Sophisticated Backpacks/Core blocks as valid Wireless IO chest types
- Works with TLM's native Wireless IO system

### HUD Overlay
- Shows nearest tamed maid's HP bar, armor, eat count, TPS, FPS
- Right-click air with Info Scanner to open settings
- Configurable: ON/OFF, position (5 presets), scale (0.5-2.0), opacity (0-100%), update interval (3-600s), X/Y offset

## Maid Priority Chain
```
Backpack Core + Backpack in back slot (highest priority)
  ↓ (backpack full / no backpack)
Storage Marker bound container
  ↓ (container full / no binding)
Items stay in maid inventory
  ↓ (inventory full → TLM handles it)
```

## Commands (OP level 2)

| Command | Description |
|---|---|
| `/maidadd <attr> <value> [uuid]` | Add attribute to nearest/specified maid |
| `/maidaddslot <slotType> [uuid]` | Add permanent Curios slot |
| `/maidaddslot <slotType> near <range>` | Add slot to nearest maid within range blocks |

Supported attributes: hp, armor, tough, dmg, speed, reach, dig, resist, explosion, thorns, crit, regen

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

## Credits

- [TartaricAcid](https://github.com/TartaricAcid) for Touhou Little Maid
- [TheIllusiveC4](https://github.com/TheIllusiveC4) for Curios API
- [P3pp3rF1y](https://github.com/P3pp3rF1y) for Sophisticated Backpacks/Core
