package com.m4ssive.totemcounterv2.command;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.util.TextFormatter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ResetScoreboardCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("resetscoreboard")
            .executes(ResetScoreboardCommand::execute));
    }
    
    private static int execute(CommandContext<FabricClientCommandSource> context) {
        TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
        if (mod == null) {
            MutableText errorMsg = Text.literal("")
                .append(Text.literal("[TotemCounter] ").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xFF5555)))
                .append(Text.literal("Mod instance not found!").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xFF5555)));
            context.getSource().sendError(errorMsg);
            return 0;
        }
        
        var tracker = mod.getTotemTracker();
        if (tracker == null) {
            MutableText errorMsg = Text.literal("")
                .append(Text.literal("[TotemCounter] ").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xFF5555)))
                .append(Text.literal("Tracker not found!").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xFF5555)));
            context.getSource().sendError(errorMsg);
            return 0;
        }
        
        int clearedCount = tracker.getAllPops().size();
        tracker.clear();
        
        // Mesajı direkt Text olarak oluştur (Türkçe karakter sorunlarını önlemek için)
        MutableText formattedMsg = Text.literal("")
            .append(Text.literal("[TotemCounter] ").setStyle(net.minecraft.text.Style.EMPTY.withColor(0x55FF55)))
            .append(Text.literal("Totem sayaci sifirlandi! ").setStyle(net.minecraft.text.Style.EMPTY.withColor(0x55FF55)))
            .append(Text.literal("(" + clearedCount + " oyuncu temizlendi)").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xAAAAAA)));
        
        context.getSource().sendFeedback(formattedMsg);
        
        TotemCounterV2Mod.LOGGER.info("Totem counter reset by resetscoreboard command - {} players cleared", clearedCount);
        
        return 1;
    }
}





