package com.m4ssive.totemcounterv2.config;

public enum PresetColors {
    SOFT_WHITE("Soft White", 0xFFF5F5F5),
    SOFT_PINK("Soft Pink", 0xFFFFB6C1),
    SOFT_BLUE("Soft Blue", 0xFFADD8E6),
    SOFT_GREEN("Soft Green", 0xFF90EE90),
    SOFT_YELLOW("Soft Yellow", 0xFFFFF8DC),
    SOFT_PURPLE("Soft Purple", 0xFFDDA0DD),
    SOFT_ORANGE("Soft Orange", 0xFFFFDAB9),
    SOFT_MINT("Soft Mint", 0xFF98FF98),
    
    BG_DARK("Dark BG", 0x66000000),
    BG_GRAY("Gray BG", 0x66202020),
    BG_BLUE("Blue BG", 0x660F0F1E),
    BG_GREEN("Green BG", 0x660F1E0F),
    BG_PURPLE("Purple BG", 0x66170F1E),
    BG_CLEAR("Very Clear BG", 0x33000000),
    
    BORDER_WHITE("White Border", 0xFFFFFFFF),
    BORDER_GOLD("Gold Border", 0xFFFFD700),
    BORDER_AQUA("Aqua Border", 0xFF00FFFF),
    BORDER_RED("Red Border", 0xFFFF6B6B),
    BORDER_LIME("Lime Border", 0xFF00FF00),
    
    WHITE("White", 0xFFFFFFFF),
    YELLOW("Yellow", 0xFFFFFF00),
    GOLD("Gold", 0xFFFFD700),
    LIME("Lime", 0xFF00FF00),
    AQUA("Aqua", 0xFF00FFFF),
    LIGHT_BLUE("Light Blue", 0xFF87CEEB),
    RED("Red", 0xFFFF0000),
    ORANGE("Orange", 0xFFFFA500),
    PINK("Pink", 0xFFFFC0CB),
    MAGENTA("Magenta", 0xFFFF00FF);
    
    private final String displayName;
    private final int color;
    
    PresetColors(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getColor() {
        return color;
    }
    
    public static PresetColors fromColor(int color) {
        for (PresetColors preset : values()) {
            if (preset.color == color) {
                return preset;
            }
        }
        return SOFT_WHITE;
    }
    
    public static PresetColors[] getTextColors() {
        return new PresetColors[] {
            SOFT_WHITE, SOFT_PINK, SOFT_BLUE, SOFT_GREEN,
            SOFT_YELLOW, SOFT_PURPLE, SOFT_ORANGE, SOFT_MINT
        };
    }
    
    public static PresetColors[] getBackgroundColors() {
        return new PresetColors[] {
            BG_DARK, BG_GRAY, BG_BLUE, BG_GREEN, BG_PURPLE, BG_CLEAR
        };
    }
    
    public static PresetColors[] getBorderColors() {
        return new PresetColors[] {
            BORDER_WHITE, BORDER_GOLD, BORDER_AQUA, BORDER_RED, BORDER_LIME
        };
    }
}

