package com.m4ssive.totemcounterv2.util;

import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Wrapper for matrix stack operations targeting Feather 1.21.11.
 * At runtime, DrawContext.getMatrices() returns org.joml.Matrix3x2fStack
 * which uses pushMatrix()/popMatrix() and 2D translate(float,float)/scale(float,float).
 * We use reflection because we compile against 1.21.1 API where it returns MatrixStack.
 */
public class GuiHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("TotemCounterV2");

    private static Method getMatricesMethod = null;
    private static Method pushMethod = null;
    private static Method popMethod = null;
    private static Method translateXYMethod = null; // translate(float, float)
    private static Method scaleXYMethod = null;     // scale(float, float)

    private static boolean initialized = false;

    private static void init(DrawContext context) {
        if (initialized) return;
        initialized = true;
        try {
            // Find getMatrices() on DrawContext - may return MatrixStack or Matrix3x2fStack
            for (Method m : context.getClass().getMethods()) {
                if (m.getParameterCount() == 0
                        && (m.getName().equals("getMatrices") || m.getName().equals("method_51448") || m.getName().equals("e"))) {
                    getMatricesMethod = m;
                    LOGGER.info("[GuiHelper] getMatrices = " + m.getName() + " -> " + m.getReturnType().getName());
                    break;
                }
            }
            if (getMatricesMethod == null) {
                // Fallback: find by return type containing "Matrix"
                for (Method m : context.getClass().getMethods()) {
                    if (m.getParameterCount() == 0 && m.getReturnType().getName().contains("Matrix")) {
                        getMatricesMethod = m;
                        LOGGER.info("[GuiHelper] getMatrices fallback = " + m.getName() + " -> " + m.getReturnType().getName());
                        break;
                    }
                }
            }

            if (getMatricesMethod != null) {
                // Get actual runtime matrix object to discover its methods
                Object matrices = getMatricesMethod.invoke(context);
                if (matrices == null) {
                    LOGGER.warn("[GuiHelper] getMatrices() returned null");
                    return;
                }
                Class<?> matClass = matrices.getClass();
                LOGGER.info("[GuiHelper] Matrix class = " + matClass.getName());

                for (Method m : matClass.getMethods()) {
                    String name = m.getName();
                    int pc = m.getParameterCount();

                    // push: pushMatrix() or push()
                    if (pushMethod == null && pc == 0 && (name.equals("pushMatrix") || name.equals("push"))) {
                        pushMethod = m;
                        LOGGER.info("[GuiHelper] push = " + name);
                    }
                    // pop: popMatrix() or pop()
                    if (popMethod == null && pc == 0 && (name.equals("popMatrix") || name.equals("pop"))) {
                        popMethod = m;
                        LOGGER.info("[GuiHelper] pop = " + name);
                    }
                    // translate(float, float) - 2D version for Matrix3x2fStack
                    if (translateXYMethod == null && name.equals("translate") && pc == 2) {
                        Class<?>[] pt = m.getParameterTypes();
                        if (pt[0] == float.class && pt[1] == float.class) {
                            translateXYMethod = m;
                            LOGGER.info("[GuiHelper] translate2D = " + name + "(float,float)");
                        }
                    }
                    // scale(float, float) - 2D version
                    if (scaleXYMethod == null && name.equals("scale") && pc == 2) {
                        Class<?>[] pt = m.getParameterTypes();
                        if (pt[0] == float.class && pt[1] == float.class) {
                            scaleXYMethod = m;
                            LOGGER.info("[GuiHelper] scale2D = " + name + "(float,float)");
                        }
                    }
                }
                // Fallback: if no 2D translate, look for 3-arg float version
                if (translateXYMethod == null) {
                    for (Method m : matClass.getMethods()) {
                        if (m.getName().equals("translate") && m.getParameterCount() == 3) {
                            Class<?>[] pt = m.getParameterTypes();
                            if (pt[0] == float.class) {
                                translateXYMethod = m;
                                LOGGER.info("[GuiHelper] translate3D fallback = " + m.getName());
                                break;
                            }
                        }
                    }
                }
                if (scaleXYMethod == null) {
                    for (Method m : matClass.getMethods()) {
                        if (m.getName().equals("scale") && m.getParameterCount() == 3) {
                            Class<?>[] pt = m.getParameterTypes();
                            if (pt[0] == float.class) {
                                scaleXYMethod = m;
                                LOGGER.info("[GuiHelper] scale3D fallback = " + m.getName());
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error("[GuiHelper] init failed", e);
        }
    }

    private static Object getMatrices(DrawContext context) {
        if (getMatricesMethod == null) return null;
        try {
            return getMatricesMethod.invoke(context);
        } catch (Throwable e) {
            return null;
        }
    }

    public static void push(DrawContext context) {
        init(context);
        if (pushMethod == null) return;
        try {
            pushMethod.invoke(getMatrices(context));
        } catch (Throwable e) {
            LOGGER.error("[GuiHelper] push failed", e);
        }
    }

    public static void pop(DrawContext context) {
        init(context);
        if (popMethod == null) return;
        try {
            popMethod.invoke(getMatrices(context));
        } catch (Throwable e) {
            LOGGER.error("[GuiHelper] pop failed", e);
        }
    }

    public static void translate(DrawContext context, double x, double y, double z) {
        init(context);
        if (translateXYMethod == null) return;
        try {
            int pc = translateXYMethod.getParameterCount();
            Object mat = getMatrices(context);
            if (pc == 2) {
                translateXYMethod.invoke(mat, (float) x, (float) y);
            } else {
                translateXYMethod.invoke(mat, (float) x, (float) y, (float) z);
            }
        } catch (Throwable e) {
            LOGGER.error("[GuiHelper] translate failed", e);
        }
    }

    public static void scale(DrawContext context, float x, float y, float z) {
        init(context);
        if (scaleXYMethod == null) return;
        try {
            int pc = scaleXYMethod.getParameterCount();
            Object mat = getMatrices(context);
            if (pc == 2) {
                scaleXYMethod.invoke(mat, x, y);
            } else {
                scaleXYMethod.invoke(mat, x, y, z);
            }
        } catch (Throwable e) {
            LOGGER.error("[GuiHelper] scale failed", e);
        }
    }
}
