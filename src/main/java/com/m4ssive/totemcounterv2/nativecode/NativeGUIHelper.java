package com.m4ssive.totemcounterv2.nativecode;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;

public class NativeGUIHelper {
    private static boolean nativeLoaded = false;
    
    static {
        try {
            String libName = System.mapLibraryName("totemcounterv2_native");
            System.loadLibrary("totemcounterv2_native");
            nativeLoaded = true;
            TotemCounterV2Mod.LOGGER.info("✅ Native C++ library loaded successfully: {}", libName);
        } catch (UnsatisfiedLinkError e) {
            nativeLoaded = false;
            TotemCounterV2Mod.LOGGER.debug("Native library not found, using Java fallback: {}", e.getMessage());
        }
    }
    
    private static native int nativeCalculateOptimalYPosition(int screenWidth, int screenHeight, int itemCount);
    private static native int nativeBlendColors(int color1, int color2, float blendFactor);
    private static native int nativeCalculateGradientColor(int count, int minCount, int maxCount, int startColor, int endColor);
    private static native float nativeEaseOutCubic(float progress);
    private static native float nativeEaseInOutCubic(float progress);
    private static native int nativeCalculateLayoutDimensions(int itemCount, int maxItemWidth, int itemHeight, int itemSpacing, int padding);
    private static native float nativeCalculateScaleAnimation(float baseScale, float animationProgress, float maxScale, float minScale);
    private static native int nativeCalculateAlphaFade(int baseColor, float fadeProgress);
    private static native float nativeLerp(float start, float end, float progress);
    private static native int nativeHSLToRGB(float hue, float saturation, float lightness, int alpha);
    
    public static boolean isNativeLoaded() {
        return nativeLoaded;
    }
    
    public static int calculateOptimalYPosition(int screenWidth, int screenHeight, int itemCount) {
        if (nativeLoaded) {
            try {
                return nativeCalculateOptimalYPosition(screenWidth, screenHeight, itemCount);
            } catch (UnsatisfiedLinkError e) {
                TotemCounterV2Mod.LOGGER.warn("Native method call failed, using fallback", e);
                return calculateOptimalYPositionFallback(screenWidth, screenHeight, itemCount);
            }
        }
        return calculateOptimalYPositionFallback(screenWidth, screenHeight, itemCount);
    }
    
    public static int blendColors(int color1, int color2, float blendFactor) {
        if (nativeLoaded) {
            try {
                return nativeBlendColors(color1, color2, blendFactor);
            } catch (UnsatisfiedLinkError e) {
                return blendColorsFallback(color1, color2, blendFactor);
            }
        }
        return blendColorsFallback(color1, color2, blendFactor);
    }
    
    public static int calculateGradientColor(int count, int minCount, int maxCount, int startColor, int endColor) {
        if (nativeLoaded) {
            try {
                return nativeCalculateGradientColor(count, minCount, maxCount, startColor, endColor);
            } catch (UnsatisfiedLinkError e) {
                return calculateGradientColorFallback(count, minCount, maxCount, startColor, endColor);
            }
        }
        return calculateGradientColorFallback(count, minCount, maxCount, startColor, endColor);
    }
    
    public static float easeOutCubic(float progress) {
        if (nativeLoaded) {
            try {
                return nativeEaseOutCubic(progress);
            } catch (UnsatisfiedLinkError e) {
                return easeOutCubicFallback(progress);
            }
        }
        return easeOutCubicFallback(progress);
    }
    
    public static float easeInOutCubic(float progress) {
        if (nativeLoaded) {
            try {
                return nativeEaseInOutCubic(progress);
            } catch (UnsatisfiedLinkError e) {
                return easeInOutCubicFallback(progress);
            }
        }
        return easeInOutCubicFallback(progress);
    }
    
    public static int[] calculateLayoutDimensions(int itemCount, int maxItemWidth, int itemHeight, int itemSpacing, int padding) {
        if (nativeLoaded) {
            try {
                int packed = nativeCalculateLayoutDimensions(itemCount, maxItemWidth, itemHeight, itemSpacing, padding);
                int width = (packed >> 16) & 0xFFFF;
                int height = packed & 0xFFFF;
                return new int[]{width, height};
            } catch (UnsatisfiedLinkError e) {
                return calculateLayoutDimensionsFallback(itemCount, maxItemWidth, itemHeight, itemSpacing, padding);
            }
        }
        return calculateLayoutDimensionsFallback(itemCount, maxItemWidth, itemHeight, itemSpacing, padding);
    }
    
    public static float calculateScaleAnimation(float baseScale, float animationProgress, float maxScale, float minScale) {
        if (nativeLoaded) {
            try {
                return nativeCalculateScaleAnimation(baseScale, animationProgress, maxScale, minScale);
            } catch (UnsatisfiedLinkError e) {
                return calculateScaleAnimationFallback(baseScale, animationProgress, maxScale, minScale);
            }
        }
        return calculateScaleAnimationFallback(baseScale, animationProgress, maxScale, minScale);
    }
    
    public static int calculateAlphaFade(int baseColor, float fadeProgress) {
        if (nativeLoaded) {
            try {
                return nativeCalculateAlphaFade(baseColor, fadeProgress);
            } catch (UnsatisfiedLinkError e) {
                return calculateAlphaFadeFallback(baseColor, fadeProgress);
            }
        }
        return calculateAlphaFadeFallback(baseColor, fadeProgress);
    }
    
    public static float lerp(float start, float end, float progress) {
        if (nativeLoaded) {
            try {
                return nativeLerp(start, end, progress);
            } catch (UnsatisfiedLinkError e) {
                return lerpFallback(start, end, progress);
            }
        }
        return lerpFallback(start, end, progress);
    }
    
