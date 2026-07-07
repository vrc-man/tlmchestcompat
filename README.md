# TLM Chest Compat

A Forge mod for Minecraft 1.20.1 that extends [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) with inventory management, baubles, and Sophisticated Backpacks integration.

## Items

All items are available in the creative tab **TLM Chest Compat**. Use `/give @p tlmchestcompat:<id>` to obtain via command.

| # | Item | ID | Recipe | Get Command |
|---|---|---|---|---|
| 1 | **Info Scanner** | `info_scanner` | 无序合成：指南针 + 糖 | `/give @p tlmchestcompat:info_scanner` |
| 2 | **Backpack Storage Core** | `backpack_bauble` | 有序合成：末影珍珠（左上）+ 烈焰棒（右下） | `/give @p tlmchestcompat:backpack_bauble` |
| 3 | **Kill-Proof Charm** | `true_immortal_bauble` | 无序合成：`vefc:maid_immortal_charm` + 下界之星 | `/give @p tlmchestcompat:true_immortal_bauble` |
| 4 | **Effect Immune Charm** | `effect_immune_bauble` | 有序合成：牛奶桶（上中）+ 发酵蛛眼（左中）+ 末影珍珠（中中）+ 糖（下中） | `/give @p tlmchestcompat:effect_immune_bauble` |
| 5 | **Storage Marker** | `storage_marker` | 无序合成：末影箱 + 末影珍珠 | `/give @p tlmchestcompat:storage_marker` |
| 6 | **Player Immortal Charm** | `player_immortal_bauble` | ❌ 无配方，仅创造/命令 | `/give @p tlmchestcompat:player_immortal_bauble` |
| 7 | **Maid Reflect Bauble** | `maid_reflect_bauble` | ❌ 无配方，仅创造/命令 | `/give @p tlmchestcompat:maid_reflect_bauble` |

## Features

### ① Info Scanner — 信息扫描器

**获取方式**：合成（指南针 + 糖）或 `/give @p tlmchestcompat:info_scanner`

| 交互 | 效果 |
|---|---|
| **右键女仆** | 打开信息面板：UUID、血量、护甲、韧性、伤害、全部 13 项成长属性、进食次数 |
| **潜行+右键女仆（创造模式）** | 全部成长属性 +9000（同时写入实体属性，无需重启） |
| **右键空气** | 打开 HUD 配置界面 |
| **面板按钮** | [HUD设置]、[复制UUID]、[槽位锁定]（6×6 开关网格） |
| **HUD 追踪** | 扫描女仆后自动追踪；面板内 [追踪] 按钮可切换 |

### ② Backpack Storage Core — 背饰背包核心

**获取方式**：合成（末影珍珠 + 烈焰棒 对角摆放）或 `/give @p tlmchestcompat:backpack_bauble`

- **条件**：女仆 Curios **背** 饰槽必须装有精妙背包（Sophisticated Backpack）
- **自动存入**：每 1 秒将女仆物品栏物品存入背包（跳过锁定槽位）
- **自动取食**：女仆饥饿时从背包提取食物喂食
- **优先级**：背包满了且有 Storage Marker 时，自动降级到容器

### ③ Kill-Proof Charm — Kill不死护符

**获取方式**：合成（`vefc:maid_immortal_charm` + 下界之星）或 `/give @p tlmchestcompat:true_immortal_bauble`

- **完全免伤**：`IMaidBauble.onDeath()` + `LivingHurtEvent` 双层拦截，阻挡包括 `/kill` 在内的所有伤害
- **心跳复活**：每 tick 检查血量，<=0 时立即恢复满血
- **注意**：已被 **女仆反伤护符** 完全取代（后者多了负面免疫、抗击退、可调反伤）

### ④ Effect Immune Charm — 状态免疫护符

**获取方式**：合成（牛奶桶 + 发酵蛛眼 + 末影珍珠 + 糖）或 `/give @p tlmchestcompat:effect_immune_bauble`

- 每 tick 清除 3 种负面效果：失明（BLINDNESS）、挖掘疲劳（DIG_SLOWDOWN）、缓慢（MOVEMENT_SLOWDOWN）

### ⑤ Storage Marker — 存储标记

