package com.m4ssive.totemcounterv2;

import com.m4ssive.totemcounterv2.config.ModConfig;
import com.m4ssive.totemcounterv2.gui.ConfigScreen;
import com.m4ssive.totemcounterv2.gui.EditModeScreen;
import com.m4ssive.totemcounterv2.gui.FirstTimeSetupScreen;
import com.m4ssive.totemcounterv2.gui.TotemHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TotemCounterV2 - Crystal PVP için gelişmiş totem sayacı
 * @author m4ssive
 * @version 2.0.0
 */
public class TotemCounterV2Mod implements ClientModInitializer {
    public static final String MOD_ID = "totemcounterv2";
    public static final String MOD_NAME = "TotemCounterV2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static TotemCounterV2Mod instance;
    private ModConfig config;
    private TotemTracker totemTracker;
    private TotemHud totemHud;

    private static KeyBinding openConfigKey;
    private static KeyBinding toggleEditModeKey;
    
    // Mouse tracking for dragging
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    
    // Edit mode
    private boolean editMode = false;

    @Override
    public void onInitializeClient() {
        instance = this;
        
        LOGGER.info("TotemCounterV2 by m4ssive yükleniyor...");

        // Config'i yükle
        config = new ModConfig();
        config.load();
        
        // İlk kullanımda setup ekranı göster
        if (!config.firstTimeSetup) {
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(new FirstTimeSetupScreen(config, () -> {
                    // Setup tamamlandıktan sonra keybind'leri kaydet
                    registerKeybinds();
                }));
            });
        }

        // Totem tracker'ı başlat
        totemTracker = new TotemTracker();

        // HUD'u başlat
        totemHud = new TotemHud(totemTracker, config);

        // HUD render callback'ini kaydet
        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.mouse != null) {
                // Update mouse position and handle dragging
                double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
                double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
                
                // Handle mouse drag if dragging or resizing
                if (config.isDragging || totemHud.isResizing()) {
                    double deltaX = mouseX - lastMouseX;
                    double deltaY = mouseY - lastMouseY;
                    totemHud.handleMouseDrag(mouseX, mouseY, 0, deltaX, deltaY);
                }
                
                lastMouseX = mouseX;
                lastMouseY = mouseY;
            }
            
            totemHud.render(context, tickCounter.getTickDelta(false));
        });

        // Keybind'leri kaydet (config'den alınan tuş ile)
        registerKeybinds();

        // Tick event'i kaydet (keybind kontrolü için)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // L tuşu - Config ekranı
            if (openConfigKey.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new ConfigScreen(null, config));
            }
            
            // Ş tuşu - Edit Mode toggle (HUD'ı sürükle ve boyutlandır)
            if (toggleEditModeKey.wasPressed()) {
                editMode = !editMode;
                totemHud.setEditMode(editMode);
                if (editMode) {
                    // Open edit mode screen (pauses game)
                    MinecraftClient.getInstance().setScreen(new EditModeScreen());
                    LOGGER.info("Edit Mode: ON");
                } else {
                    LOGGER.info("Edit Mode: OFF");
                    config.save();
                }
            }
        });

        LOGGER.info("TotemCounterV2 başarıyla yüklendi!");
    }

    public static TotemCounterV2Mod getInstance() {
        return instance;
    }

    public ModConfig getConfig() {
        return config;
    }

    public TotemTracker getTotemTracker() {
        return totemTracker;
    }

    public TotemHud getTotemHud() {
        return totemHud;
    }
    
    /**
     * Register keybinds based on config
     */
    private void registerKeybinds() {
        // Config menü tuşu (kullanıcının seçtiği tuş)
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.totemcounterv2.openconfig",
            InputUtil.Type.KEYSYM,
            config.configMenuKeybind,
            "category.totemcounterv2"
        ));
        
        // Edit Mode toggle - Ş tuşu (HUD'ı sürükle/boyutlandır)
        // GLFW_KEY_SEMICOLON = Türkçe klavyede Ş tuşu
        toggleEditModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.totemcounterv2.toggleedit",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_SEMICOLON,
            "category.totemcounterv2"
        ));
        
        LOGGER.info("Keybinds registered - Config key code: " + config.configMenuKeybind);
    }
}

