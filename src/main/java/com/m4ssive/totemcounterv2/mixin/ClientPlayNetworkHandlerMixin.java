package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityStatus(Lnet/minecraft/network/packet/s2c/play/EntityStatusS2CPacket;)V", at = @At("HEAD"), require = 0)
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) (Object) this;
        
        if (handler.getWorld() == null) return;
        
        Entity entity = packet.getEntity(handler.getWorld());
        if (entity == null) return;
        
        if (packet.getStatus() == 35) {
            TotemCounterV2Mod.getInstance().getTotemTracker().recordTotemPop(entity);
            TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] Totem pop: {} (Total: {})", 
                entity.getName().getString(),
                TotemCounterV2Mod.getInstance().getTotemTracker().getTotemPops(entity));
        }
        
        else if (packet.getStatus() == 3) {
            TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
            if (mod != null && mod.getConfig() != null && mod.getConfig().autoCounterReset) {
                mod.getTotemTracker().clearPlayer(entity.getUuid());
                TotemCounterV2Mod.LOGGER.info("§c[TotemCounter] Player died, counter reset: {}", 
                    entity.getName().getString());
            }
        }
    }
    
    @Inject(method = "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
        if (mod == null || mod.getConfig() == null || !mod.getConfig().autoCounterReset) {
            return;
        }
        
        Text message = packet.content();
        if (message == null) {
            return;
        }
        
        String messageText = message.getString().toLowerCase();
        
        if (messageText.contains("kit") && (messageText.contains("load") || messageText.contains("çek") || 
            messageText.contains("aldı") || messageText.contains("yükle"))) {
            
            String plainText = message.getString();
            
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.world != null) {
                for (PlayerEntity player : client.world.getPlayers()) {
                    String playerName = player.getGameProfile().getName();
                    if (plainText.contains(playerName)) {
                        mod.getTotemTracker().clearPlayer(player.getUuid());
                        TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] {}'s counter reset - kit loaded detected in chat", playerName);
                        break;
                    }
                }
            }
        }
    }
}

