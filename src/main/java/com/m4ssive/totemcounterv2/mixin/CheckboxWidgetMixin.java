package com.m4ssive.totemcounterv2.mixin;

import net.minecraft.client.gui.widget.CheckboxWidget;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CheckboxWidget.class)
public interface CheckboxWidgetMixin {
    // Artık kullanılmıyor - CustomCheckboxWidget composition kullanıyor
}
