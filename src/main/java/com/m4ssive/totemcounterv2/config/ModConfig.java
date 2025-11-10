package com.m4ssive.totemcounterv2.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("totemcounterv2.json");

    public boolean firstTimeSetup = false;
    public int configMenuKeybind = 72;
    
    public int hudX = -1;
    public int hudY = -1;
    public transient boolean isDragging = false;
    public transient int dragOffsetX = 0;
    public transient int dragOffsetY = 0;

    public int textColor = PresetColors.SOFT_WHITE.getColor();
    public int backgroundColor = PresetColors.BG_DARK.getColor();
    public int borderColor = PresetColors.BORDER_AQUA.getColor();

    public float scale = 1.0f;
    public int padding = 5;

    public boolean showBackground = true;
    public boolean showBorder = true;
    public boolean showSelf = false;
    public boolean showOnlyNearby = false;
    public int nearbyDistance = 50;
    public boolean showIcon = true;

    public boolean enableHud = true;
    public boolean showInNametags = true;
    public boolean showInTabList = true;
    public int nametagColor = PresetColors.SOFT_YELLOW.getColor();
    public int tabListColor = PresetColors.SOFT_YELLOW.getColor();
    
    public boolean useCustomNametagPopColor = false;
    public int customNametagPopColor = PresetColors.YELLOW.getColor();
    
    public int nametagPopColor1_2 = 0xFF55FF55;
    public int nametagPopColor3_4 = 0xFF00AA00;
    public int nametagPopColor5_6 = 0xFFFFFF55;
    public int nametagPopColor7_8 = 0xFFFFAA00;
    public int nametagPopColor9Plus = 0xFFFF5555;
    
    public boolean showInventoryTotemCount = true;
    public int inventoryTotemCountColor = PresetColors.SOFT_YELLOW.getColor();
    public boolean inventoryTotemCountShadow = true;

    public boolean countShadow = true;
    public float countScale = 1.2f;

    public String displayFormat = "{player}: {count}";
    public int maxPlayersShown = 10;
    
    public boolean autoCounterReset = true;
    public boolean autoResetOnDeath = true;
    
    public boolean enableSounds = true;
    public int soundType = 0;
    public float soundVolume = 0.5f;
    public float soundPitch = 1.0f;

    public boolean milestoneEnabled = true;
    public int milestoneThreshold = 14;
    public String milestoneMessageFormat = "§6§l[TotemCounter] §e{player} §7has popped §6{count} §7times";

    public void load() {
        if (!Files.exists(CONFIG_PATH)) {
            TotemCounterV2Mod.LOGGER.info("Config dosyası bulunamadı, varsayılan ayarlar kullanılıyor");
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
            if (loaded != null) {
                copyFrom(loaded);
                TotemCounterV2Mod.LOGGER.info("Config başarıyla yüklendi");
            }
        } catch (Exception e) {
            TotemCounterV2Mod.LOGGER.error("Config yüklenirken hata oluştu, varsayılan ayarlar kullanılıyor", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
                TotemCounterV2Mod.LOGGER.info("Config kaydedildi");
            }
        } catch (Exception e) {
            TotemCounterV2Mod.LOGGER.error("Config kaydedilirken hata oluştu", e);
        }
    }

    private void copyFrom(ModConfig other) {
        this.firstTimeSetup = other.firstTimeSetup;
        this.configMenuKeybind = other.configMenuKeybind;
        if (other.hudX == -1 || other.hudY == -1 || (other.hudX == 10 && other.hudY == 10)) {
            this.hudX = -1;
            this.hudY = -1;
        } else {
            this.hudX = other.hudX;
            this.hudY = other.hudY;
        }
        this.textColor = other.textColor;
        this.backgroundColor = other.backgroundColor;
        this.borderColor = other.borderColor;
        this.scale = other.scale;
        this.padding = other.padding;
        this.enableHud = other.enableHud;
        this.showBackground = other.showBackground;
        this.showBorder = other.showBorder;
        this.showSelf = other.showSelf;
        this.showOnlyNearby = other.showOnlyNearby;
        this.nearbyDistance = other.nearbyDistance;
        this.showIcon = other.showIcon;
        this.showInNametags = other.showInNametags;
        this.showInTabList = other.showInTabList;
        this.nametagColor = other.nametagColor;
        this.tabListColor = other.tabListColor;
        this.useCustomNametagPopColor = other.useCustomNametagPopColor;
        this.customNametagPopColor = other.customNametagPopColor;
        this.nametagPopColor1_2 = other.nametagPopColor1_2;
        this.nametagPopColor3_4 = other.nametagPopColor3_4;
        this.nametagPopColor5_6 = other.nametagPopColor5_6;
        this.nametagPopColor7_8 = other.nametagPopColor7_8;
        this.nametagPopColor9Plus = other.nametagPopColor9Plus;
        this.showInventoryTotemCount = other.showInventoryTotemCount;
        this.inventoryTotemCountColor = other.inventoryTotemCountColor;
        this.inventoryTotemCountShadow = other.inventoryTotemCountShadow;
        this.countShadow = other.countShadow;
        this.countScale = other.countScale;
        this.displayFormat = other.displayFormat;
        this.maxPlayersShown = other.maxPlayersShown;
        this.autoCounterReset = other.autoCounterReset;
        this.autoResetOnDeath = other.autoResetOnDeath;
        this.enableSounds = other.enableSounds;
        this.soundType = other.soundType;
        this.soundVolume = other.soundVolume;
        this.soundPitch = other.soundPitch;
        this.milestoneEnabled = other.milestoneEnabled;
        this.milestoneThreshold = other.milestoneThreshold;
        this.milestoneMessageFormat = other.milestoneMessageFormat;
    }
}
