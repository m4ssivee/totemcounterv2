package com.m4ssive.totemcounterv2.mixin;

/**
 * EntityRendererMixin - DEVRE DIŞI
 * PlayerEntityRendererMixin kullanılıyor (daha spesifik)
 * Çakışmaları önlemek için bu mixin devre dışı bırakıldı
 */
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.render.entity.EntityRenderer.class)
public class EntityRendererMixin {
    // Artık kullanılmıyor - PlayerEntityRendererMixin kullanılıyor
}
