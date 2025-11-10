package com.m4ssive.totemcounterv2.command;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.TotemTracker;
import com.m4ssive.totemcounterv2.util.TextFormatter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SetPopsCommand {
    private static final SuggestionProvider<FabricClientCommandSource> PLAYER_SUGGESTIONS = (context, builder) -> {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) {
            return CompletableFuture.completedFuture(builder.build());
        }
        
        String input = builder.getRemaining().toLowerCase();
        ClientPlayNetworkHandler nh = client.getNetworkHandler();
        
        for (PlayerListEntry entry : nh.getPlayerList()) {
            String name = entry.getProfile().getName();
            if (name.toLowerCase().startsWith(input)) {
                builder.suggest(name);
            }
        }
        
        if (client.player != null) {
            String selfName = client.player.getGameProfile().getName();
            if (selfName.toLowerCase().startsWith(input)) {
                builder.suggest(selfName);
            }
        }
        
        return CompletableFuture.completedFuture(builder.build());
    };
    
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("setpops")
            .then(ClientCommandManager.argument("player", StringArgumentType.word())
                .suggests(PLAYER_SUGGESTIONS)
                .then(ClientCommandManager.argument("count", IntegerArgumentType.integer(Integer.MIN_VALUE, Integer.MAX_VALUE))
                    .executes(SetPopsCommand::execute))));

        dispatcher.register(ClientCommandManager.literal("addpops")
            .then(ClientCommandManager.argument("player", StringArgumentType.word())
                .suggests(PLAYER_SUGGESTIONS)
                .then(ClientCommandManager.argument("delta", IntegerArgumentType.integer(Integer.MIN_VALUE, Integer.MAX_VALUE))
                    .executes(SetPopsCommand::executeAdd))));
    }

    private static UUID resolvePlayerUuid(String name) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) return null;
        ClientPlayNetworkHandler nh = client.getNetworkHandler();
        for (PlayerListEntry e : nh.getPlayerList()) {
            if (e.getProfile().getName().equalsIgnoreCase(name)) {
                return e.getProfile().getId();
            }
        }
        if (client.player != null && client.player.getGameProfile().getName().equalsIgnoreCase(name)) {
            return client.player.getUuid();
        }
        return null;
    }

    private static int execute(CommandContext<FabricClientCommandSource> ctx) {
        try {
            String name = StringArgumentType.getString(ctx, "player");
            Integer count = IntegerArgumentType.getInteger(ctx, "count");

            UUID id = resolvePlayerUuid(name);
            if (id == null) {
                MutableText errorMsg = TextFormatter.parse("§c[TotemCounter] Player not found: " + name);
                ctx.getSource().sendError(errorMsg);
                return 0;
            }

            TotemTracker tracker = TotemCounterV2Mod.getInstance().getTotemTracker();
            tracker.setTotemPops(id, count);

            MutableText successMsg = TextFormatter.parse("§a[TotemCounter] Set §e" + name + "§a pops to §6" + count);
            ctx.getSource().sendFeedback(successMsg);
            return 1;
        } catch (Exception e) {
            MutableText errorMsg = TextFormatter.parse("§c[TotemCounter] Usage: /setpops <player> <count>");
            ctx.getSource().sendError(errorMsg);
            return 0;
        }
    }

    private static int executeAdd(CommandContext<FabricClientCommandSource> ctx) {
        try {
            String name = StringArgumentType.getString(ctx, "player");
            int delta = IntegerArgumentType.getInteger(ctx, "delta");

            UUID id = resolvePlayerUuid(name);
            if (id == null) {
                MutableText errorMsg = TextFormatter.parse("§c[TotemCounter] Player not found: " + name);
                ctx.getSource().sendError(errorMsg);
                return 0;
            }

            TotemTracker tracker = TotemCounterV2Mod.getInstance().getTotemTracker();
            tracker.addTotemPops(id, delta);

            int newVal = tracker.getTotemPops(id);
            MutableText successMsg = TextFormatter.parse("§a[TotemCounter] Added §6" + delta + " §ato §e" + name + "§a (now §6" + newVal + "§a)");
            ctx.getSource().sendFeedback(successMsg);
            return 1;
        } catch (Exception e) {
            MutableText errorMsg = TextFormatter.parse("§c[TotemCounter] Usage: /addpops <player> <delta>");
            ctx.getSource().sendError(errorMsg);
            return 0;
        }
    }
}


