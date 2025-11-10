package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    
    private int lastInventoryFillCount = 0;
    private boolean lastArmorWasFull = false;
    private boolean wasDead = false;
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
        
        if (mod == null || mod.getConfig() == null || !mod.getConfig().autoCounterReset) {
            return;
        }
        
        if (player == null || player.getInventory() == null) {
            return;
        }
        
        int emptySlots = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isEmpty()) {
                emptySlots++;
            }
        }
        int filledSlots = player.getInventory().size() - emptySlots;
        
        if (filledSlots >= 35 && lastInventoryFillCount < 35) {
            mod.getTotemTracker().clearPlayer(player.getUuid());
            String playerName = player.getGameProfile().getName();
            TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] {}'s counter reset - inventory full", playerName);
        }
        lastInventoryFillCount = filledSlots;
        
        boolean armorFull = true;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armor = player.getEquippedStack(slot);
            if (!armor.isEmpty()) {
                if (armor.getMaxDamage() > 0 && armor.getDamage() > 0) {
                    armorFull = false;
                    break;
                }
            }
        }
        
        if (armorFull && !lastArmorWasFull && filledSlots > 10) {
            mod.getTotemTracker().clearPlayer(player.getUuid());
            String playerName = player.getGameProfile().getName();
            TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] {}'s counter reset - armor durability full", playerName);
        }
        lastArmorWasFull = armorFull;
        
        // Ölümde otomatik reset - referans mod (uku3lig/totemcounter) gibi
        if (mod == null || mod.getConfig() == null || !mod.getConfig().autoResetOnDeath) {
            return;
        }
        
        boolean isDead = player.isDead();
        if (isDead && !wasDead) {
            // Oyuncu öldü - counter'ı sıfırla
            mod.getTotemTracker().clearPlayer(player.getUuid());
            String playerName = player.getGameProfile().getName();
            TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] {}'s counter reset - player died", playerName);
        }
        wasDead = isDead;
    }
}

