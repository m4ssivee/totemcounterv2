package com.m4ssive.totemcounterv2.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow @Final protected TextFieldWidget chatField;
    
    @Inject(
        method = "keyPressed",
        at = @At("HEAD"),
        cancellable = true
    )
    private void totemcounterv2$handleTabCompletion(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode != 258) {
            return;
        }
        
        String text = chatField.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        
        String trimmed = text.trim();
        if (!trimmed.startsWith("/setpops ") && !trimmed.startsWith("/addpops ")) {
            return;
        }
        
        String[] parts = trimmed.split(" ", 3);
        if (parts.length < 2) {
            return;
        }
        
        String command = parts[0];
        String playerNamePrefix = parts.length > 1 ? parts[1] : "";
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) {
            return;
        }
        
        List<String> matchingPlayers = new ArrayList<>();
        ClientPlayNetworkHandler nh = client.getNetworkHandler();
        for (PlayerListEntry entry : nh.getPlayerList()) {
            String name = entry.getProfile().getName();
            if (name.toLowerCase().startsWith(playerNamePrefix.toLowerCase())) {
                matchingPlayers.add(name);
            }
        }
        
        if (client.player != null) {
            String selfName = client.player.getGameProfile().getName();
            if (selfName.toLowerCase().startsWith(playerNamePrefix.toLowerCase()) && !matchingPlayers.contains(selfName)) {
                matchingPlayers.add(selfName);
            }
        }
        
        if (matchingPlayers.isEmpty()) {
            return;
        }
        
        String selected = matchingPlayers.get(0);
        if (parts.length == 2) {
            chatField.setText(command + " " + selected + " ");
            chatField.setCursorToEnd(false);
            cir.setReturnValue(true);
        }
    }
}