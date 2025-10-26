package com.m4ssive.totemcounterv2.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mod ayarları ve config yönetimi
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("totemcounterv2.json");

    // First time setup
    public boolean firstTimeSetup = false;
    public int configMenuKeybind = 76; // Default: L key (GLFW.GLFW_KEY_L)
    
    // HUD Pozisyon ayarları
    public int hudX = 10;
    public int hudY = 10;
    public transient boolean isDragging = false;
    public transient int dragOffsetX = 0;
    public transient int dragOffsetY = 0;

    // Renk ayarları (Preset colors)
    public int textColor = PresetColors.SOFT_WHITE.getColor();
    public int backgroundColor = PresetColors.BG_DARK.getColor();
    public int borderColor = PresetColors.BORDER_GOLD.getColor();

    // Boyut ayarları
    public float scale = 1.0f;
    public int padding = 5;

    // Görünüm ayarları
    public boolean showBackground = true;
    public boolean showBorder = true;
    public boolean showSelf = false; // Kendi totem pop'unu göster
    public boolean showOnlyNearby = false; // Sadece yakındaki oyuncuları göster
    public int nearbyDistance = 50; // Yakınlık mesafesi (blok)
    public boolean showIcon = true; // Totem ikonu göster

    // Görüntüleme formatı
    public String displayFormat = "{player}: {count}";
    public int maxPlayersShown = 10; // En fazla kaç oyuncu gösterilsin

    /**
     * Config dosyasını yükle
     */
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
            TotemCounterV2Mod.LOGGER.error("Config yüklenirken hata oluştu", e);
        }
    }

    /**
     * Config dosyasını kaydet
     */
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

    /**
     * Başka bir config'ten ayarları kopyala
     */
    private void copyFrom(ModConfig other) {
        this.firstTimeSetup = other.firstTimeSetup;
        this.configMenuKeybind = other.configMenuKeybind;
        this.hudX = other.hudX;
        this.hudY = other.hudY;
        this.textColor = other.textColor;
        this.backgroundColor = other.backgroundColor;
        this.borderColor = other.borderColor;
        this.scale = other.scale;
        this.padding = other.padding;
        this.showBackground = other.showBackground;
        this.showBorder = other.showBorder;
        this.showSelf = other.showSelf;
        this.showOnlyNearby = other.showOnlyNearby;
        this.nearbyDistance = other.nearbyDistance;
        this.showIcon = other.showIcon;
        this.displayFormat = other.displayFormat;
        this.maxPlayersShown = other.maxPlayersShown;
    }
}

