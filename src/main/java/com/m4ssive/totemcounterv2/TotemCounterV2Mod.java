package com.m4ssive.totemcounterv2;

import com.m4ssive.totemcounterv2.config.ModConfig;
import com.m4ssive.totemcounterv2.gui.ConfigScreen;
import com.m4ssive.totemcounterv2.gui.EditModeScreen;
import com.m4ssive.totemcounterv2.gui.TotemHud;
import com.m4ssive.totemcounterv2.command.ResetCounterCommand;
import com.m4ssive.totemcounterv2.command.ResetScoreboardCommand;
import com.m4ssive.totemcounterv2.command.SetPopsCommand;
import com.m4ssive.totemcounterv2.command.ImperialCommand;
import com.m4ssive.m4lib.M4Lib;
import com.m4ssive.m4lib.NametagRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotemCounterV2Mod implements ClientModInitializer {
    public static final String MOD_ID = "totemcounterv2";
    public static final String MOD_NAME = "TotemCounterV2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static TotemCounterV2Mod instance;
    private ModConfig config;
    private TotemTracker totemTracker;
    private TotemHud totemHud;

    private static KeyBinding openConfigKey;
    private static KeyBinding openConfigKeyAlt;
    private static KeyBinding toggleEditModeKey;
    
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    
    private boolean editMode = false;
    private boolean m4LibRegistered = false;
    private boolean m4LibRetryRegistered = false;

    @Override
    public void onInitializeClient() {
        instance = this;
        
        LOGGER.info("TotemCounterV2 by m4ssive yükleniyor...");

        config = new ModConfig();
        config.load();
        
        if (!config.firstTimeSetup) {
            config.firstTimeSetup = true;
            config.save();
        }

        totemTracker = new TotemTracker();

        totemHud = new TotemHud(totemTracker, config);
        
        registerNametagSuffix();

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (client != null && client.currentScreen == null) {
                if (client.mouse != null) {
                    double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
                    double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
                    
                    if (totemHud.isDragging() || totemHud.isResizing()) {
                        double deltaX = mouseX - lastMouseX;
                        double deltaY = mouseY - lastMouseY;
                        totemHud.handleMouseDrag(mouseX, mouseY, 0, deltaX, deltaY);
                    }
                    
                    lastMouseX = mouseX;
                    lastMouseY = mouseY;
                }
                
                totemHud.render(context, tickCounter.getTickDelta(false));
            }
        });

        registerKeybinds();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ResetCounterCommand.register(dispatcher);
            ResetScoreboardCommand.register(dispatcher);
            ImperialCommand.register(dispatcher);
            SetPopsCommand.register(dispatcher);
            LOGGER.info("All commands registered: /resetcounter, /resetscoreboard, /imperial, /setpops, /addpops");
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (config.hudX == -1 || config.hudY == -1) {
                if (client.getWindow() != null && client.getWindow().getScaledWidth() > 0) {
                    initializeDefaultPosition();
                }
            }
            
            if (openConfigKey.wasPressed() || (openConfigKeyAlt != null && openConfigKeyAlt.wasPressed())) {
                MinecraftClient.getInstance().setScreen(new ConfigScreen(null, config));
            }
            
            if (toggleEditModeKey.wasPressed()) {
                if (!editMode) {
                    editMode = true;
                    MinecraftClient.getInstance().setScreen(new EditModeScreen());
                    LOGGER.info("Edit Mode: ON - Edit Mode Screen opened");
                } else {
                    editMode = false;
                    totemHud.setEditMode(false);
                    if (MinecraftClient.getInstance().currentScreen instanceof EditModeScreen) {
                        MinecraftClient.getInstance().setScreen(null);
                    }
                    config.save();
                    LOGGER.info("Edit Mode: OFF - Changes saved");
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
    
    private void registerNametagSuffix() {
        try {
            LOGGER.info("[TotemCounterV2] Registering nametag suffix provider with m4lib...");
            
            M4Lib m4Lib = M4Lib.getInstance();
            if (m4Lib == null) {
                LOGGER.warn("[TotemCounterV2] m4lib not found on first attempt. Will retry...");
                if (!m4LibRetryRegistered) {
                    m4LibRetryRegistered = true;
                    ClientTickEvents.END_CLIENT_TICK.register(client -> {
                        if (!m4LibRegistered) {
                            try {
                                M4Lib lib = M4Lib.getInstance();
                                if (lib != null) {
                                    NametagRenderer renderer = lib.getNametagRenderer();
                                    if (renderer != null) {
                                        LOGGER.info("[TotemCounterV2] m4lib found on retry, registering nametag suffix...");
                                        doRegisterNametagSuffix(renderer);
                                    } else {
                                        LOGGER.debug("[TotemCounterV2] m4lib found but NametagRenderer not ready yet...");
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("[TotemCounterV2] Error during retry registration", e);
                            }
                        }
                    });
                }
                return;
            }
            
            NametagRenderer nametagRenderer = m4Lib.getNametagRenderer();
            if (nametagRenderer == null) {
                LOGGER.warn("[TotemCounterV2] m4lib found but NametagRenderer not available. Will retry...");
                if (!m4LibRetryRegistered) {
                    m4LibRetryRegistered = true;
                    ClientTickEvents.END_CLIENT_TICK.register(client -> {
                        if (!m4LibRegistered) {
                            try {
                                M4Lib lib = M4Lib.getInstance();
                                if (lib != null) {
                                    NametagRenderer renderer = lib.getNametagRenderer();
                                    if (renderer != null) {
                                        LOGGER.info("[TotemCounterV2] NametagRenderer ready on retry, registering nametag suffix...");
                                        doRegisterNametagSuffix(renderer);
                                    } else {
                                        LOGGER.debug("[TotemCounterV2] NametagRenderer still not ready...");
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("[TotemCounterV2] Error during retry registration", e);
                            }
                        }
                    });
                }
                return;
            }
            
            doRegisterNametagSuffix(nametagRenderer);
        } catch (Exception e) {
            LOGGER.error("[TotemCounterV2] ✗ Error registering nametag suffix with m4lib", e);
        }
    }
    
    private void doRegisterNametagSuffix(NametagRenderer nametagRenderer) {
        if (m4LibRegistered) {
            LOGGER.debug("[TotemCounterV2] Nametag suffix already registered, skipping...");
            return;
        }
        
        if (nametagRenderer == null) {
            LOGGER.error("[TotemCounterV2] Cannot register nametag suffix: NametagRenderer is null!");
            return;
        }
        
        try {
            nametagRenderer.registerModSuffix("totemcounterv2", (player) -> {
                try {
                    if (player == null) {
                        return null;
                    }
                    
                    ModConfig config = getConfig();
                    if (config == null) {
                        LOGGER.warn("[TotemCounterV2] Config is null in nametag suffix provider!");
                        return null;
                    }
                    
                    if (!config.showInNametags) {
                        return null;
                    }
                    
                    if (totemTracker == null) {
                        LOGGER.warn("[TotemCounterV2] TotemTracker is null in nametag suffix provider!");
                        return null;
                    }
                    
                    if (!player.isAlive()) {
                        totemTracker.clearPlayer(player.getUuid());
                        return null;
                    }
                    
                    int totemPops = totemTracker.getTotemPops(player.getUuid());
                    if (totemPops <= 0) {
                        return null;
                    }
                    
                    MutableText suffix = Text.empty().append(" ");
                    
                    suffix.append(Text.literal("| ").styled(s -> s.withColor(net.minecraft.util.Formatting.GRAY)));
                    
                    MutableText counter = Text.literal("-" + totemPops);
                    TotemCounterV2Mod modInstance = getInstance();
                    int popColor;
                    if (modInstance != null) {
                        popColor = modInstance.getPopColor(totemPops);
                    } else {
                        popColor = switch (totemPops) {
                            case 1, 2 -> 0xFF55FF55;
                            case 3, 4 -> 0xFF00AA00;
                            case 5, 6 -> 0xFFFFFF55;
                            case 7, 8 -> 0xFFFFAA00;
                            default -> 0xFFFF5555;
                        };
                    }
                    TextColor textColor = TextColor.fromRgb(popColor);
                    counter.setStyle(counter.getStyle().withColor(textColor));
                    suffix.append(counter);
                    
                    LOGGER.debug("[TotemCounterV2] ✓ Returning nametag suffix for {}: '{}' (pops: {})", 
                        player.getNameForScoreboard(), suffix.getString(), totemPops);
                    return suffix;
                } catch (Exception e) {
                    LOGGER.error("[TotemCounterV2] ✗ Error creating nametag suffix for player {}", 
                        player != null ? player.getNameForScoreboard() : "null", e);
                    return null;
                }
            });
            
            m4LibRegistered = true;
            ModConfig currentConfig = getConfig();
            LOGGER.info("[TotemCounterV2] ✓✓✓ Nametag suffix provider registered successfully with m4lib");
            LOGGER.info("[TotemCounterV2]   - Text suffix provider: Shows totem pop count as text (format: | -5)");
            LOGGER.info("[TotemCounterV2]   - Config setting 'showInNametags' controls visibility (current: {})", 
                currentConfig != null ? currentConfig.showInNametags : "unknown");
            LOGGER.info("[TotemCounterV2]   - Provider will be called for each player when nametag is rendered");
        } catch (Exception e) {
            LOGGER.error("[TotemCounterV2] ✗✗✗ Failed to register nametag suffix with m4lib", e);
        }
    }
    
    private void initializeDefaultPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) {
            return;
        }
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        int estimatedHudWidth = 250;
        int padding = 20;
        
        config.hudX = screenWidth - estimatedHudWidth - padding;
        config.hudY = screenHeight / 2;
        
        config.save();
        LOGGER.info("HUD pozisyonu sağ orta olarak ayarlandı: X={}, Y={}", config.hudX, config.hudY);
    }
    
    private void registerKeybinds() {
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.totemcounterv2.openconfig",
            InputUtil.Type.KEYSYM,
            config.configMenuKeybind,
            "category.totemcounterv2"
        ));
        
        openConfigKeyAlt = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.totemcounterv2.openconfigalt",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "category.totemcounterv2"
        ));
        
        toggleEditModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.totemcounterv2.toggleedit",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.totemcounterv2"
        ));
        
        LOGGER.info("Keybinds registered - Config key (H): {}, Alt key (L): {}", config.configMenuKeybind, GLFW.GLFW_KEY_L);
    }
    
    public int getPopColor(int pops) {
        ModConfig config = getConfig();
        if (config == null) {
            return switch (pops) {
                case 1, 2 -> 0xFF55FF55;
                case 3, 4 -> 0xFF00AA00;
                case 5, 6 -> 0xFFFFFF55;
                case 7, 8 -> 0xFFFFAA00;
                default -> 0xFFFF5555;
            };
        }
        
        if (config.useCustomNametagPopColor) {
            return config.customNametagPopColor;
        }
        
        return switch (pops) {
            case 1, 2 -> config.nametagPopColor1_2;
            case 3, 4 -> config.nametagPopColor3_4;
            case 5, 6 -> config.nametagPopColor5_6;
            case 7, 8 -> config.nametagPopColor7_8;
            default -> config.nametagPopColor9Plus;
        };
    }
    
}
