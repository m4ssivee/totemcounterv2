package com.m4ssive.totemcounterv2.sound;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

public class SoundManager {

    public enum SoundType {
        TOTEM("Totem", SoundEvents.ITEM_TOTEM_USE),
        NOTE_HARP("Note Harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP.value()),
        NOTE_BELL("Note Bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL.value()),
        NOTE_PLING("Note Pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING.value()),
        ITEM_PICKUP("Item Pickup", SoundEvents.ENTITY_ITEM_PICKUP);
        
        private final String displayName;
        private final SoundEvent soundEvent;
        
        SoundType(String displayName, SoundEvent soundEvent) {
            this.displayName = displayName;
            this.soundEvent = soundEvent;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public SoundEvent getSoundEvent() {
            return soundEvent;
        }
    }
    
    public static void playTotemPopSound(SoundType soundType, float volume, float pitch) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            TotemCounterV2Mod.LOGGER.warn("Client is null!");
            return;
        }
        
        if (client.player == null) {
            TotemCounterV2Mod.LOGGER.warn("Player is null!");
            return;
        }
        
        if (client.getSoundManager() == null) {
            TotemCounterV2Mod.LOGGER.warn("SoundManager is null!");
            return;
        }
        
        SoundEvent soundEvent = soundType.getSoundEvent();
        if (soundEvent == null) {
            TotemCounterV2Mod.LOGGER.warn("Sound event is null for: {}", soundType.getDisplayName());
            return;
        }
        
        SoundCategory category = SoundCategory.MASTER;
        
        PositionedSoundInstance sound = new PositionedSoundInstance(
            soundEvent,
            category,
            volume,
            pitch,
            Random.create(),
            client.player.getX(),
            client.player.getY(),
            client.player.getZ()
        );
        
        TotemCounterV2Mod.LOGGER.info("§e[SoundManager] Playing sound: {} at volume: {}, pitch: {}, category: {}", 
            soundType.getDisplayName(), volume, pitch, category);
        
        client.getSoundManager().play(sound);
    }
    
    public static void playNotificationSound(SoundType soundType, float volume, float pitch) {
        playTotemPopSound(soundType, volume * 1.5f, pitch * 1.2f);
    }
    
    public static void playMilestoneSound(float baseVolume, float pitch) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            TotemCounterV2Mod.LOGGER.warn("Client is null!");
            return;
        }
        
        if (client.player == null) {
            TotemCounterV2Mod.LOGGER.warn("Player is null!");
            return;
        }
        
        if (client.getSoundManager() == null) {
            TotemCounterV2Mod.LOGGER.warn("SoundManager is null!");
            return;
        }
        
        SoundEvent soundEvent = SoundType.NOTE_PLING.getSoundEvent();
        if (soundEvent == null) {
            TotemCounterV2Mod.LOGGER.warn("Sound event is null for NOTE_PLING");
            return;
        }
        
        SoundCategory category = SoundCategory.MASTER;
        
        float volume = Math.min(baseVolume, 1.0f);
        float finalPitch = Math.min(pitch, 2.0f);
        
        PositionedSoundInstance sound1 = new PositionedSoundInstance(
            soundEvent,
            category,
            volume,
            finalPitch,
            Random.create(),
            client.player.getX(),
            client.player.getY(),
            client.player.getZ()
        );
        
        PositionedSoundInstance sound2 = new PositionedSoundInstance(
            soundEvent,
            category,
            volume * 0.8f,
            finalPitch * 0.9f,
            Random.create(),
            client.player.getX(),
            client.player.getY(),
            client.player.getZ()
        );
        
        TotemCounterV2Mod.LOGGER.info("§6§l[SoundManager] Playing milestone sound: NOTE_PLING at volume: {}, pitch: {}", 
            volume, finalPitch);
        
        client.getSoundManager().play(sound1);
        client.getSoundManager().play(sound2);
    }
}
