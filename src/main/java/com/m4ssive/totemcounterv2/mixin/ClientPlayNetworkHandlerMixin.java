package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Totem pop event detection mixin
 * Based on uku3lig's totemcounter implementation
 * Entity status 35 = Totem of Undying activated
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityStatus", at = @At("HEAD"))
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) (Object) this;
        
        // Null check for world (can be null during login/disconnect)
        if (handler.getWorld() == null) return;
        
        Entity entity = packet.getEntity(handler.getWorld());
        if (entity == null) return;
        
        // Status byte 35 = Totem of Undying activated
        if (packet.getStatus() == 35) {
            TotemCounterV2Mod.getInstance().getTotemTracker().recordTotemPop(entity);
            TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] Totem pop: {} (Total: {})", 
                entity.getName().getString(),
                TotemCounterV2Mod.getInstance().getTotemTracker().getTotemPops(entity));
        }
        
        // Status byte 3 = Player died - Reset totem count
        else if (packet.getStatus() == 3) {
            TotemCounterV2Mod.getInstance().getTotemTracker().clearPlayer(entity.getUuid());
            TotemCounterV2Mod.LOGGER.info("§c[TotemCounter] Player died, counter reset: {}", 
                entity.getName().getString());
        }
    }
}

