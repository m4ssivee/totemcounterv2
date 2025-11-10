package com.m4ssive.totemcounterv2.gui;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class EditModeScreen extends Screen {
    
    private final TotemHud totemHud;
    private final ModConfig config;
    
    public EditModeScreen() {
        super(Text.literal("Edit Mode - HUD Editor"));
        TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
        this.totemHud = mod != null ? mod.getTotemHud() : null;
        this.config = mod != null ? mod.getConfig() : null;
        
        if (this.totemHud != null) {
            this.totemHud.setEditMode(true);
        }
    }

    @Override
    protected void init() {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, this.width, this.height, 0x60000000);
        
        if (totemHud != null) {
            totemHud.render(context, delta);
        }
        
        int headerHeight = 40;
        context.fillGradient(0, 0, this.width, headerHeight, 
            0xFF2d1b4e, 0xFF1a237e);
        
        context.fill(0, headerHeight - 2, this.width, headerHeight, 0xAA7b2cbf);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("§6§l✎ EDIT MODE").setStyle(net.minecraft.text.Style.EMPTY.withBold(true)),
            this.width / 2, 10, 0xFFFFFF);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("§eDrag §7to move • §bDrag corners §7to resize"),
            this.width / 2, 22, 0xE0E0E0);
        
        int footerY = this.height - 35;
        context.fillGradient(0, footerY, this.width, this.height, 
            0x80000000, 0xC0000000);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("§7Press §eK/ESC §7to save • §cQ §7to cancel"),
            this.width / 2, footerY + 8, 0xCCCCCC);
        
        if (totemHud != null) {
            String status = totemHud.isDragging() ? "§eDragging..." : 
                           totemHud.isResizing() ? "§bResizing..." : "§aReady";
            int statusX = this.width - 80;
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal(status),
                statusX, 10, 0xFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (totemHud != null) {
            return totemHud.handleMouseClick(mouseX, mouseY, button) 
                || super.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (totemHud != null) {
            boolean handled = totemHud.handleMouseRelease(mouseX, mouseY, button);
            return handled || super.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (totemHud != null) {
            return totemHud.handleMouseDrag(mouseX, mouseY, button, deltaX, deltaY)
                || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // K tuşu veya ESC - kaydet ve çık
        if (keyCode == 75 || keyCode == 256) { // K or ESC
            exitEditMode(true);
            return true;
        }
        // Q tuşu - kaydetmeden çık
        if (keyCode == 81) { // Q
            exitEditMode(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void exitEditMode(boolean save) {
        if (totemHud != null) {
            totemHud.setEditMode(false);
        }
        if (save && config != null) {
            // Save changes
            config.save();
            TotemCounterV2Mod.LOGGER.info("Edit Mode: Changes saved - HUD Position: X={}, Y={}, Scale={}", 
                config.hudX, config.hudY, config.scale);
        } else if (config != null) {
            // Cancel - değişiklikleri geri al (config'i reload et)
            // Önce mevcut pozisyonu kaydet
            int oldX = config.hudX;
            int oldY = config.hudY;
            float oldScale = config.scale;
            
            // Config'i reload et
            config.load();
            
            TotemCounterV2Mod.LOGGER.info("Edit Mode: Changes discarded - Reverted to X={}, Y={}, Scale={}", 
                config.hudX, config.hudY, config.scale);
        }
        this.close();
    }

    @Override
    public void close() {
        if (totemHud != null) {
            totemHud.setEditMode(false);
        }
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }

    @Override
    public boolean shouldPause() {
        return true; // Oyunu duraklat - ekran kilitlensin
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // ESC ile kapatma - keyPressed'de handle ediyoruz
    }
}