    public static int hslToRGB(float hue, float saturation, float lightness, int alpha) {
        if (nativeLoaded) {
            try {
                return nativeHSLToRGB(hue, saturation, lightness, alpha);
            } catch (UnsatisfiedLinkError e) {
                return hslToRGBFallback(hue, saturation, lightness, alpha);
            }
        }
        return hslToRGBFallback(hue, saturation, lightness, alpha);
    }
    
    private static int calculateOptimalYPositionFallback(int screenWidth, int screenHeight, int itemCount) {
        if (itemCount <= 0) {
            return screenHeight / 2;
        }
        
        int itemHeight = 25;
        int itemSpacing = 2;
        int padding = 10;
        
        int totalHeight = (itemCount * itemHeight) + ((itemCount - 1) * itemSpacing) + (padding * 2);
        int optimalY = (screenHeight - totalHeight) / 2;
        
        int minMargin = 50;
        if (optimalY < minMargin) {
            optimalY = minMargin;
        } else if (optimalY + totalHeight > screenHeight - minMargin) {
            optimalY = screenHeight - totalHeight - minMargin;
        }
        
        return optimalY;
    }
    
    private static int blendColorsFallback(int color1, int color2, float blendFactor) {
        float t = Math.max(0.0f, Math.min(1.0f, blendFactor));
        
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        
        a = Math.max(0, Math.min(255, a));
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    private static int calculateGradientColorFallback(int count, int minCount, int maxCount, int startColor, int endColor) {
        if (minCount >= maxCount) {
            return startColor;
        }
        
        int clampedCount = Math.max(minCount, Math.min(maxCount, count));
        float ratio = (float) (clampedCount - minCount) / (float) (maxCount - minCount);
        float easedRatio = easeInOutCubicFallback(ratio);
        
        return blendColorsFallback(startColor, endColor, easedRatio);
    }
    
    private static float easeOutCubicFallback(float progress) {
        float t = Math.max(0.0f, Math.min(1.0f, progress));
        return (float) (1.0 - Math.pow(1.0 - t, 3.0));
    }
    
    private static float easeInOutCubicFallback(float progress) {
        float t = Math.max(0.0f, Math.min(1.0f, progress));
        if (t < 0.5) {
            return (float) (4.0 * t * t * t);
        } else {
            return (float) (1.0 - Math.pow(-2.0 * t + 2.0, 3.0) / 2.0);
        }
    }
    
    private static int[] calculateLayoutDimensionsFallback(int itemCount, int maxItemWidth, int itemHeight, int itemSpacing, int padding) {
        if (itemCount <= 0) {
            int defaultWidth = maxItemWidth + (padding * 2);
            int defaultHeight = itemHeight + (padding * 2);
            return new int[]{defaultWidth, defaultHeight};
        }
        
        int width = maxItemWidth + (padding * 2);
        int height = (itemCount * itemHeight) + ((itemCount - 1) * itemSpacing) + (padding * 2);
        
        return new int[]{width, height};
    }
    
    private static float calculateScaleAnimationFallback(float baseScale, float animationProgress, float maxScale, float minScale) {
        float progress = Math.max(0.0f, Math.min(1.0f, animationProgress));
        float easedProgress = easeOutCubicFallback(progress);
        
        float scale;
        if (progress < 0.5f) {
            scale = baseScale + (maxScale - baseScale) * (easedProgress * 2.0f);
        } else {
            float bounceProgress = (progress - 0.5f) * 2.0f;
            float bounceEased = easeOutCubicFallback(bounceProgress);
            scale = maxScale + (baseScale - maxScale) * bounceEased;
            
            if (bounceProgress < 0.3f) {
                scale = maxScale + (baseScale * 1.1f - maxScale) * (bounceProgress / 0.3f);
            }
        }
        
        return Math.max(minScale, Math.min(maxScale, scale));
    }
    
    private static int calculateAlphaFadeFallback(int baseColor, float fadeProgress) {
        float progress = Math.max(0.0f, Math.min(1.0f, fadeProgress));
        int alpha = (baseColor >> 24) & 0xFF;
        int newAlpha = (int) (alpha * progress);
        newAlpha = Math.max(0, Math.min(255, newAlpha));
        
        return (newAlpha << 24) | (baseColor & 0x00FFFFFF);
    }
    
    private static float lerpFallback(float start, float end, float progress) {
        float t = Math.max(0.0f, Math.min(1.0f, progress));
        return start + (end - start) * t;
    }
    
    private static int hslToRGBFallback(float hue, float saturation, float lightness, int alpha) {
        float h = (hue % 360.0f) / 360.0f;
        float s = Math.max(0.0f, Math.min(1.0f, saturation));
        float l = Math.max(0.0f, Math.min(1.0f, lightness));
        int a = Math.max(0, Math.min(255, alpha));
        
        float r, g, b;
        
        if (s == 0.0f) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1.0f + s) : l + s - l * s;
            float p = 2.0f * l - q;
            
            r = hue2rgb(p, q, h + 1.0f / 3.0f);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1.0f / 3.0f);
        }
        
        int rInt = Math.max(0, Math.min(255, (int) (r * 255.0f)));
        int gInt = Math.max(0, Math.min(255, (int) (g * 255.0f)));
        int bInt = Math.max(0, Math.min(255, (int) (b * 255.0f)));
        
        return (a << 24) | (rInt << 16) | (gInt << 8) | bInt;
    }
    
    private static float hue2rgb(float p, float q, float t) {
        if (t < 0.0f) t += 1.0f;
        if (t > 1.0f) t -= 1.0f;
        if (t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
        if (t < 1.0f / 2.0f) return q;
        if (t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        return p;
    }
}
