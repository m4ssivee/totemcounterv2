package com.m4ssive.totemcounterv2;

import com.m4ssive.totemcounterv2.config.ModConfig;
import com.m4ssive.totemcounterv2.sound.SoundManager;
import com.m4ssive.totemcounterv2.util.TextFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TotemTracker {
    private final Map<UUID, Integer> totemPops = new HashMap<>();
    private final Map<UUID, Long> lastPopTime = new HashMap<>();
    
    private static final long DUPLICATE_COOLDOWN = 50;

    public void recordTotemPop(Entity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        UUID playerId = entity.getUuid();
        long currentTime = System.currentTimeMillis();
        
        Long lastTime = lastPopTime.get(playerId);
        if (lastTime != null && (currentTime - lastTime) < DUPLICATE_COOLDOWN) {
            TotemCounterV2Mod.LOGGER.warn("§e[TotemCounter] Duplicate event filtered for {}", 
                ((PlayerEntity) entity).getGameProfile().getName());
            return;
        }
        
        int newCount = totemPops.getOrDefault(playerId, 0) + 1;
        totemPops.put(playerId, newCount);
        lastPopTime.put(playerId, currentTime);
        
        TotemCounterV2Mod.LOGGER.info("§a[TotemCounter] {} popped a totem! Total: {}", 
            ((PlayerEntity) entity).getGameProfile().getName(), 
            newCount);
        
        ModConfig config = TotemCounterV2Mod.getInstance().getConfig();
        
        boolean isMilestone = false;
        if (config != null && config.milestoneEnabled && config.milestoneThreshold > 0) {
            isMilestone = (newCount == config.milestoneThreshold);
            TotemCounterV2Mod.LOGGER.debug("[TotemTracker] Milestone check - newCount: {}, threshold: {}, enabled: {}, isMilestone: {}", 
                newCount, config.milestoneThreshold, config.milestoneEnabled, isMilestone);
        }
        
        if (isMilestone) {
            TotemCounterV2Mod.LOGGER.info("§6§l[TotemCounter] Milestone reached: {} pops (threshold: {})", 
                newCount, config.milestoneThreshold);
            playMilestoneSound();
            showMilestoneNotification((PlayerEntity) entity, newCount);
            return;
        }
        
        playTotemSound();
    }
    
    private void playTotemSound() {
        TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
        if (mod == null) {
            TotemCounterV2Mod.LOGGER.warn("Mod instance is null!");
            return;
        }
        
        ModConfig config = mod.getConfig();
        if (config == null) {
            TotemCounterV2Mod.LOGGER.warn("Config is null!");
            return;
        }
        
        if (!config.enableSounds) {
            TotemCounterV2Mod.LOGGER.debug("Sounds disabled in config");
            return;
        }
        
        SoundManager.SoundType[] soundTypes = SoundManager.SoundType.values();
        if (config.soundType >= 0 && config.soundType < soundTypes.length) {
            SoundManager.SoundType selectedSound = soundTypes[config.soundType];
            TotemCounterV2Mod.LOGGER.info("§d[TotemCounter] Playing sound: {} (volume: {}, pitch: {})", 
                selectedSound.getDisplayName(), config.soundVolume, config.soundPitch);
            SoundManager.playTotemPopSound(selectedSound, config.soundVolume, config.soundPitch);
        } else {
            TotemCounterV2Mod.LOGGER.warn("Invalid sound type: {}", config.soundType);
        }
    }
    
    private void playMilestoneSound() {
        TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
        if (mod == null) {
            TotemCounterV2Mod.LOGGER.warn("Mod instance is null!");
            return;
        }
        
        ModConfig config = mod.getConfig();
        if (config == null) {
            TotemCounterV2Mod.LOGGER.warn("Config is null!");
            return;
        }
        
        if (!config.enableSounds) {
            TotemCounterV2Mod.LOGGER.debug("Sounds disabled in config");
            return;
        }
        
        SoundManager.playMilestoneSound(config.soundVolume * 2.0f, 1.5f);
        TotemCounterV2Mod.LOGGER.info("§6§l[TotemCounter] Milestone sesi çalındı!");
    }
    
    private void showMilestoneNotification(PlayerEntity player, int count) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        
        ModConfig config = TotemCounterV2Mod.getInstance().getConfig();
        if (config == null) {
            return;
        }
        
        String playerName = player.getGameProfile().getName();
        
        MutableText formattedMsg = Text.literal("")
            .append(Text.literal("[TotemCounter] ").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xFFAA00).withBold(true)))
            .append(Text.literal(playerName).setStyle(net.minecraft.text.Style.EMPTY.withColor(0xFFFF55)))
            .append(Text.literal(" has popped ").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xAAAAAA)))
            .append(Text.literal(String.valueOf(count)).setStyle(net.minecraft.text.Style.EMPTY.withColor(0xFFAA00).withBold(true)))
            .append(Text.literal(" times").setStyle(net.minecraft.text.Style.EMPTY.withColor(0xAAAAAA)));
        
        client.player.sendMessage(formattedMsg, false);
        
        TotemCounterV2Mod.LOGGER.info("[TotemCounter] Milestone notification sent: {} has popped {} times", 
            playerName, count);
    }

    public int getTotemPops(UUID playerId) {
        return totemPops.getOrDefault(playerId, 0);
    }

    public int getTotemPops(Entity entity) {
        if (entity == null) return 0;
        return getTotemPops(entity.getUuid());
    }

    public void clear() {
        totemPops.clear();
        lastPopTime.clear();
        TotemCounterV2Mod.LOGGER.info("§e[TotemCounter] All totem counts cleared");
    }

    public void clearPlayer(UUID playerId) {
        totemPops.remove(playerId);
        lastPopTime.remove(playerId);
    }

    public Map<UUID, Integer> getAllPops() {
        return new HashMap<>(totemPops);
    }

    public void setTotemPops(UUID playerId, int value) {
        totemPops.put(playerId, value);
    }

    public void addTotemPops(UUID playerId, int delta) {
        int current = totemPops.getOrDefault(playerId, 0);
        totemPops.put(playerId, current + delta);
    }
}

