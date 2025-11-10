package com.m4ssive.totemcounterv2.mixin;

/**
 * WorldRendererMixin - DEVRE DIŞI
 * LivingEntityRendererMixin kullanılıyor (daha güvenilir ve nametag sistemine daha yakın)
 * 
 * Yeni yaklaşım: LivingEntityRenderer'da renderLabelIfPresent metodundan sonra
 * nametag'in üstüne totem sayısını render ediyoruz (3D dünyada)
 */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.render.WorldRenderer.class)
public class WorldRendererMixin {
    // Artık kullanılmıyor - LivingEntityRendererMixin kullanılıyor
}
