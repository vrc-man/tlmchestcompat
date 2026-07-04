# TLM Chest Compat

A Forge mod for Minecraft 1.20.1 that adds Sophisticated Backpacks/Storage integration to [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid).

## Features

### 1. Wireless IO Chest Type
Registers Sophisticated Backpacks and Core blocks as valid chest types for TLM's Wireless IO system. Place a Sophisticated Backpack/Chest block in the world and bind it with the Wireless IO binding item — maids can transfer items to/from it.

### 2. Backpack Storage Core
A bauble item for maids that provides automatic inventory management:

- **Auto-store**: Items in the maid's inventory are automatically transferred into a Sophisticated Backpack worn in the maid's Curios **back** slot.
- **Auto-feed**: When the maid's hunger drops below 15 and no food is in her inventory, food is extracted from the backpack into her inventory so her AI can eat it.

## Dependencies

- [Minecraft Forge](https://files.minecraftforge.net/) 47.3.33+
- [Touhou Little Maid](https://www.curseforge.com/minecraft/mc-mods/touhou-little-maid) 1.5.3+
- [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) 5.14.1+
- [Sophisticated Backpacks](https://www.curseforge.com/minecraft/mc-mods/sophisticated-backpacks) (optional, for backpack storage features)
- [Sophisticated Core](https://www.curseforge.com/minecraft/mc-mods/sophisticated-core) (optional)

## Usage

### Wireless IO Binding
1. Place a Sophisticated Backpack/Chest block in the world.
2. Use TLM's Wireless IO binding item on it.
3. Give the maid a Wireless IO bauble.
4. Set the maid to collect/store mode.

### Backpack Storage Core
1. Give the item: `/give @p tlmchestcompat:backpack_bauble`
2. Put a **Sophisticated Backpack** in the maid's Curios **back** slot.
3. Put the **Backpack Storage Core** in the maid's bauble slot.
4. The maid will automatically store items and feed from the backpack.

## Building

Requirements: JDK 17, Gradle 7.6.3 (wrapper included)

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "C:\Program Files\Java\jdk-17\bin;$env:Path"
.\gradlew.bat build
```

Output jar: `build/libs/tlmchestcompat-1.0.0.jar`

## Credits

- [TartaricAcid](https://github.com/TartaricAcid) for Touhou Little Maid
- [TheIllusiveC4](https://github.com/TheIllusiveC4) for Curios API
- [P3pp3rF1y](https://github.com/P3pp3rF1y) for Sophisticated Backpacks/Core
