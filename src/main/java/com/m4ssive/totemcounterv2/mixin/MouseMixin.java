package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        try {
            TotemCounterV2Mod mod = TotemCounterV2Mod.getInstance();
            if (mod == null) return;
            
            var totemHud = mod.getTotemHud();
            if (totemHud == null || !totemHud.isEditMode()) return;
            
            // EditModeScreen açıkken Screen kendi mouseClicked/Released'ini halleder
            // Sadece screen YOK ama editMode AÇIKSA intercept et
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.currentScreen != null) return;

            Mouse mouse = (Mouse) (Object) this;
            // mouse.getX()/getY() returns raw physical pixels.
            // Convert to scaled GUI coordinates.
            double scaledMouseX = mouse.getX();
            double scaledMouseY = mouse.getY();
            if (client != null && client.getWindow() != null) {
                double scale = client.getWindow().getWidth() > 0
                    ? (double) client.getWindow().getScaledWidth() / client.getWindow().getWidth()
                    : 1.0;
                scaledMouseX = mouse.getX() * scale;
                scaledMouseY = mouse.getY() * scale;
            }
            
            if (action == 1) { // Press
                boolean handled = totemHud.handleMouseClick(scaledMouseX, scaledMouseY, button);
                if (handled) ci.cancel();
            } else if (action == 0) { // Release
                boolean handled = totemHud.handleMouseRelease(scaledMouseX, scaledMouseY, button);
                if (handled) ci.cancel();
            }
        } catch (Exception e) {
            TotemCounterV2Mod.LOGGER.error("[MouseMixin] Error handling mouse button", e);
        }
    }
    
    @Inject(method = "onCursorPos", at = @At("TAIL"))
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
    }
}























