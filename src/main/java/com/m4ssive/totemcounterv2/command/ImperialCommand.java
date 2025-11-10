package com.m4ssive.totemcounterv2.command;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.util.TextFormatter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ImperialCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("imperial")
            .executes(ImperialCommand::execute));
    }
    
    private static int execute(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.inGameHud != null) {
            MutableText title = TextFormatter.parse("§6§l§k|||§r §6§lİMPERİAL §6§l§k|||§r");
            MutableText subtitle = TextFormatter.parse("§eKatılmak için §7yagiwz'a §eulaş");
            client.inGameHud.setTitle(title);
            client.inGameHud.setTitleTicks(0, 100, 40);
            client.inGameHud.setSubtitle(subtitle);
            TotemCounterV2Mod.LOGGER.info("§6[TotemCounter] Imperial easter egg triggered!");
            
            MutableText feedbackMsg = TextFormatter.parse("§6[TotemCounter] Imperial easter egg activated!");
            context.getSource().sendFeedback(feedbackMsg);
        } else {
            MutableText errorMsg = TextFormatter.parse("§c[TotemCounter] Client bulunamadı!");
            context.getSource().sendError(errorMsg);
        }
        
        return 1;
    }
}





