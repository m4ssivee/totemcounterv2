package com.m4ssive.totemcounterv2.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Reflection-based text rendering for cross-version compatibility.
 *
 * Problem: In Minecraft 1.21.1, DrawContext.drawText(...) returns `int`.
 *          In Feather 1.21.11, the same method returns `void`.
 *          Same issue with drawCenteredTextWithShadow and drawTextWithShadow.
 * Solution: Use reflection to find and invoke the method regardless of return type.
 */
public class TextHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("TotemCounterV2");

    // drawText(TextRenderer, Text, int, int, int, boolean shadow)
    private static Method drawTextShadowFlagMethod = null;
    // drawTextWithShadow(TextRenderer, Text, int, int, int)
    private static Method drawTextWithShadowMethod = null;
    // drawCenteredTextWithShadow(TextRenderer, Text, int, int, int)
    private static Method drawCenteredTextWithShadowMethod = null;

    private static boolean initialized = false;

    private static void init(DrawContext ctx) {
        if (initialized) return;
        initialized = true;
        try {
            Class<?> textRendererClass = TextRenderer.class;
            Class<?> textClass = Text.class;

            for (Method m : ctx.getClass().getMethods()) {
                Class<?>[] p = m.getParameterTypes();
                String name = m.getName();

                // drawText(TextRenderer, Text, int, int, int, boolean) - 6 args
                if (drawTextShadowFlagMethod == null
                        && p.length == 6
                        && p[0] == textRendererClass && p[1] == textClass
                        && p[2] == int.class && p[3] == int.class && p[4] == int.class
                        && p[5] == boolean.class) {
                    drawTextShadowFlagMethod = m;
                    LOGGER.info("[TextHelper] drawText(6): " + name + " -> " + m.getReturnType().getSimpleName());
                }

                // drawTextWithShadow(TextRenderer, Text, int, int, int) - 5 args, contains "shadow" or "Shadow" (not Centered)
                if (drawTextWithShadowMethod == null
                        && p.length == 5
                        && p[0] == textRendererClass && p[1] == textClass
                        && p[2] == int.class && p[3] == int.class && p[4] == int.class
                        && (name.contains("Shadow") || name.contains("shadow"))
                        && !(name.contains("Centered") || name.contains("centered"))) {
                    drawTextWithShadowMethod = m;
                    LOGGER.info("[TextHelper] drawTextWithShadow(5): " + name + " -> " + m.getReturnType().getSimpleName());
                }

                // drawCenteredTextWithShadow(TextRenderer, Text, int, int, int) - 5 args, contains "Centered"
                if (drawCenteredTextWithShadowMethod == null
                        && p.length == 5
                        && p[0] == textRendererClass && p[1] == textClass
                        && p[2] == int.class && p[3] == int.class && p[4] == int.class
                        && (name.contains("Centered") || name.contains("centered"))) {
                    drawCenteredTextWithShadowMethod = m;
                    LOGGER.info("[TextHelper] drawCenteredTextWithShadow(5): " + name + " -> " + m.getReturnType().getSimpleName());
                }
            }

            if (drawTextShadowFlagMethod == null && drawTextWithShadowMethod == null) {
                LOGGER.error("[TextHelper] Could NOT find drawText methods! Logging all TextRenderer methods:");
                for (Method m : ctx.getClass().getMethods()) {
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length >= 4 && p[0] == textRendererClass) {
                        StringBuilder sb = new StringBuilder(m.getName()).append("(");
                        for (int i = 0; i < p.length; i++) { sb.append(p[i].getSimpleName()).append(i < p.length - 1 ? "," : ""); }
                        sb.append(") -> ").append(m.getReturnType().getSimpleName());
                        LOGGER.error("[TextHelper]   " + sb);
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error("[TextHelper] init failed", e);
        }
    }

    /** drawText with shadow=true */
    public static void drawTextWithShadow(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color) {
        init(context);
        if (drawTextShadowFlagMethod != null) {
            try { drawTextShadowFlagMethod.invoke(context, textRenderer, text, x, y, color, true); return; }
            catch (Throwable e) { LOGGER.error("[TextHelper] drawText(6) failed", e); }
        }
        if (drawTextWithShadowMethod != null) {
            try { drawTextWithShadowMethod.invoke(context, textRenderer, text, x, y, color); return; }
            catch (Throwable e) { LOGGER.error("[TextHelper] drawTextWithShadow(5) failed", e); }
        }
    }

    /** drawText without shadow */
    public static void drawText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color) {
        init(context);
        if (drawTextShadowFlagMethod != null) {
            try { drawTextShadowFlagMethod.invoke(context, textRenderer, text, x, y, color, false); return; }
            catch (Throwable e) { LOGGER.error("[TextHelper] drawText(6,false) failed", e); }
        }
        if (drawTextWithShadowMethod != null) {
            try { drawTextWithShadowMethod.invoke(context, textRenderer, text, x, y, color); return; }
            catch (Throwable e) { LOGGER.error("[TextHelper] drawText fallback failed", e); }
        }
    }

    /** drawCenteredTextWithShadow */
    public static void drawCenteredTextWithShadow(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int y, int color) {
        init(context);
        if (drawCenteredTextWithShadowMethod != null) {
            try { drawCenteredTextWithShadowMethod.invoke(context, textRenderer, text, centerX, y, color); return; }
            catch (Throwable e) { LOGGER.error("[TextHelper] drawCenteredTextWithShadow failed", e); }
        }
        // Fallback: draw manually centered
        int textWidth = textRenderer.getWidth(text);
        drawTextWithShadow(context, textRenderer, text, centerX - textWidth / 2, y, color);
    }
}
