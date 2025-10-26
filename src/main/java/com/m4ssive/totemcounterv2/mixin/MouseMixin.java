package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to handle mouse events for HUD dragging and resizing
 */
@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        Mouse mouse = (Mouse) (Object) this;
        double mouseX = mouse.getX();
        double mouseY = mouse.getY();
        
        // Press (action == 1)
        if (action == 1) {
            boolean handled = TotemCounterV2Mod.getInstance().getTotemHud()
                .handleMouseClick(mouseX, mouseY, button);
            if (handled) {
                ci.cancel();
            }
        }
        // Release (action == 0)
        else if (action == 0) {
            boolean handled = TotemCounterV2Mod.getInstance().getTotemHud()
                .handleMouseRelease(mouseX, mouseY, button);
            if (handled) {
                ci.cancel();
            }
        }
    }
    
    @Inject(method = "onCursorPos", at = @At("TAIL"))
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        // This is called continuously as mouse moves
        // Actual dragging is handled in the HUD render loop
    }
}

