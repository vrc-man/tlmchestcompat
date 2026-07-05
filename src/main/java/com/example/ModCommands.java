package com.example;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "tlmchestcompat")
public class ModCommands {
    private static final Map<String, String> ATTR_MAP = Map.ofEntries(
        Map.entry("hp", "eatHP"),
        Map.entry("armor", "attrArmor"),
        Map.entry("tough", "attrTough"),
        Map.entry("dmg", "attrDmg"),
        Map.entry("speed", "attrSpeed"),
        Map.entry("reach", "attrReach"),
        Map.entry("dig", "attrDig"),
        Map.entry("resist", "attrResist"),
        Map.entry("explosion", "attrExplosion"),
        Map.entry("thorns", "attrThorns"),
        Map.entry("crit", "attrCrit"),
        Map.entry("regen", "attrRegenPct")
    );

    private static final SuggestionProvider<CommandSourceStack> SLOT_SUGGESTIONS =
        (ctx, builder) -> {
            for (var slot : CuriosApi.getSlots(true).keySet()) {
                builder.suggest(slot);
            }
            return builder.buildFuture();
        };

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var d = event.getDispatcher();

        d.register(Commands.literal("maidadd")
            .requires(s -> s.hasPermission(2))
            .then(Commands.argument("attr", StringArgumentType.word())
                .suggests((ctx, b) -> SharedSuggestionProvider.suggest(ATTR_MAP.keySet(), b))
                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                    .executes(ctx -> addAttr(ctx.getSource(),
                        StringArgumentType.getString(ctx, "attr"),
                        DoubleArgumentType.getDouble(ctx, "value"), null))
                    .then(Commands.argument("uuid", StringArgumentType.word())
                        .executes(ctx -> addAttr(ctx.getSource(),
                            StringArgumentType.getString(ctx, "attr"),
                            DoubleArgumentType.getDouble(ctx, "value"),
                            StringArgumentType.getString(ctx, "uuid")))))));

        d.register(Commands.literal("maidaddslot")
            .requires(s -> s.hasPermission(2))
            .then(Commands.argument("slotType", StringArgumentType.word())
                .suggests(SLOT_SUGGESTIONS)
                .executes(ctx -> addSlot(ctx.getSource(),
                    StringArgumentType.getString(ctx, "slotType"), null, 8))
                .then(Commands.literal("near")
                    .then(Commands.argument("range", IntegerArgumentType.integer(1, 100))
                        .executes(ctx -> addSlot(ctx.getSource(),
                            StringArgumentType.getString(ctx, "slotType"), null,
                            IntegerArgumentType.getInteger(ctx, "range")))))
                .then(Commands.argument("uuid", StringArgumentType.word())
                    .executes(ctx -> addSlot(ctx.getSource(),
                        StringArgumentType.getString(ctx, "slotType"),
                        StringArgumentType.getString(ctx, "uuid"), 8)))));
    }

    private static int addAttr(CommandSourceStack src, String attr, double value, String uuid) {
        ServerPlayer player;
        try { player = src.getPlayerOrException(); } catch (Exception e) { return 0; }
        var maid = findMaid(player, uuid, 8);
        if (maid == null) return 0;

        var key = ATTR_MAP.get(attr.toLowerCase());
        if (key == null) {
            player.sendSystemMessage(Component.literal("\u00A7c\u672A\u77E5\u5C5E\u6027: " + attr));
            return 0;
        }

        var d = maid.getPersistentData();
        if (key.equals("attrArmor")) {
            d.putInt(key, d.getInt(key) + (int) value * 2);
        } else if (key.equals("attrReach") || key.equals("attrSpeed") || key.equals("attrRegenPct")) {
            d.putDouble(key, d.getDouble(key) + value);
        } else {
            d.putInt(key, d.getInt(key) + (int) value);
        }

        player.sendSystemMessage(Component.literal("\u00A7a\u5DF2\u4E3A " + maid.getName().getString()
            + " \u6DFB\u52A0 " + attr + " +" + (int) value));
        return 1;
    }

    private static int addSlot(CommandSourceStack src, String slotType, String uuid, int range) {
        ServerPlayer player;
        try { player = src.getPlayerOrException(); } catch (Exception e) { return 0; }

        if (!CuriosApi.getSlots(true).containsKey(slotType)) {
            player.sendSystemMessage(Component.literal("\u00A7c\u69FD\u4F4D\u7C7B\u578B\u4E0D\u5B58\u5728: " + slotType));
            var available = String.join(", ", CuriosApi.getSlots(true).keySet());
            player.sendSystemMessage(Component.literal("\u00A77\u53EF\u7528\u69FD\u4F4D: " + available));
            return 0;
        }

        var maid = findMaid(player, uuid, range);
        if (maid == null) return 0;

        CuriosApi.getCuriosInventory(maid).ifPresent(handler -> {
            var sig = "addPermanentSlotModifier(java.lang.String,java.util.UUID," +
                "java.lang.String,double," +
                "net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation)";
            try {
                var method = handler.getClass().getMethod(sig,
                    String.class, UUID.class, String.class, double.class,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.class);
                method.invoke(handler, slotType, UUID.randomUUID(),
                    "cmd_" + slotType, 1.0,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION);
                player.sendSystemMessage(Component.literal("\u00A7a\u5DF2\u4E3A " + maid.getName().getString()
                    + " \u589E\u52A0 1 \u4E2A\u00A7e" + slotType + " \u00A7a\u69FD\u4F4D"));
            } catch (Exception e) {
                player.sendSystemMessage(Component.literal("\u00A7c\u9519\u8BEF: " + e.getMessage()));
            }
        });
        return 1;
    }

    private static TamableAnimal findMaid(ServerPlayer player, String uuid, int range) {
        var level = player.serverLevel();

        if (uuid != null) {
            try {
                var u = UUID.fromString(uuid);
                var entity = level.getEntity(u);
                if (entity instanceof TamableAnimal maid) return maid;
                player.sendSystemMessage(Component.literal("\u00A7c\u672A\u627E\u5230 UUID: " + uuid));
                return null;
            } catch (Exception e) {
                player.sendSystemMessage(Component.literal("\u00A7c\u65E0\u6548 UUID"));
                return null;
            }
        }

        var aabb = new AABB(player.blockPosition()).inflate(range);
        var maids = level.getEntitiesOfClass(TamableAnimal.class, aabb,
            m -> m.getOwner() == player);

        if (maids.isEmpty()) {
            player.sendSystemMessage(Component.literal("\u00A7c" + range + "\u683C\u5185\u6CA1\u6709\u5973\u4EC6"));
            return null;
        }

        TamableAnimal nearest = null;
        double minDist = 999;
        for (var m : maids) {
            double d = m.distanceToSqr(player);
            if (d < minDist) {
                minDist = d;
                nearest = m;
            }
        }
        return nearest;
    }
}
