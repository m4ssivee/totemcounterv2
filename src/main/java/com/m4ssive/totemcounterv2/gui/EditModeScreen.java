package com.m4ssive.totemcounterv2.gui;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Invisible screen that pauses the game during Edit Mode
 * Shows instructions and handles mouse events
 */
public class EditModeScreen extends Screen {

    public EditModeScreen() {
        super(Text.literal("Edit Mode"));
    }

    @Override
    protected void init() {
        // No widgets needed
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Don't render background (let game show through)
        // Just render instructions
        
        // Edit mode instructions overlay
        int centerX = this.width / 2;
        int centerY = 20;
        
        // Title
        context.fill(0, 0, this.width, 60, 0x88000000);
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§6§l✎ EDIT MODE", 
            centerX, centerY, 0xFFFFFF);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§eLeft-Click Drag §7to move §8| §eRight-Click Corners §7to resize", 
            centerX, centerY + 15, 0xFFFFFF);
        
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§7Press §eŞ §7or §eESC §7to exit and save", 
            centerX, centerY + 27, 0xAAAAAA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Forward to HUD
        return TotemCounterV2Mod.getInstance().getTotemHud().handleMouseClick(mouseX, mouseY, button) 
            || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Forward to HUD
        return TotemCounterV2Mod.getInstance().getTotemHud().handleMouseRelease(mouseX, mouseY, button)
            || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Forward to HUD
        return TotemCounterV2Mod.getInstance().getTotemHud().handleMouseDrag(mouseX, mouseY, button, deltaX, deltaY)
            || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Ş key (semicolon) or ESC to exit
        if (keyCode == 59 || keyCode == 256) { // Semicolon (Ş) or ESC
            exitEditMode();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void exitEditMode() {
        TotemCounterV2Mod.getInstance().getTotemHud().setEditMode(false);
        TotemCounterV2Mod.getInstance().getConfig().save();
        this.close();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }

    @Override
    public boolean shouldPause() {
        return true; // Pause the game
    }
}

