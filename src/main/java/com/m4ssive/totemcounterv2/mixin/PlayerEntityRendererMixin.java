package com.m4ssive.totemcounterv2.mixin;

/**
 * PlayerEntityRendererMixin - DEVRE DIŞI
 * LivingEntityRendererMixin kullanılıyor (daha genel ve güvenilir)
 * Çakışmaları önlemek için bu mixin devre dışı bırakıldı
 * 
 * Yeni yaklaşım: LivingEntityRenderer'da renderLabelIfPresent metodundan sonra
 * nametag'in üstüne totem sayısını render ediyoruz (3D dünyada)
 */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    // Artık kullanılmıyor - LivingEntityRendererMixin kullanılıyor
}
