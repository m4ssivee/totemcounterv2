package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.util.TextFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatInputMixin {

    private void resetCounterAndScoreboard() {
        TotemCounterV2Mod.getInstance().getTotemTracker().clear();
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            // Mesajı direkt Text olarak oluştur (Türkçe karakter sorunlarını önlemek için)
            MutableText msg = Text.literal("")
                .append(Text.literal("[TotemCounter] ").setStyle(net.minecraft.text.Style.EMPTY.withColor(0x55FF55)))
                .append(Text.literal("Totem counter sifirlandi!").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xAAAAAA)));
            client.player.sendMessage(msg, false);
        }
        
        TotemCounterV2Mod.LOGGER.info("[TotemCounter] Counter and scoreboard reset by command");
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        
        String command = message.trim().toLowerCase();
        
        if ((command.matches("/k\\d+") || command.matches("/kit\\d+")) && 
            TotemCounterV2Mod.getInstance() != null && 
            TotemCounterV2Mod.getInstance().getConfig() != null &&
            TotemCounterV2Mod.getInstance().getConfig().autoCounterReset) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                TotemCounterV2Mod.getInstance().getTotemTracker().clearPlayer(client.player.getUuid());
                TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] Player counter cleared (kit change detected: {}) - Player: {}", 
                    command, client.player.getGameProfile().getName());
            }
            return;
        }
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void onSendChatCommand(String command, CallbackInfo ci) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        
        String cmd = command.trim().toLowerCase();
        
        if ((cmd.matches("k\\d+") || cmd.matches("kit\\d+")) &&
            TotemCounterV2Mod.getInstance() != null && 
            TotemCounterV2Mod.getInstance().getConfig() != null &&
            TotemCounterV2Mod.getInstance().getConfig().autoCounterReset) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                TotemCounterV2Mod.getInstance().getTotemTracker().clearPlayer(client.player.getUuid());
                TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] Player counter cleared (kit change detected: {}) - Player: {}", 
                    cmd, client.player.getGameProfile().getName());
            }
            return;
        }
    }
}

