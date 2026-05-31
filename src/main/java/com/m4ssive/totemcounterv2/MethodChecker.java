
package com.m4ssive.totemcounterv2;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import java.lang.reflect.Method;

public class MethodChecker {
    public static void check() {
        System.out.println("Checking DrawContext methods...");
        for (Method m : DrawContext.class.getMethods()) {
            if (m.getName().contains("drawText") || m.getName().contains("method_25303")) {
                System.out.print("Method: " + m.getName() + "(");
                for (Class<?> p : m.getParameterTypes()) {
                    System.out.print(p.getSimpleName() + ", ");
                }
                System.out.println(") returns " + m.getReturnType().getSimpleName());
            }
        }
    }
}
