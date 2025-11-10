package com.m4ssive.totemcounterv2.gui;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.config.ModConfig;
import com.m4ssive.totemcounterv2.config.PresetColors;
import com.m4ssive.totemcounterv2.nativecode.NativeGUIHelper;
import com.m4ssive.totemcounterv2.sound.SoundManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;

    private long openTime = System.currentTimeMillis();
    private float animationProgress = 0.0f;
    private int hoveredWidgetIndex = -1;
    private int selectedWidgetIndex = -1;
    private static final int HEADER_GRADIENT_TOP = 0xFF1a1a2e;
    private static final int HEADER_GRADIENT_BOTTOM = 0xFF16213e;
    private static final int PANEL_BG = 0xDD1e1e2e;
    private static final int PANEL_BORDER = 0xFF1e3a5f;
    private static final int ACCENT_COLOR_GENERAL = 0xFF4ade80;
    private static final int ACCENT_COLOR_DISPLAY = 0xFF3b82f6;
    private static final int ACCENT_COLOR_SOUND = 0xFFf59e0b;
    private static final int ACCENT_COLOR_INVENTORY = 0xFF10b981;
    private static final int ACCENT_COLOR_MILESTONE = 0xFFef4444;
    private static final int TEXT_PRIMARY = 0xFFFFFFFF;
    private static final int TEXT_SECONDARY = 0xFFCCCCCC;
    private static final int BUTTON_BG = 0xDD2a2a3a;
    private static final int BUTTON_HOVER = 0xDD3a3a4a;
    private static final int BUTTON_ACTIVE = 0xDD4a4a5a;
    private static final int CHECKBOX_CHECKED = 0xFF4ade80;
    private static final int GLOW_COLOR = 0xFF00D9FF;
    private static final int WIDGET_BG = 0xCC252535;
    private static final ItemStack TOTEM_ICON = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static final ItemStack REDSTONE_ICON = new ItemStack(Items.REDSTONE);
    private static final ItemStack EYE_ICON = new ItemStack(Items.ENDER_EYE);
    private static final ItemStack BELL_ICON = new ItemStack(Items.BELL);
    private static final ItemStack NAME_TAG_ICON = new ItemStack(Items.NAME_TAG);
    private static final ItemStack CHEST_ICON = new ItemStack(Items.CHEST);
    private static final ItemStack TARGET_ICON = new ItemStack(Items.TARGET);
    private static final ItemStack PAINTING_ICON = new ItemStack(Items.PAINTING);
    private static final ItemStack MUSIC_DISC_ICON = new ItemStack(Items.MUSIC_DISC_11);
    private static final ItemStack COMPASS_ICON = new ItemStack(Items.COMPASS);
    private static final ItemStack BOOK_ICON = new ItemStack(Items.BOOK);
    private static final ItemStack GOLD_INGOT_ICON = new ItemStack(Items.GOLD_INGOT);
    private static final ItemStack DIAMOND_ICON = new ItemStack(Items.DIAMOND);
    private static final ItemStack EMERALD_ICON = new ItemStack(Items.EMERALD);
    private static final ItemStack NETHER_STAR_ICON = new ItemStack(Items.NETHER_STAR);
    private static final ItemStack CLOCK_ICON = new ItemStack(Items.CLOCK);
    private static final ItemStack MAP_ICON = new ItemStack(Items.MAP);
    
    private static class CustomWidget {
        int x, y, width, height;
        String label;
        ItemStack icon;
        WidgetType type;
        int sectionIndex;
        boolean checked;
        float value;
        float minValue, maxValue;
        PresetColors selectedColor;
        PresetColors[] colorOptions;
        String textValue;
        Object data;
        
        CustomWidget(int x, int y, int width, int height, String label, ItemStack icon, 
                    WidgetType type, int sectionIndex) {
            this.x = x; this.y = y; this.width = width; this.height = height;
            this.label = label; this.icon = icon; this.type = type; this.sectionIndex = sectionIndex;
        }
    }
    
    private enum WidgetType {
        CHECKBOX, COLOR_BUTTON, SLIDER, TEXT_FIELD, BUTTON
    }
    
    private static class SectionHeader {
        String text;
        int x, y;
        ItemStack icon;
        int accentColor;
        int index;
        boolean expanded;
        int widgetCount;
        
        SectionHeader(String text, int x, int y, ItemStack icon, int accentColor, int index) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.icon = icon;
            this.accentColor = accentColor;
            this.index = index;
            this.expanded = true;
            this.widgetCount = 0;
        }
    }
    
    private final List<CustomWidget> widgets = new ArrayList<>();
    private final List<SectionHeader> sectionHeaders = new ArrayList<>();
    
    private final boolean[] sectionExpanded = new boolean[6];
    private int scrollOffset = 0;
    private static final int SCROLL_SPEED = 10;
    
    private CustomWidget editingTextField = null;
    private StringBuilder textInputBuffer = new StringBuilder();

    public ConfigScreen(Screen parent, ModConfig config) {
        super(Text.literal("TotemCounter Settings"));
        this.parent = parent;
        this.config = config;
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        this.children().clear();
        this.widgets.clear();
        this.sectionHeaders.clear();
        
        boolean allFalse = true;
        for (boolean expanded : sectionExpanded) {
            if (expanded) {
                allFalse = false;
                break;
        }
        }
        if (allFalse) {
            for (int i = 0; i < sectionExpanded.length; i++) {
                sectionExpanded[i] = true;
            }
        }
        
        int panelX = this.width / 2 - 200;
        int panelY = 60;
        int panelWidth = 400;
        
        int y = panelY + 30;
        int x = panelX + 20;
        int widgetWidth = panelWidth - 40;
        
        SectionHeader generalHeader = new SectionHeader("General Settings", x, y, COMPASS_ICON, ACCENT_COLOR_GENERAL, 0);
        generalHeader.expanded = sectionExpanded[0];
        sectionHeaders.add(generalHeader);
        y += 30;
        
        if (sectionExpanded[0]) {
            addCustomCheckbox(x, y, "Enable HUD", config.enableHud, TOTEM_ICON, 0);
            y += 32;
            addCustomCheckbox(x, y, "Show Self", config.showSelf, EYE_ICON, 0);
            y += 32;
            addCustomCheckbox(x, y, "Only Nearby Players", config.showOnlyNearby, MAP_ICON, 0);
            y += 32;
            addCustomCheckbox(x, y, "Auto Reset Counter", config.autoCounterReset, CLOCK_ICON, 0);
            y += 32;
            addCustomCheckbox(x, y, "Auto Reset On Death", config.autoResetOnDeath, TARGET_ICON, 0);
            y += 32;
            addCustomCheckbox(x, y, "Show in Tab List", config.showInTabList, BOOK_ICON, 0);
            y += 32;
            PresetColors[] tabListColors = new PresetColors[] {
                com.m4ssive.totemcounterv2.config.PresetColors.WHITE,
                com.m4ssive.totemcounterv2.config.PresetColors.YELLOW,
                com.m4ssive.totemcounterv2.config.PresetColors.GOLD,
                com.m4ssive.totemcounterv2.config.PresetColors.LIME,
                com.m4ssive.totemcounterv2.config.PresetColors.AQUA,
                com.m4ssive.totemcounterv2.config.PresetColors.LIGHT_BLUE,
                com.m4ssive.totemcounterv2.config.PresetColors.RED,
                com.m4ssive.totemcounterv2.config.PresetColors.ORANGE,
                com.m4ssive.totemcounterv2.config.PresetColors.PINK,
                com.m4ssive.totemcounterv2.config.PresetColors.MAGENTA
            };
            addCustomColorButton(x, y, "Tab List Color", tabListColors,
                PresetColors.fromColor(config.tabListColor), BOOK_ICON, 0);
            generalHeader.widgetCount = 7;
        }
        y += 40;
        
        SectionHeader displayHeader = new SectionHeader("Display Settings", x, y, PAINTING_ICON, ACCENT_COLOR_DISPLAY, 1);
        displayHeader.expanded = sectionExpanded[1];
        sectionHeaders.add(displayHeader);
        y += 30;
        
        if (sectionExpanded[1]) {
            addCustomCheckbox(x, y, "Show Background", config.showBackground, BOOK_ICON, 1);
            y += 32;
            addCustomCheckbox(x, y, "Show Border", config.showBorder, TARGET_ICON, 1);
            y += 32;
            addCustomCheckbox(x, y, "Show Icon", config.showIcon, DIAMOND_ICON, 1);
            y += 32;
            addCustomColorButton(x, y, "Text Color", PresetColors.getTextColors(), 
                PresetColors.fromColor(config.textColor), GOLD_INGOT_ICON, 1);
            y += 36;
            addCustomColorButton(x, y, "Background Color", PresetColors.getBackgroundColors(),
                PresetColors.fromColor(config.backgroundColor), EMERALD_ICON, 1);
            y += 36;
            addCustomColorButton(x, y, "Border Color", PresetColors.getBorderColors(),
                PresetColors.fromColor(config.borderColor), NETHER_STAR_ICON, 1);
            y += 36;
            addCustomSlider(x, y, "Scale", config.countScale, 0.5f, 3.0f, PAINTING_ICON, 1);
            displayHeader.widgetCount = 7;
        }
        y += 40;
        
        SectionHeader soundHeader = new SectionHeader("Sound Settings", x, y, MUSIC_DISC_ICON, ACCENT_COLOR_SOUND, 2);
        soundHeader.expanded = sectionExpanded[2];
        sectionHeaders.add(soundHeader);
        y += 30;
        
        if (sectionExpanded[2]) {
            addCustomCheckbox(x, y, "Enable Sounds", config.enableSounds, BELL_ICON, 2);
            y += 32;
            CustomWidget soundTypeWidget = new CustomWidget(x + 25, y, widgetWidth - 25, 22, "Sound Type", MUSIC_DISC_ICON, WidgetType.BUTTON, 2);
        SoundManager.SoundType[] soundTypes = SoundManager.SoundType.values();
            soundTypeWidget.data = soundTypes;
            int currentSoundTypeIndex = config.soundType >= 0 && config.soundType < soundTypes.length ? config.soundType : 0;
            soundTypeWidget.textValue = soundTypes[currentSoundTypeIndex].getDisplayName();
            widgets.add(soundTypeWidget);
            y += 34;
            addCustomSlider(x, y, "Volume", config.soundVolume, 0.0f, 1.0f, GOLD_INGOT_ICON, 2);
            y += 36;
            addCustomSlider(x, y, "Pitch", config.soundPitch, 0.5f, 2.0f, DIAMOND_ICON, 2);
            soundHeader.widgetCount = 4;
        }
        y += 40;
        
        SectionHeader inventoryHeader = new SectionHeader("Inventory Settings", x, y, CHEST_ICON, ACCENT_COLOR_INVENTORY, 3);
        inventoryHeader.expanded = sectionExpanded[3];
        sectionHeaders.add(inventoryHeader);
        y += 30;
        
        if (sectionExpanded[3]) {
            addCustomCheckbox(x, y, "Show Inventory Count", config.showInventoryTotemCount, CHEST_ICON, 3);
            y += 32;
            PresetColors[] brightColors = new PresetColors[] {
                com.m4ssive.totemcounterv2.config.PresetColors.WHITE,
                com.m4ssive.totemcounterv2.config.PresetColors.YELLOW,
                com.m4ssive.totemcounterv2.config.PresetColors.GOLD,
                com.m4ssive.totemcounterv2.config.PresetColors.LIME,
                com.m4ssive.totemcounterv2.config.PresetColors.AQUA,
                com.m4ssive.totemcounterv2.config.PresetColors.LIGHT_BLUE,
                com.m4ssive.totemcounterv2.config.PresetColors.RED,
                com.m4ssive.totemcounterv2.config.PresetColors.ORANGE,
                com.m4ssive.totemcounterv2.config.PresetColors.PINK,
                com.m4ssive.totemcounterv2.config.PresetColors.MAGENTA
            };
            addCustomColorButton(x, y, "Inventory Count Color", brightColors,
                PresetColors.fromColor(config.inventoryTotemCountColor), EMERALD_ICON, 3);
            inventoryHeader.widgetCount = 2;
        }
        y += 40;
        
        SectionHeader milestoneHeader = new SectionHeader("Milestone Settings", x, y, NETHER_STAR_ICON, ACCENT_COLOR_MILESTONE, 4);
        milestoneHeader.expanded = sectionExpanded[4];
        sectionHeaders.add(milestoneHeader);
        y += 30;
        
        if (sectionExpanded[4]) {
            addCustomCheckbox(x, y, "Enable Milestones", config.milestoneEnabled, TARGET_ICON, 4);
            y += 32;
            addCustomTextField(x, y, "Threshold", String.valueOf(config.milestoneThreshold), GOLD_INGOT_ICON, 4);
            milestoneHeader.widgetCount = 2;
        }
        y += 40;
        
        int ACCENT_COLOR_NAMETAG = 0xFF8b5cf6;
        SectionHeader nametagHeader = new SectionHeader("Nametag Settings", x, y, NAME_TAG_ICON, ACCENT_COLOR_NAMETAG, 5);
        nametagHeader.expanded = sectionExpanded[5];
        sectionHeaders.add(nametagHeader);
        y += 30;
        
        if (sectionExpanded[5]) {
            addCustomCheckbox(x, y, "Show in Nametags", config.showInNametags, NAME_TAG_ICON, 5);
            y += 32;
            PresetColors[] nametagColors = new PresetColors[] {
                com.m4ssive.totemcounterv2.config.PresetColors.WHITE,
                com.m4ssive.totemcounterv2.config.PresetColors.YELLOW,
                com.m4ssive.totemcounterv2.config.PresetColors.GOLD,
                com.m4ssive.totemcounterv2.config.PresetColors.LIME,
                com.m4ssive.totemcounterv2.config.PresetColors.AQUA,
                com.m4ssive.totemcounterv2.config.PresetColors.LIGHT_BLUE,
                com.m4ssive.totemcounterv2.config.PresetColors.RED,
                com.m4ssive.totemcounterv2.config.PresetColors.ORANGE,
                com.m4ssive.totemcounterv2.config.PresetColors.PINK,
                com.m4ssive.totemcounterv2.config.PresetColors.MAGENTA
            };
            addCustomColorButton(x, y, "Nametag Color", nametagColors,
                PresetColors.fromColor(config.nametagColor), NAME_TAG_ICON, 5);
            y += 36;
            addCustomCheckbox(x, y, "Use Custom Pop Color", config.useCustomNametagPopColor, TOTEM_ICON, 5);
            y += 32;
            PresetColors[] popColors = new PresetColors[] {
                com.m4ssive.totemcounterv2.config.PresetColors.WHITE,
                com.m4ssive.totemcounterv2.config.PresetColors.YELLOW,
                com.m4ssive.totemcounterv2.config.PresetColors.GOLD,
                com.m4ssive.totemcounterv2.config.PresetColors.LIME,
                com.m4ssive.totemcounterv2.config.PresetColors.AQUA,
                com.m4ssive.totemcounterv2.config.PresetColors.LIGHT_BLUE,
                com.m4ssive.totemcounterv2.config.PresetColors.RED,
                com.m4ssive.totemcounterv2.config.PresetColors.ORANGE,
                com.m4ssive.totemcounterv2.config.PresetColors.PINK,
                com.m4ssive.totemcounterv2.config.PresetColors.MAGENTA,
                com.m4ssive.totemcounterv2.config.PresetColors.SOFT_GREEN,
                com.m4ssive.totemcounterv2.config.PresetColors.SOFT_BLUE
            };
            addCustomColorButton(x, y, "Custom Pop Color", popColors,
                PresetColors.fromColor(config.customNametagPopColor), TOTEM_ICON, 5);
            y += 36;
            addCustomColorButton(x, y, "Pop Color (1-2)", popColors,
                PresetColors.fromColor(config.nametagPopColor1_2), TOTEM_ICON, 5);
            y += 36;
            addCustomColorButton(x, y, "Pop Color (3-4)", popColors,
                PresetColors.fromColor(config.nametagPopColor3_4), TOTEM_ICON, 5);
            y += 36;
            addCustomColorButton(x, y, "Pop Color (5-6)", popColors,
                PresetColors.fromColor(config.nametagPopColor5_6), TOTEM_ICON, 5);
            y += 36;
            addCustomColorButton(x, y, "Pop Color (7-8)", popColors,
                PresetColors.fromColor(config.nametagPopColor7_8), TOTEM_ICON, 5);
            y += 36;
            addCustomColorButton(x, y, "Pop Color (9+)", popColors,
                PresetColors.fromColor(config.nametagPopColor9Plus), TOTEM_ICON, 5);
            nametagHeader.widgetCount = 8;
        }
        
        int editModeButtonY = this.height - 45;
        int editModeButtonX = this.width / 2 - 100;
        CustomWidget editModeButton = new CustomWidget(editModeButtonX, editModeButtonY, 200, 30, "Edit HUD Position", TARGET_ICON, WidgetType.BUTTON, -1);
        editModeButton.data = "EDIT_MODE";
        editModeButton.textValue = "Edit HUD Position";
        widgets.add(editModeButton);
    }
    
    private void addCustomCheckbox(int x, int y, String label, boolean checked, ItemStack icon, int sectionIndex) {
        CustomWidget widget = new CustomWidget(x + 25, y, 350, 22, label, icon, WidgetType.CHECKBOX, sectionIndex);
        widget.checked = checked;
        widgets.add(widget);
    }
    
    private void addCustomColorButton(int x, int y, String label, PresetColors[] colors, 
            PresetColors selected, ItemStack icon, int sectionIndex) {
        CustomWidget widget = new CustomWidget(x + 25, y, 350, 22, label, icon, WidgetType.COLOR_BUTTON, sectionIndex);
        widget.colorOptions = colors;
        widget.selectedColor = selected;
        widgets.add(widget);
    }
    
    private void addCustomSlider(int x, int y, String label, float value, float min, float max, 
            ItemStack icon, int sectionIndex) {
        CustomWidget widget = new CustomWidget(x + 25, y, 350, 22, label, icon, WidgetType.SLIDER, sectionIndex);
        widget.value = value;
        widget.minValue = min;
        widget.maxValue = max;
        widgets.add(widget);
    }
    
    private void addCustomTextField(int x, int y, String label, String value, ItemStack icon, int sectionIndex) {
        CustomWidget widget = new CustomWidget(x + 130, y, 100, 22, label, icon, WidgetType.TEXT_FIELD, sectionIndex);
        widget.textValue = value;
        widgets.add(widget);
    }
    
    private void updateCheckboxConfig(String label, boolean value) {
        switch (label) {
            case "Enable HUD": config.enableHud = value; break;
            case "Show Self": config.showSelf = value; break;
            case "Only Nearby Players": config.showOnlyNearby = value; break;
            case "Auto Reset Counter": config.autoCounterReset = value; break;
            case "Auto Reset On Death": config.autoResetOnDeath = value; break;
            case "Show Background": config.showBackground = value; break;
            case "Show Border": config.showBorder = value; break;
            case "Show Icon": config.showIcon = value; break;
            case "Enable Sounds": config.enableSounds = value; break;
            case "Show in Tab List": config.showInTabList = value; break;
            case "Show Inventory Count": config.showInventoryTotemCount = value; break;
            case "Enable Milestones": config.milestoneEnabled = value; break;
            case "Show in Nametags": config.showInNametags = value; break;
            case "Use Custom Pop Color": config.useCustomNametagPopColor = value; break;
        }
    }
    
    private void updateColorConfig(String label, PresetColors color) {
        switch (label) {
            case "Text Color": config.textColor = color.getColor(); break;
            case "Background Color": config.backgroundColor = color.getColor(); break;
            case "Border Color": config.borderColor = color.getColor(); break;
            case "Inventory Count Color": config.inventoryTotemCountColor = color.getColor(); break;
            case "Tab List Color": config.tabListColor = color.getColor(); break;
            case "Nametag Color": config.nametagColor = color.getColor(); break;
            case "Custom Pop Color": config.customNametagPopColor = color.getColor(); break;
            case "Pop Color (1-2)": config.nametagPopColor1_2 = color.getColor(); break;
            case "Pop Color (3-4)": config.nametagPopColor3_4 = color.getColor(); break;
            case "Pop Color (5-6)": config.nametagPopColor5_6 = color.getColor(); break;
            case "Pop Color (7-8)": config.nametagPopColor7_8 = color.getColor(); break;
            case "Pop Color (9+)": config.nametagPopColor9Plus = color.getColor(); break;
        }
    }
    
    private void updateSliderConfig(String label, float value) {
        switch (label) {
            case "Scale": config.countScale = value; break;
            case "Volume": config.soundVolume = value; break;
            case "Pitch": config.soundPitch = value; break;
        }
    }
    
    private void saveConfig() {
        config.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        long elapsed = System.currentTimeMillis() - openTime;
        animationProgress = Math.min(1.0f, elapsed / 500.0f);
        animationProgress = NativeGUIHelper.easeOutCubic(animationProgress);
        
        int bgAlpha = (int) (200 * animationProgress);
        context.fill(0, 0, this.width, this.height, (bgAlpha << 24) | 0x101020);
        
        int headerHeight = 55;
        context.fillGradient(0, 0, this.width, headerHeight, HEADER_GRADIENT_TOP, HEADER_GRADIENT_BOTTOM);
        context.fill(0, headerHeight - 2, this.width, headerHeight, 0xAA7b2cbf);
        
        float titleOffset = (1.0f - animationProgress) * 20.0f;
        int titleY = (int) (18 + titleOffset);
        float iconPulse = (float) (Math.sin(System.currentTimeMillis() / 500.0) * 0.1f + 1.0f);
        context.getMatrices().push();
        context.getMatrices().translate(this.width / 2 - 85, titleY - 2, 0);
        context.getMatrices().scale(iconPulse, iconPulse, 1.0f);
        context.drawItem(TOTEM_ICON, 0, 0);
        context.getMatrices().pop();
        context.drawCenteredTextWithShadow(this.textRenderer, 
            this.title.copy().setStyle(net.minecraft.text.Style.EMPTY.withBold(true).withColor(0xFFE0E0FF)),
            this.width / 2 + 15, titleY, TEXT_PRIMARY);
        
        int panelX = this.width / 2 - 200;
        int panelY = 60;
        int panelWidth = 400;
        int panelHeight = this.height - panelY - 100;
        float panelAlpha = animationProgress;
        int panelBg = (int) ((PANEL_BG >> 24) * panelAlpha) << 24 | (PANEL_BG & 0x00FFFFFF);
        
        context.enableScissor(panelX, panelY, panelX + panelWidth, panelY + panelHeight);
        context.fillGradient(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 
            panelBg, (panelBg & 0xFF000000) | 0x00252535);
        
        int borderAlpha = (int) (255 * animationProgress);
        int borderColor = (borderAlpha << 24) | (PANEL_BORDER & 0x00FFFFFF);
        int borderWidth = 2;
        context.fill(panelX - borderWidth, panelY - borderWidth, panelX + panelWidth + borderWidth, panelY, borderColor);
        context.fill(panelX - borderWidth, panelY + panelHeight, panelX + panelWidth + borderWidth, panelY + panelHeight + borderWidth, borderColor);
        context.fill(panelX - borderWidth, panelY - borderWidth, panelX, panelY + panelHeight + borderWidth, borderColor);
        context.fill(panelX + panelWidth, panelY - borderWidth, panelX + panelWidth + borderWidth, panelY + panelHeight + borderWidth, borderColor);
        
        context.getMatrices().push();
        context.getMatrices().translate(0, -scrollOffset, 0);
        
        for (SectionHeader header : sectionHeaders) {
            drawSectionHeader(context, header, mouseX, mouseY + scrollOffset);
        }
        
        hoveredWidgetIndex = -1;
        for (int i = 0; i < widgets.size(); i++) {
            CustomWidget widget = widgets.get(i);
            
            if (widget.sectionIndex == -1 && widget.data instanceof String && 
                widget.data.equals("EDIT_MODE")) {
                continue;
            }
            
            if (!sectionExpanded[widget.sectionIndex]) {
                continue;
            }
            
            boolean isHovered = mouseX >= widget.x && mouseX <= widget.x + widget.width &&
                               (mouseY + scrollOffset) >= widget.y && (mouseY + scrollOffset) <= widget.y + widget.height;
            
            if (isHovered) {
                hoveredWidgetIndex = i;
            }
            
            drawCustomWidget(context, widget, isHovered, mouseX, mouseY + scrollOffset, delta);
        }
        
        if (sectionExpanded[4]) {
            CustomWidget thresholdWidget = null;
            for (CustomWidget w : widgets) {
                if (w.label.equals("Threshold") && w.sectionIndex == 4) {
                    thresholdWidget = w;
                    break;
                }
            }
            if (thresholdWidget != null) {
                context.drawTextWithShadow(this.textRenderer, Text.literal("Threshold: "), 
                    thresholdWidget.x - 105, thresholdWidget.y + 6, TEXT_SECONDARY);
            }
        }
        
        context.getMatrices().pop();
        context.disableScissor();
        
        int maxScroll = calculateMaxScroll();
        if (maxScroll > 0) {
            drawScrollBar(context, panelX + panelWidth - 8, panelY, 6, panelHeight, scrollOffset, maxScroll);
        }
        
        int bottomY = this.height - 85;
        int buttonWidth = 150;
        int buttonSpacing = 20;
        int totalWidth = buttonWidth * 2 + buttonSpacing;
        int startX = this.width / 2 - totalWidth / 2;
        
        drawCustomButton(context, startX, bottomY, buttonWidth, 25, "✓ Save & Close", 
            mouseX >= startX && mouseX <= startX + buttonWidth && mouseY >= bottomY && mouseY <= bottomY + 25);
        drawCustomButton(context, startX + buttonWidth + buttonSpacing, bottomY, buttonWidth, 25, "✗ Cancel",
            mouseX >= startX + buttonWidth + buttonSpacing && mouseX <= startX + buttonWidth + buttonSpacing + buttonWidth && 
            mouseY >= bottomY && mouseY <= bottomY + 25);
        
        int editModeButtonY = this.height - 45;
        int editModeButtonX = this.width / 2 - 100;
        CustomWidget editModeButton = null;
        for (CustomWidget w : widgets) {
            if (w.sectionIndex == -1 && w.data instanceof String && w.data.equals("EDIT_MODE")) {
                editModeButton = w;
                break;
            }
        }
        if (editModeButton != null) {
            editModeButton.x = editModeButtonX;
            editModeButton.y = editModeButtonY;
            boolean isHovered = mouseX >= editModeButtonX && mouseX <= editModeButtonX + 200 &&
                               mouseY >= editModeButtonY && mouseY <= editModeButtonY + 30;
            drawCustomWidget(context, editModeButton, isHovered, mouseX, mouseY, delta);
        }
    }
    
    private int calculateMaxScroll() {
        int totalHeight = 0;
        for (SectionHeader header : sectionHeaders) {
            totalHeight += 30;
            if (sectionExpanded[header.index]) {
                totalHeight += header.widgetCount * 32 + 15;
            }
            totalHeight += 10;
        }
        int panelHeight = this.height - 60 - 100;
        return Math.max(0, totalHeight - panelHeight);
    }
    
    private void drawScrollBar(DrawContext context, int x, int y, int width, int height, int scroll, int maxScroll) {
        context.fill(x, y, x + width, y + height, (0xAA << 24) | 0x001a1a2a);
        
        if (maxScroll > 0) {
            int thumbHeight = Math.max(30, (int) ((float) height / (height + maxScroll) * height));
            int thumbY = y + (int) ((float) scroll / maxScroll * (height - thumbHeight));
            context.fill(x + 1, thumbY, x + width - 1, thumbY + thumbHeight, PANEL_BORDER);
            context.fill(x + 2, thumbY + 1, x + width - 2, thumbY + thumbHeight - 1, (0xDD << 24) | 0x003a4a6a);
        }
    }
    
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }
    
    private void drawSectionHeader(DrawContext context, SectionHeader header, int mouseX, int mouseY) {
        float headerPulse = (float) (Math.sin((System.currentTimeMillis() + header.index * 500) / 800.0) * 0.1f + 0.9f);
        int pulseColor = blendColor(header.accentColor, GLOW_COLOR, headerPulse * 0.3f);
        
        boolean isHovered = mouseX >= header.x - 25 && mouseX <= header.x + 380 && 
                           mouseY >= header.y && mouseY <= header.y + 25;
        
        int headerBg = isHovered ? (0x60 << 24) | 0x00333333 : (0x40 << 24) | 0x00222222;
        context.fill(header.x - 25, header.y - 2, header.x + 380, header.y + 27, headerBg);
        
        int underlineColor = header.accentColor;
        context.fill(header.x - 25, header.y + 23, header.x + 380, header.y + 25, underlineColor);
        
        int arrowX = header.x + 365;
        int arrowY = header.y + 7;
        String arrow = header.expanded ? "▼" : "▶";
        int arrowColor = isHovered ? GLOW_COLOR : pulseColor;
        context.drawTextWithShadow(this.textRenderer, Text.literal(arrow), arrowX, arrowY, arrowColor);
        
        context.getMatrices().push();
        context.getMatrices().translate(header.x - 3, header.y - 1, 0);
        context.getMatrices().scale(1.2f, 1.2f, 1.0f);
        context.drawItem(header.icon, 0, 0);
        context.getMatrices().pop();
        
        context.drawTextWithShadow(this.textRenderer, 
            Text.literal(header.text).setStyle(net.minecraft.text.Style.EMPTY.withBold(true).withColor(pulseColor)),
            header.x + 22, header.y + 5, pulseColor);
    }
    
    private void drawCustomWidget(DrawContext context, CustomWidget widget, boolean isHovered, int mouseX, int mouseY, float delta) {
        int sectionColor = getSectionColor(widget.sectionIndex);
        
        int widgetBg = isHovered ? WIDGET_BG : BUTTON_BG;
        context.fill(widget.x - 25, widget.y, widget.x + widget.width, widget.y + widget.height, widgetBg);
        
        if (isHovered) {
            int hoverColor = (0x30 << 24) | (sectionColor & 0x00FFFFFF);
            context.fill(widget.x - 25, widget.y, widget.x + widget.width, widget.y + widget.height, hoverColor);
        }
        
        context.fill(widget.x - 25, widget.y, widget.x - 5, widget.y + widget.height, 
            isHovered ? (0x40 << 24) | 0x00333333 : (0x20 << 24) | 0x00222222);
        
        context.getMatrices().push();
        float iconScale = isHovered ? 1.15f : 1.0f;
        float iconOffset = (1.0f - iconScale) * 8.0f;
        context.getMatrices().translate(widget.x - 20 + iconOffset, widget.y + 2 + iconOffset, 0);
        context.getMatrices().scale(iconScale, iconScale, 1.0f);
        context.drawItem(widget.icon, 0, 0);
        context.getMatrices().pop();
        
        switch (widget.type) {
            case CHECKBOX:
                drawCustomCheckbox(context, widget, isHovered);
                break;
            case COLOR_BUTTON:
                drawCustomColorButton(context, widget, isHovered);
                break;
            case SLIDER:
                drawCustomSlider(context, widget, isHovered, mouseX);
                break;
            case TEXT_FIELD:
                drawCustomTextField(context, widget, isHovered);
                break;
            case BUTTON:
                drawCustomButton(context, widget.x, widget.y, widget.width, widget.height, widget.textValue, isHovered);
                break;
        }
    }
    
    private void drawCustomCheckbox(DrawContext context, CustomWidget widget, boolean isHovered) {
        int checkboxX = widget.x;
        int checkboxY = widget.y + 3;
        int checkboxSize = 16;
        
        int borderColor = PANEL_BORDER;
        context.fill(checkboxX, checkboxY, checkboxX + checkboxSize, checkboxY + 1, borderColor);
        context.fill(checkboxX, checkboxY + checkboxSize - 1, checkboxX + checkboxSize, checkboxY + checkboxSize, borderColor);
        context.fill(checkboxX, checkboxY, checkboxX + 1, checkboxY + checkboxSize, borderColor);
        context.fill(checkboxX + checkboxSize - 1, checkboxY, checkboxX + checkboxSize, checkboxY + checkboxSize, borderColor);
        
        int bgColor = widget.checked ? CHECKBOX_CHECKED : WIDGET_BG;
        context.fill(checkboxX + 1, checkboxY + 1, checkboxX + checkboxSize - 1, checkboxY + checkboxSize - 1, bgColor);
        
        if (widget.checked) {
            int checkColor = 0xFFFFFFFF;
            for (int i = 0; i < 3; i++) {
                context.fill(checkboxX + 3 + i, checkboxY + 7 + i, checkboxX + 4 + i, checkboxY + 8 + i, checkColor);
            }
            for (int i = 0; i < 5; i++) {
                context.fill(checkboxX + 7 + i, checkboxY + 11 - i, checkboxX + 8 + i, checkboxY + 12 - i, checkColor);
            }
        }
        
        int labelX = checkboxX + checkboxSize + 12;
        int labelY = widget.y + 6;
        context.drawTextWithShadow(this.textRenderer, Text.literal(widget.label), 
            labelX, labelY, TEXT_PRIMARY);
    }
    
    private void drawCustomColorButton(DrawContext context, CustomWidget widget, boolean isHovered) {
        int buttonX = widget.x;
        int buttonY = widget.y;
        
        int bgColor = isHovered ? BUTTON_HOVER : WIDGET_BG;
        context.fill(buttonX, buttonY, buttonX + widget.width, buttonY + widget.height, bgColor);
        
        String labelText = widget.label;
        int labelWidth = this.textRenderer.getWidth(labelText + ": ");
        context.drawTextWithShadow(this.textRenderer, Text.literal(labelText + ": "), 
            buttonX + 8, buttonY + 6, TEXT_SECONDARY);
        
        String colorText = widget.selectedColor != null ? widget.selectedColor.getDisplayName() : "Unknown";
        int textColor = widget.selectedColor != null ? widget.selectedColor.getColor() : TEXT_PRIMARY;
        int colorX = buttonX + labelWidth + 8;
        context.drawTextWithShadow(this.textRenderer, Text.literal(colorText), 
            colorX, buttonY + 6, textColor);
        
        if (widget.selectedColor != null) {
            int colorBoxX = colorX + this.textRenderer.getWidth(colorText) + 8;
            int colorBoxY = buttonY + 4;
            int colorBoxSize = 14;
            int previewColor = widget.selectedColor.getColor();
            context.fill(colorBoxX, colorBoxY, colorBoxX + colorBoxSize, colorBoxY + colorBoxSize, previewColor);
            context.fill(colorBoxX, colorBoxY, colorBoxX + colorBoxSize, colorBoxY + 1, PANEL_BORDER);
            context.fill(colorBoxX, colorBoxY + colorBoxSize - 1, colorBoxX + colorBoxSize, colorBoxY + colorBoxSize, PANEL_BORDER);
            context.fill(colorBoxX, colorBoxY, colorBoxX + 1, colorBoxY + colorBoxSize, PANEL_BORDER);
            context.fill(colorBoxX + colorBoxSize - 1, colorBoxY, colorBoxX + colorBoxSize, colorBoxY + colorBoxSize, PANEL_BORDER);
        }
    }
    
    private void drawCustomSlider(DrawContext context, CustomWidget widget, boolean isHovered, int mouseX) {
        int sliderX = widget.x;
        int sliderY = widget.y;
        int sliderWidth = widget.width - 80;
        int sliderHeight = widget.height;
        int trackHeight = 5;
        int trackY = sliderY + (sliderHeight - trackHeight) / 2;
        
        String labelText = widget.label + ":";
        context.drawTextWithShadow(this.textRenderer, Text.literal(labelText), 
            sliderX + 8, sliderY + 6, TEXT_SECONDARY);
        
        int trackStartX = sliderX + this.textRenderer.getWidth(labelText) + 12;
        context.fill(trackStartX, trackY, trackStartX + sliderWidth, trackY + trackHeight, WIDGET_BG);
        context.fill(trackStartX, trackY, trackStartX + sliderWidth, trackY + 1, PANEL_BORDER);
        context.fill(trackStartX, trackY + trackHeight - 1, trackStartX + sliderWidth, trackY + trackHeight, PANEL_BORDER);
        
        float normalizedValue = (widget.value - widget.minValue) / (widget.maxValue - widget.minValue);
        int fillWidth = (int) (sliderWidth * normalizedValue);
        int fillColor = getSectionColor(widget.sectionIndex);
        if (fillWidth > 0) {
            context.fill(trackStartX, trackY, trackStartX + fillWidth, trackY + trackHeight, fillColor);
        }
        
        int handleX = trackStartX + fillWidth - 5;
        int handleY = trackY - 3;
        int handleSize = 11;
        int handleColor = isHovered ? GLOW_COLOR : fillColor;
        context.fill(handleX, handleY, handleX + handleSize, handleY + handleSize, PANEL_BORDER);
        context.fill(handleX + 1, handleY + 1, handleX + handleSize - 1, handleY + handleSize - 1, handleColor);
        
        String valueText = String.format("%.2f", widget.value);
        int valueX = trackStartX + sliderWidth + 12;
        context.drawTextWithShadow(this.textRenderer, Text.literal(valueText), 
            valueX, sliderY + 6, TEXT_PRIMARY);
    }
    
    private void drawCustomTextField(DrawContext context, CustomWidget widget, boolean isHovered) {
        int fieldX = widget.x;
        int fieldY = widget.y;
        
        int bgColor = isHovered || editingTextField == widget ? BUTTON_HOVER : WIDGET_BG;
        context.fill(fieldX, fieldY, fieldX + widget.width, fieldY + widget.height, bgColor);
        
        int borderColor = editingTextField == widget ? GLOW_COLOR : PANEL_BORDER;
        context.fill(fieldX, fieldY, fieldX + widget.width, fieldY + 1, borderColor);
        context.fill(fieldX, fieldY + widget.height - 1, fieldX + widget.width, fieldY + widget.height, borderColor);
        context.fill(fieldX, fieldY, fieldX + 1, fieldY + widget.height, borderColor);
        context.fill(fieldX + widget.width - 1, fieldY, fieldX + widget.width, fieldY + widget.height, borderColor);
        
        String text = editingTextField == widget ? textInputBuffer.toString() : (widget.textValue != null ? widget.textValue : "");
        
        if (editingTextField == widget) {
            long time = System.currentTimeMillis();
            boolean showCursor = (time / 500) % 2 == 0;
            if (showCursor) {
                text += "|";
            }
        }
        
        context.drawTextWithShadow(this.textRenderer, Text.literal(text), 
            fieldX + 6, fieldY + 6, TEXT_PRIMARY);
    }
    
    private void drawCustomButton(DrawContext context, int x, int y, int width, int height, String text, boolean isHovered) {
        int bgColor = isHovered ? BUTTON_HOVER : WIDGET_BG;
        context.fill(x, y, x + width, y + height, bgColor);
        
        int borderColor = PANEL_BORDER;
        int borderWidth = 2;
        context.fill(x, y, x + width, y + borderWidth, borderColor);
        context.fill(x, y + height - borderWidth, x + width, y + height, borderColor);
        context.fill(x, y, x + borderWidth, y + height, borderColor);
        context.fill(x + width - borderWidth, y, x + width, y + height, borderColor);
        
        if (isHovered) {
            int glowColor = (0x40 << 24) | (GLOW_COLOR & 0x00FFFFFF);
            context.fill(x + borderWidth, y + borderWidth, x + width - borderWidth, y + height - borderWidth, glowColor);
        }
        
        if (text != null && !text.isEmpty()) {
            int textX = x + (width - this.textRenderer.getWidth(text)) / 2;
            int textY = y + (height - this.textRenderer.fontHeight) / 2;
            context.drawTextWithShadow(this.textRenderer, Text.literal(text), textX, textY, TEXT_PRIMARY);
        }
    }
    
    private int getSectionColor(int sectionIndex) {
        switch (sectionIndex) {
            case 0: return ACCENT_COLOR_GENERAL;
            case 1: return ACCENT_COLOR_DISPLAY;
            case 2: return ACCENT_COLOR_SOUND;
            case 3: return ACCENT_COLOR_INVENTORY;
            case 4: return ACCENT_COLOR_MILESTONE;
            case 5: return 0xFF8b5cf6;
            default: return ACCENT_COLOR_GENERAL;
        }
    }
    
    private int blendColor(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int bottomY = this.height - 85;
            int buttonWidth = 150;
            int buttonSpacing = 20;
            int totalWidth = buttonWidth * 2 + buttonSpacing;
            int startX = this.width / 2 - totalWidth / 2;
            
            if (mouseX >= startX && mouseX <= startX + buttonWidth &&
                mouseY >= bottomY && mouseY <= bottomY + 25) {
                saveConfig();
                this.close();
                return true;
            }
            
            if (mouseX >= startX + buttonWidth + buttonSpacing && 
                mouseX <= startX + buttonWidth + buttonSpacing + buttonWidth &&
                mouseY >= bottomY && mouseY <= bottomY + 25) {
                this.close();
                return true;
            }
            
            int editModeButtonY = this.height - 45;
            int editModeButtonX = this.width / 2 - 100;
            if (mouseX >= editModeButtonX && mouseX <= editModeButtonX + 200 &&
                mouseY >= editModeButtonY && mouseY <= editModeButtonY + 30) {
                this.client.setScreen(new com.m4ssive.totemcounterv2.gui.EditModeScreen());
                return true;
            }
            
            for (SectionHeader header : sectionHeaders) {
                if (mouseX >= header.x - 25 && mouseX <= header.x + 380 && 
                    (mouseY + scrollOffset) >= header.y && (mouseY + scrollOffset) <= header.y + 25) {
                    sectionExpanded[header.index] = !sectionExpanded[header.index];
                    header.expanded = sectionExpanded[header.index];
                    this.init();
                    return true;
                }
            }
            
            for (int i = 0; i < widgets.size(); i++) {
                CustomWidget widget = widgets.get(i);
                
                if (widget.sectionIndex == -1 && widget.data instanceof String && 
                    widget.data.equals("EDIT_MODE")) {
                    continue;
                }
                
                if (widget.sectionIndex >= 0 && !sectionExpanded[widget.sectionIndex]) {
                    continue;
                }
                
                int checkY = (int) (mouseY + scrollOffset);
                if (mouseX >= widget.x && mouseX <= widget.x + widget.width &&
                    checkY >= widget.y && checkY <= widget.y + widget.height) {
                    
                    selectedWidgetIndex = i;
                    
                    switch (widget.type) {
                        case CHECKBOX:
                            widget.checked = !widget.checked;
                            updateCheckboxConfig(widget.label, widget.checked);
                            saveConfig();
                            return true;
                        case COLOR_BUTTON:
                            if (widget.colorOptions != null && widget.colorOptions.length > 0) {
                                int currentIndex = 0;
                                for (int j = 0; j < widget.colorOptions.length; j++) {
                                    if (widget.colorOptions[j] == widget.selectedColor) {
                                        currentIndex = j;
                                        break;
                                    }
                                }
                                int nextIndex = (currentIndex + 1) % widget.colorOptions.length;
                                widget.selectedColor = widget.colorOptions[nextIndex];
                                updateColorConfig(widget.label, widget.selectedColor);
                                saveConfig();
                            }
                            return true;
                        case SLIDER:
                            float normalizedValue = (float) ((mouseX - widget.x) / widget.width);
                            normalizedValue = Math.max(0.0f, Math.min(1.0f, normalizedValue));
                            widget.value = widget.minValue + normalizedValue * (widget.maxValue - widget.minValue);
                            updateSliderConfig(widget.label, widget.value);
                            saveConfig();
                            return true;
                        case TEXT_FIELD:
                            editingTextField = widget;
                            textInputBuffer = new StringBuilder(widget.textValue != null ? widget.textValue : "");
                            return true;
                        case BUTTON:
                            if (widget.data instanceof SoundManager.SoundType[]) {
                                SoundManager.SoundType[] soundTypes = (SoundManager.SoundType[]) widget.data;
                                int currentIndex = config.soundType;
                                int nextIndex = (currentIndex + 1) % soundTypes.length;
                                config.soundType = nextIndex;
                                widget.textValue = soundTypes[nextIndex].getDisplayName();
                                saveConfig();
                            }
                            return true;
                    }
                }
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = calculateMaxScroll();
        if (maxScroll > 0) {
            scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * SCROLL_SPEED));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && selectedWidgetIndex >= 0 && selectedWidgetIndex < widgets.size()) {
            CustomWidget widget = widgets.get(selectedWidgetIndex);
            if (widget.type == WidgetType.SLIDER) {
                float normalizedValue = (float) ((mouseX - widget.x) / widget.width);
                normalizedValue = Math.max(0.0f, Math.min(1.0f, normalizedValue));
                widget.value = widget.minValue + normalizedValue * (widget.maxValue - widget.minValue);
                updateSliderConfig(widget.label, widget.value);
                saveConfig();
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        selectedWidgetIndex = -1;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editingTextField != null) {
            if (keyCode == 257) {
                editingTextField.textValue = textInputBuffer.toString();
                try {
                    int intValue = Integer.parseInt(textInputBuffer.toString());
                    if (intValue > 0) {
                        config.milestoneThreshold = intValue;
                        saveConfig();
                    }
                } catch (NumberFormatException ignored) {}
                editingTextField = null;
                return true;
            } else if (keyCode == 256) {
                editingTextField = null;
                return true;
            } else if (keyCode == 259) {
                if (textInputBuffer.length() > 0) {
                    textInputBuffer.deleteCharAt(textInputBuffer.length() - 1);
                }
                return true;
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (editingTextField != null && Character.isDigit(chr)) {
            textInputBuffer.append(chr);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
