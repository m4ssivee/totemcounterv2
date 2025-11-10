package com.m4ssive.totemcounterv2.mixin;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        Mouse mouse = (Mouse) (Object) this;
        double mouseX = mouse.getX();
        double mouseY = mouse.getY();
        
        if (action == 1) {
            boolean handled = TotemCounterV2Mod.getInstance().getTotemHud()
                .handleMouseClick(mouseX, mouseY, button);
            if (handled) {
                ci.cancel();
            }
        }
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
    }
}























