package com.m4ssive.totemcounterv2;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Oyuncuların totem patlatmalarını takip eden sınıf
 */
public class TotemTracker {
    private final Map<UUID, Integer> totemPops = new HashMap<>();
    private final Map<UUID, Long> lastPopTime = new HashMap<>();
    
    // Duplicate event önleme - 50ms cooldown (çok kısa, sadece duplicate'leri filtreler)
    private static final long DUPLICATE_COOLDOWN = 50;

    /**
     * Bir oyuncunun totem patlatmasını kaydet
     * Duplicate event filtreleme ile
     */
    public void recordTotemPop(Entity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        UUID playerId = entity.getUuid();
        long currentTime = System.currentTimeMillis();
        
        // Duplicate event kontrolü (50ms içinde aynı oyuncu için tekrar tetikleme = duplicate)
        Long lastTime = lastPopTime.get(playerId);
        if (lastTime != null && (currentTime - lastTime) < DUPLICATE_COOLDOWN) {
            TotemCounterV2Mod.LOGGER.warn("§e[TotemCounter] Duplicate event filtered for {}", 
                ((PlayerEntity) entity).getGameProfile().getName());
            return;
        }
        
        // Totem pop sayısını artır
        int newCount = totemPops.getOrDefault(playerId, 0) + 1;
        totemPops.put(playerId, newCount);
        lastPopTime.put(playerId, currentTime);
        
        TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] {} popped a totem! Total: {}", 
            ((PlayerEntity) entity).getGameProfile().getName(), 
            newCount);
    }

    /**
     * Bir oyuncunun totem pop sayısını al
     */
    public int getTotemPops(UUID playerId) {
        return totemPops.getOrDefault(playerId, 0);
    }

    /**
     * Bir oyuncunun totem pop sayısını al (Entity ile)
     */
    public int getTotemPops(Entity entity) {
        if (entity == null) return 0;
        return getTotemPops(entity.getUuid());
    }

    /**
     * Tüm totem pop kayıtlarını temizle
     */
    public void clear() {
        totemPops.clear();
        lastPopTime.clear();
        TotemCounterV2Mod.LOGGER.info("§e[TotemCounter] All totem counts cleared");
    }

    /**
     * Belirli bir oyuncunun kayıtlarını temizle
     */
    public void clearPlayer(UUID playerId) {
        totemPops.remove(playerId);
        lastPopTime.remove(playerId);
    }

    /**
     * Tüm oyuncuların toplam totem pop sayısı
     */
    public Map<UUID, Integer> getAllPops() {
        return new HashMap<>(totemPops);
    }
}

