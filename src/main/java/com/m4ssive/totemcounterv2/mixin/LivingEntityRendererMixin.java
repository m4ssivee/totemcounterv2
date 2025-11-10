package com.m4ssive.totemcounterv2.mixin;

/**
 * LivingEntityRendererMixin - DEVRE DIŞI
 * Artık m4lib üzerinden nametag render ediliyor
 * TotemCounterV2 sadece provider'ları kaydediyor, render işlemini m4lib yapıyor
 * Çakışmaları önlemek için bu mixin devre dışı bırakıldı
 * 
 * NOT: Nametag rendering artık m4lib/PlayerEntityMixin tarafından yapılıyor
 * TotemCounterV2/TotemCounterV2Mod.java'da registerNametagSuffix() metodu ile
 * provider'lar kaydediliyor ve m4lib bunları render ediyor
 */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.render.entity.LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    // Artık kullanılmıyor - m4lib üzerinden çalışıyor
}