**获取方式**：合成（末影箱 + 末影珍珠）或 `/give @p tlmchestcompat:storage_marker`

| 交互 | 效果 |
|---|---|
| **潜行+右键容器** | 绑定/解绑容器（箱子、桶等有库存能力的方块） |
| **潜行+右键空气** | 解绑当前容器 |
| **右键空气** | 打开过滤 GUI（3×3 网格，Shift+点击添加/移除物品，白名单/黑名单切换） |
| **自动存入** | 每 3 秒将女仆物品栏物品存入绑定容器（跳过锁定槽位） |
| **自动取食** | 女仆饥饿时从容器提取食物 |
| **物品补充** | 女仆物品栏某物品 <4 个时，从容器的过滤列表中补充到 16 个 |
| **事件驱动** | 监听 `MaidRequestItemEvent`——女仆 AI 需要种子/箭等时自动从容器提取 |
| **背包优先** | 同时装有背饰背包核心时，仅在背包满后激活 |

### ⑥ Player Immortal Charm — 玩家无敌饰品

**获取方式**：❌ 无配方，仅 `/give @p tlmchestcompat:player_immortal_bauble` 或创造模式

**佩戴位置**：Curios charm/belt/back 槽位，或主手/副手/背包任意位置

| 功能 | 说明 |
|---|---|
| **完全免伤** | 取消所有伤害包括 `/kill` |
| **常驻 Buff** | 每 tick 施加：饱和 IV、再生 II、吸收 IV、防火、水下呼吸、抗性 IV |
| **负面免疫** | 每 tick 清除 11 种 DEBUFF：中毒、凋零、缓慢、挖掘疲劳、失明、饥饿、虚弱、漂浮、霉运、不祥之兆、黑暗 |
| **夜视** | 可在配置界面开关 |
| **抗击退** | 100% 抗击退（每 tick 强制） |
| **右键空气** | 打开配置界面：飞行开关 + 飞行速度（0.5x~10x）+ 缓降开关 + 夜视开关 + 反伤倍率（0x~10x，步进 0.5） |
| **反伤机制** | 倍率 > 0 时，将所受伤害 × 倍率反伤为魔法伤害 + 攻击者着火（倍率 × 2 秒） |

### ⑦ Maid Reflect Bauble — 女仆反伤护符

**获取方式**：❌ 无配方，仅 `/give @p tlmchestcompat:maid_reflect_bauble` 或创造模式

**佩戴位置**：女仆饰品槽

| 功能 | 说明 |
|---|---|
| **完全免伤** | 取消所有伤害包括 `/kill` |
| **负面免疫** | 每 tick 清除 11 种 DEBUFF（同玩家无敌饰品） |
| **抗击退** | 100% 抗击退（每 tick 强制） |
| **右键空气** | 打开倍率配置界面（0x~10x，步进 0.5，默认 1x） |
| **反伤机制** | 倍率 > 0 时，将所受伤害 × 倍率反伤为魔法伤害 + 攻击者着火（倍率 × 2 秒） |
| **全功能包含** | ✅ Kill不死护符 + ✅ 状态免疫护符 的完整功能，无需再装备这两者 |

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

## KubeJS Server Scripts

The mod includes companion KubeJS scripts (place in `kubejs/server_scripts/`):

### `maid_food_stats.js`
- When a maid eats food, randomly increases 1 of 11 growth attributes (HP, Armor, Toughness, Damage, Speed, Reach, Dig, Resist, Explosion, Thorns, Crit, Regen).
- Restores XP if the maid's owner is online.
- If the maid has no owner, stats are saved to the maid's persistent data for later assignment when claimed.

### `maid_immortal.js`
- On maid spawn, checks if the maid's name contains "灵" (Ling). If so, adds permanent immunity buffs (Saturation IV, Regeneration IV, Absorption IV, Resistance IV) that reapply every 5 seconds.
- When such a maid takes damage, she regains full health and heals nearby players within 10 blocks.

### `maid_curios_slots.js`
- `/maidcurios reload` — Reloads slot config from a JSON file (`kubejs/assets/curios/`).
- `/maidcurios add <slotType> [count]` — Adds permanent slots to the nearest maid.
- **Sneak + right-click** a maid with any item to cycle through Curios slot types and auto-add one slot.

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
