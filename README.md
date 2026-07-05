# TLM Chest Compat

A Forge mod for Minecraft 1.20.1 that extends [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) with inventory management, baubles, and Sophisticated Backpacks integration.

## Features

### Items (Creative Tab: TLM Chest Compat)

| Item | Recipe | Function |
|---|---|---|
| **Info Scanner** | Compass + Sugar | Right-click maid: GUI info panel. Sneak+click: +9000 all stats. Air click: HUD settings. |
| **Backpack Storage Core** | Ender Pearl + Blaze Rod (diagonal) | Auto-store items into Sophisticated Backpack in maid's Curios back slot. Auto-feed when hungry. |
| **Kill-Proof Charm** | Immortal Charm + Nether Star | Immune to ALL damage including `/kill`. Uses TLM API `onDeath()`. |
| **Effect Immune Charm** | Milk + Fermented Eye + Ender Pearl + Sugar | Removes blindness, mining fatigue, slowness every tick. |
| **Storage Marker** | Ender Chest + Ender Pearl | Right-click any container to bind. Maid auto-deposits items. Backpack has priority. |

### Commands (OP level 2)

- `/maidadd <attr> <value> [uuid]` — Add attribute to nearest/specified maid
- `/maidaddslot <slotType> [uuid]` — Add permanent Curios slot (Tab-completion for all registered slot types)

### HUD Overlay

Configurable overlay showing nearest maid's HP bar, armor, eat count, TPS, FPS.

Right-click air with Info Scanner to open HUD settings:
- Toggle ON/OFF
- 5 position presets
- Scale (0.5x ~ 2.0x)
- Opacity (0% ~ 100%)
- Update interval (3s ~ 600s)
- X/Y offset (-200 ~ 200)

### Maid Priority Chain

```
Backpack Storage Core + Backpack in back slot (highest priority)
    ↓ (backpack full / no backpack)
Storage Marker bound container
    ↓ (container full / no binding)
Items stay in maid inventory
    ↓ (inventory full → TLM handles it)
```

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
