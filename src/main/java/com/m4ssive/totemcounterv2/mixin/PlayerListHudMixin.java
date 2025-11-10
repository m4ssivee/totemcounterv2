package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.config.ModConfig;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void totemcounterv2$appendCounter(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        try {
            TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
            if (mod == null) {
                return;
            }
            
            ModConfig cfg = mod.getConfig();
            if (cfg == null || !cfg.showInTabList) {
                return;
            }

            int count = mod.getTotemTracker().getTotemPops(entry.getProfile().getId());
            
            if (count == 0) {
                return;
            }

            Text base = cir.getReturnValue();
            if (base == null) {
                return;
            }
            
            MutableText suffixText = Text.literal(" (-").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xAAAAAA))
                .append(Text.literal(String.valueOf(count)).setStyle(net.minecraft.text.Style.EMPTY.withColor(cfg.tabListColor)))
                .append(Text.literal(")").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xAAAAAA)));
            
            MutableText updated = base.copy().append(suffixText);
            
            cir.setReturnValue(updated);
        } catch (Exception e) {
            TotemCounterV2Mod.LOGGER.error("Error in PlayerListHud mixin", e);
        }
    }
}


