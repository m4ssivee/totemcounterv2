package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
        method = "renderStatusBars",
        at = @At(value = "RETURN"),
        remap = true
    )
    private void totemcounterv2$renderInventoryTotemCount(DrawContext context, CallbackInfo ci) {
        try {
            if (this.client.player == null || this.client.options.hudHidden) {
                return;
            }

            TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
            if (mod == null) {
                return;
            }

            ModConfig config = mod.getConfig();
            if (config == null || !config.showInventoryTotemCount) {
                return;
            }

            int totemCount = 0;
            PlayerEntity player = this.client.player;
            
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    totemCount += stack.getCount();
                }
            }

            int screenWidth = this.client.getWindow().getScaledWidth();
            int screenHeight = this.client.getWindow().getScaledHeight();
            
            int xpBarY = screenHeight - 39;
            int xpBarCenterX = screenWidth / 2;
            
            String countText = String.valueOf(totemCount);
            ItemStack totemIcon = new ItemStack(Items.TOTEM_OF_UNDYING);
            int iconSize = 16;
            int spacing = 4; // Icon ve text arası boşluk
            
            int textWidth = this.client.textRenderer.getWidth(countText);
            int textHeight = this.client.textRenderer.fontHeight;
            
            // Totem icon'u önce çiz (arka plan) - XP bar'ın üstünde, ortada
            // Text genişliğini hesapla ve icon'u text'in soluna yerleştir
            int totalWidth = iconSize + spacing + textWidth;
            int iconX = xpBarCenterX - totalWidth / 2;
            int iconY = xpBarY - iconSize - 2;
            
            // Icon'u çiz (arka plan)
            context.drawItem(totemIcon, iconX, iconY);
            
            // Text'i icon'un sağına çiz (ön plan)
            int textX = iconX + iconSize + spacing;
            int textY = iconY + (iconSize - textHeight) / 2;
            
            // Shadow ile text çiz
            if (config.inventoryTotemCountShadow) {
                context.drawTextWithShadow(this.client.textRenderer, countText, textX, textY, config.inventoryTotemCountColor);
            } else {
                context.drawText(this.client.textRenderer, countText, textX, textY, config.inventoryTotemCountColor, false);
            }
        } catch (Exception e) {
            TotemCounterV2Mod.LOGGER.error("Error rendering inventory totem count", e);
        }
    }
    
}

