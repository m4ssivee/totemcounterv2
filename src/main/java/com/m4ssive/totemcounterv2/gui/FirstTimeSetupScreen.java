package com.m4ssive.totemcounterv2.gui;

import com.m4ssive.totemcounterv2.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * First-time setup screen for keybind selection
 */
public class FirstTimeSetupScreen extends Screen {
    private final ModConfig config;
    private final Runnable onComplete;

    public FirstTimeSetupScreen(ModConfig config, Runnable onComplete) {
        super(Text.literal("§6§lTotemCounterV2 §r- First Time Setup"));
        this.config = config;
        this.onComplete = onComplete;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // L tuşu seçeneği
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§a§lL §r§7(Recommended)"),
            button -> selectKeybind(GLFW.GLFW_KEY_L)
        ).dimensions(centerX - 100, startY + 60, 95, 30).build());

        // J tuşu seçeneği
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§b§lJ"),
            button -> selectKeybind(GLFW.GLFW_KEY_J)
        ).dimensions(centerX + 5, startY + 60, 95, 30).build());

        // K tuşu seçeneği
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§e§lK"),
            button -> selectKeybind(GLFW.GLFW_KEY_K)
        ).dimensions(centerX - 100, startY + 95, 95, 30).build());

        // M tuşu seçeneği
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§d§lM"),
            button -> selectKeybind(GLFW.GLFW_KEY_M)
        ).dimensions(centerX + 5, startY + 95, 95, 30).build());

        // Skip butonu
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§7Skip (use default: L)"),
            button -> selectKeybind(GLFW.GLFW_KEY_L)
        ).dimensions(centerX - 75, startY + 140, 150, 20).build());
    }

    private void selectKeybind(int keyCode) {
        config.configMenuKeybind = keyCode;
        config.firstTimeSetup = true;
        config.save();
        
        if (onComplete != null) {
            onComplete.run();
        }
        
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 80, 0xFFD700);
        
        // Instructions
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§fWelcome to §6TotemCounterV2§f!", 
            this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§7Please select a key to open the config menu:", 
            this.width / 2, this.height / 2 - 35, 0xAAAAAA);
        
        // Info
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§7Press §eE §7anytime to enter Edit Mode", 
            this.width / 2, this.height / 2 + 70, 0x888888);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§7(Drag and resize HUD)", 
            this.width / 2, this.height / 2 + 82, 0x666666);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // Kullanıcı seçim yapmalı
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }
}

