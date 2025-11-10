#include <jni.h>
#include <cmath>
#include <algorithm>

// Math constants
#define M_PI 3.14159265358979323846
#define EASE_OUT_CUBIC(x) (1.0 - std::pow(1.0 - (x), 3.0))
#define EASE_IN_OUT_CUBIC(x) ((x) < 0.5 ? 4.0 * (x) * (x) * (x) : 1.0 - std::pow(-2.0 * (x) + 2.0, 3.0) / 2.0)

// Clamp utility function
inline int clamp(int value, int min, int max) {
    return std::max(min, std::min(max, value));
}

inline float clamp(float value, float min, float max) {
    return std::max(min, std::min(max, value));
}

// Smooth interpolation between two values
inline float lerp(float a, float b, float t) {
    return a + (b - a) * t;
}

// Extract ARGB components from 32-bit integer
struct ColorRGBA {
    int a, r, g, b;
    
    ColorRGBA(int color) {
        a = (color >> 24) & 0xFF;
        r = (color >> 16) & 0xFF;
        g = (color >> 8) & 0xFF;
        b = color & 0xFF;
    }
    
    int toInt() const {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
};

extern "C" {

/**
 * Calculate optimal Y position for GUI items with smooth centering
 * Enhanced version with better layout calculations
 */
JNIEXPORT jint JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeCalculateOptimalYPosition(
    JNIEnv *env, jclass clazz, jint screenWidth, jint screenHeight, jint itemCount) {
    
    if (itemCount <= 0) {
        return screenHeight / 2;
    }
    
    // Dynamic item height calculation
    int itemHeight = 25;
    int itemSpacing = 2;
    int padding = 10;
    
    int totalHeight = (itemCount * itemHeight) + ((itemCount - 1) * itemSpacing) + (padding * 2);
    int optimalY = (screenHeight - totalHeight) / 2;
    
    // Ensure minimum margin from top and bottom
    int minMargin = 50;
    if (optimalY < minMargin) {
        optimalY = minMargin;
    } else if (optimalY + totalHeight > screenHeight - minMargin) {
        optimalY = screenHeight - totalHeight - minMargin;
    }
    
    return optimalY;
}

/**
 * Enhanced color blending with smooth interpolation
 * Supports multiple blending modes
 */
JNIEXPORT jint JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeBlendColors(
    JNIEnv *env, jclass clazz, jint color1, jint color2, jfloat blendFactor) {
    
    // Clamp blend factor
    float t = clamp(blendFactor, 0.0f, 1.0f);
    
    ColorRGBA c1(color1);
    ColorRGBA c2(color2);
    
    // Smooth interpolation with gamma correction for better visual results
    float gamma = 2.2f;
    
    // Convert to linear space for blending
    auto toLinear = [gamma](int component) -> float {
        float normalized = component / 255.0f;
        return std::pow(normalized, gamma);
    };
    
    auto toGamma = [gamma](float linear) -> int {
        float normalized = std::pow(linear, 1.0f / gamma);
        return clamp(static_cast<int>(normalized * 255.0f), 0, 255);
    };
    
    // Blend in linear space
    float r = lerp(toLinear(c1.r), toLinear(c2.r), t);
    float g = lerp(toLinear(c1.g), toLinear(c2.g), t);
    float b = lerp(toLinear(c1.b), toLinear(c2.b), t);
    int a = static_cast<int>(lerp(static_cast<float>(c1.a), static_cast<float>(c2.a), t));
    
    ColorRGBA result(0);
    result.a = clamp(a, 0, 255);
    result.r = toGamma(r);
    result.g = toGamma(g);
    result.b = toGamma(b);
    
    return result.toInt();
}

/**
 * Calculate gradient color based on totem count
 * Creates smooth color transitions from green to red
 */
JNIEXPORT jint JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeCalculateGradientColor(
    JNIEnv *env, jclass clazz, jint count, jint minCount, jint maxCount, 
    jint startColor, jint endColor) {
    
    if (minCount >= maxCount) {
        return startColor;
    }
    
    int clampedCount = clamp(count, minCount, maxCount);
    float ratio = static_cast<float>(clampedCount - minCount) / static_cast<float>(maxCount - minCount);
    
    // Apply easing for smoother color transition
    float easedRatio = static_cast<float>(EASE_IN_OUT_CUBIC(ratio));
    
    return Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeBlendColors(
        env, clazz, startColor, endColor, easedRatio);
}

/**
 * Calculate smooth animation value using easing functions
 * Returns interpolated value between start and end
 */
JNIEXPORT jfloat JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeEaseOutCubic(
    JNIEnv *env, jclass clazz, jfloat progress) {
    
    float t = clamp(progress, 0.0f, 1.0f);
    return static_cast<float>(EASE_OUT_CUBIC(t));
}

JNIEXPORT jfloat JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeEaseInOutCubic(
    JNIEnv *env, jclass clazz, jfloat progress) {
    
    float t = clamp(progress, 0.0f, 1.0f);
    return static_cast<float>(EASE_IN_OUT_CUBIC(t));
}

/**
 * Calculate optimal layout dimensions for HUD
 * Returns packed integer: width in upper 16 bits, height in lower 16 bits
 */
JNIEXPORT jint JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeCalculateLayoutDimensions(
    JNIEnv *env, jclass clazz, jint itemCount, jint maxItemWidth, 
    jint itemHeight, jint itemSpacing, jint padding) {
    
    if (itemCount <= 0) {
        int defaultWidth = maxItemWidth + (padding * 2);
        int defaultHeight = itemHeight + (padding * 2);
        return (defaultWidth << 16) | defaultHeight;
    }
    
    int width = maxItemWidth + (padding * 2);
    int height = (itemCount * itemHeight) + ((itemCount - 1) * itemSpacing) + (padding * 2);
    
    return (width << 16) | height;
}

/**
 * Calculate smooth scale animation
 * Provides smooth scaling effect for count numbers
 */
JNIEXPORT jfloat JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeCalculateScaleAnimation(
    JNIEnv *env, jclass clazz, jfloat baseScale, jfloat animationProgress, 
    jfloat maxScale, jfloat minScale) {
    
    float progress = clamp(animationProgress, 0.0f, 1.0f);
    float easedProgress = static_cast<float>(EASE_OUT_CUBIC(progress));
    
    // Bounce effect: scale up then back down
    float scale;
    if (progress < 0.5f) {
        // Scale up
        scale = lerp(baseScale, maxScale, easedProgress * 2.0f);
    } else {
        // Scale down with slight overshoot
        float bounceProgress = (progress - 0.5f) * 2.0f;
        float bounceEased = static_cast<float>(EASE_OUT_CUBIC(bounceProgress));
        scale = lerp(maxScale, baseScale, bounceEased);
        
        // Add slight overshoot
        if (bounceProgress < 0.3f) {
            scale = lerp(maxScale, baseScale * 1.1f, bounceProgress / 0.3f);
        }
    }
    
    return clamp(scale, minScale, maxScale);
}

/**
 * Calculate alpha fade for smooth transitions
 */
JNIEXPORT jint JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeCalculateAlphaFade(
    JNIEnv *env, jclass clazz, jint baseColor, jfloat fadeProgress) {
    
    float progress = clamp(fadeProgress, 0.0f, 1.0f);
    ColorRGBA color(baseColor);
    
    // Apply fade
    int newAlpha = static_cast<int>(color.a * progress);
    color.a = clamp(newAlpha, 0, 255);
    
    return color.toInt();
}

/**
 * Calculate smooth position interpolation for animations
 */
JNIEXPORT jfloat JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeLerp(
    JNIEnv *env, jclass clazz, jfloat start, jfloat end, jfloat progress) {
    
    float t = clamp(progress, 0.0f, 1.0f);
    return lerp(start, end, t);
}

/**
 * Calculate HSL to RGB conversion for advanced color manipulation
 * Returns packed ARGB integer
 */
JNIEXPORT jint JNICALL
Java_com_m4ssive_totemcounterv2_nativecode_NativeGUIHelper_nativeHSLToRGB(
    JNIEnv *env, jclass clazz, jfloat hue, jfloat saturation, jfloat lightness, jint alpha) {
    
    // Normalize HSL values
    float h = std::fmod(hue, 360.0f) / 360.0f;
    float s = clamp(saturation, 0.0f, 1.0f);
    float l = clamp(lightness, 0.0f, 1.0f);
    int a = clamp(alpha, 0, 255);
    
    float r, g, b;
    
    if (s == 0.0f) {
        // Grayscale
        r = g = b = l;
    } else {
        auto hue2rgb = [](float p, float q, float t) -> float {
            if (t < 0.0f) t += 1.0f;
            if (t > 1.0f) t -= 1.0f;
            if (t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
            if (t < 1.0f / 2.0f) return q;
            if (t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
            return p;
        };
        
        float q = l < 0.5f ? l * (1.0f + s) : l + s - l * s;
        float p = 2.0f * l - q;
        
        r = hue2rgb(p, q, h + 1.0f / 3.0f);
        g = hue2rgb(p, q, h);
        b = hue2rgb(p, q, h - 1.0f / 3.0f);
    }
    
    ColorRGBA result(0);
    result.a = a;
    result.r = clamp(static_cast<int>(r * 255.0f), 0, 255);
    result.g = clamp(static_cast<int>(g * 255.0f), 0, 255);
    result.b = clamp(static_cast<int>(b * 255.0f), 0, 255);
    
    return result.toInt();
}

}
