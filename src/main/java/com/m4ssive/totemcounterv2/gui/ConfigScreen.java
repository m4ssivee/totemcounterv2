package com.m4ssive.totemcounterv2.gui;

import com.m4ssive.totemcounterv2.TotemCounterV2Mod;
import com.m4ssive.totemcounterv2.config.ModConfig;
import com.m4ssive.totemcounterv2.config.PresetColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

/**
 * Simplified config screen - NO X/Y/Scale fields
 * Use Edit Mode for positioning and sizing
 */
public class ConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;

    // Color buttons
    private CyclingButtonWidget<PresetColors> textColorButton;
    private CyclingButtonWidget<PresetColors> bgColorButton;
    private CyclingButtonWidget<PresetColors> borderColorButton;
    
    // Checkboxes
    private CheckboxWidget showBackgroundCheckbox;
    private CheckboxWidget showBorderCheckbox;
    private CheckboxWidget showSelfCheckbox;
    private CheckboxWidget showOnlyNearbyCheckbox;
    private CheckboxWidget showIconCheckbox;
    
    // Text fields
    private TextFieldWidget nearbyDistanceField;
    private TextFieldWidget maxPlayersField;
    
    // Track selected colors
    private PresetColors selectedTextColor;
    private PresetColors selectedBgColor;
    private PresetColors selectedBorderColor;

    public ConfigScreen(Screen parent, ModConfig config) {
        super(Text.literal("§6§lTotemCounterV2 §r- Settings"));
        this.parent = parent;
        this.config = config;
        
        // Initialize with current colors
        this.selectedTextColor = PresetColors.fromColor(config.textColor);
        this.selectedBgColor = PresetColors.fromColor(config.backgroundColor);
        this.selectedBorderColor = PresetColors.fromColor(config.borderColor);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        int spacing = 26;
        int currentY = startY;

        // === COLORS SECTION ===
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§l§nCOLORS"),
            button -> {}
        ).dimensions(centerX - 160, currentY, 320, 20).build());
        
        currentY += spacing;

        // Text Color
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§bText:"),
            button -> {}
        ).dimensions(centerX - 160, currentY, 70, 20).build());

        textColorButton = CyclingButtonWidget.builder((PresetColors color) -> {
            return Text.literal(color.getDisplayName());
        })
        .values(PresetColors.getTextColors())
        .initially(selectedTextColor)
        .build(centerX - 85, currentY, 245, 20, Text.literal("Text Color"), (button, value) -> {
            selectedTextColor = value;
        });
        this.addDrawableChild(textColorButton);

        currentY += spacing;

        // Background Color
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§9Background:"),
            button -> {}
        ).dimensions(centerX - 160, currentY, 70, 20).build());

        bgColorButton = CyclingButtonWidget.builder((PresetColors color) -> {
            return Text.literal(color.getDisplayName());
        })
        .values(PresetColors.getBackgroundColors())
        .initially(selectedBgColor)
        .build(centerX - 85, currentY, 245, 20, Text.literal("BG Color"), (button, value) -> {
            selectedBgColor = value;
        });
        this.addDrawableChild(bgColorButton);

        currentY += spacing;

        // Border Color
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§dBorder:"),
            button -> {}
        ).dimensions(centerX - 160, currentY, 70, 20).build());

        borderColorButton = CyclingButtonWidget.builder((PresetColors color) -> {
            return Text.literal(color.getDisplayName());
        })
        .values(PresetColors.getBorderColors())
        .initially(selectedBorderColor)
        .build(centerX - 85, currentY, 245, 20, Text.literal("Border Color"), (button, value) -> {
            selectedBorderColor = value;
        });
        this.addDrawableChild(borderColorButton);

        currentY += spacing + 10;

        // === DISPLAY OPTIONS ===
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§l§nDISPLAY OPTIONS"),
            button -> {}
        ).dimensions(centerX - 160, currentY, 320, 20).build());
        
        currentY += spacing;

        // Row 1: Background, Border, Icon
        showBackgroundCheckbox = CheckboxWidget.builder(Text.literal("Background"), this.textRenderer)
            .pos(centerX - 160, currentY)
            .checked(config.showBackground)
            .build();
        this.addDrawableChild(showBackgroundCheckbox);

        showBorderCheckbox = CheckboxWidget.builder(Text.literal("Border"), this.textRenderer)
            .pos(centerX - 40, currentY)
            .checked(config.showBorder)
            .build();
        this.addDrawableChild(showBorderCheckbox);

        showIconCheckbox = CheckboxWidget.builder(Text.literal("Icon"), this.textRenderer)
            .pos(centerX + 80, currentY)
            .checked(config.showIcon)
            .build();
        this.addDrawableChild(showIconCheckbox);

        currentY += spacing;

        // Row 2: Show Self, Only Nearby
        showSelfCheckbox = CheckboxWidget.builder(Text.literal("Show Self"), this.textRenderer)
            .pos(centerX - 160, currentY)
            .checked(config.showSelf)
            .build();
        this.addDrawableChild(showSelfCheckbox);

        showOnlyNearbyCheckbox = CheckboxWidget.builder(Text.literal("Only Nearby"), this.textRenderer)
            .pos(centerX - 10, currentY)
            .checked(config.showOnlyNearby)
            .build();
        this.addDrawableChild(showOnlyNearbyCheckbox);

        currentY += spacing;

        // Nearby Distance
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§eNearby Range:"),
            button -> {}
        ).dimensions(centerX - 160, currentY, 100, 20).build());

        nearbyDistanceField = new TextFieldWidget(this.textRenderer, centerX - 50, currentY, 60, 20, Text.literal("Distance"));
        nearbyDistanceField.setText(String.valueOf(config.nearbyDistance));
        this.addDrawableChild(nearbyDistanceField);

        // Max Players
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§6Max Players:"),
            button -> {}
        ).dimensions(centerX + 20, currentY, 90, 20).build());

        maxPlayersField = new TextFieldWidget(this.textRenderer, centerX + 120, currentY, 60, 20, Text.literal("Max"));
        maxPlayersField.setText(String.valueOf(config.maxPlayersShown));
        this.addDrawableChild(maxPlayersField);

        currentY += spacing + 15;

        // === ACTION BUTTONS ===
        // Edit Mode button (BIG and prominent)
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§e§l✎ EDIT MODE §r§7(Position & Size)"),
            button -> {
                saveConfig();
                this.close();
                TotemCounterV2Mod.getInstance().getTotemHud().setEditMode(true);
                if (this.client != null) {
                    this.client.setScreen(new EditModeScreen());
                }
            }
        ).dimensions(centerX - 160, currentY, 320, 25).build());

        currentY += 30;

        // Save and Cancel
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§a✔ Save & Close"),
            button -> {
                saveConfig();
                this.close();
            }
        ).dimensions(centerX - 160, currentY, 155, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            ScreenTexts.CANCEL,
            button -> this.close()
        ).dimensions(centerX + 5, currentY, 155, 20).build());
    }

    private void saveConfig() {
        try {
            // Save colors
            config.textColor = selectedTextColor.getColor();
            config.backgroundColor = selectedBgColor.getColor();
            config.borderColor = selectedBorderColor.getColor();
            
            // Save display options
            config.showBackground = showBackgroundCheckbox.isChecked();
            config.showBorder = showBorderCheckbox.isChecked();
            config.showIcon = showIconCheckbox.isChecked();
            config.showSelf = showSelfCheckbox.isChecked();
            config.showOnlyNearby = showOnlyNearbyCheckbox.isChecked();
            config.nearbyDistance = Integer.parseInt(nearbyDistanceField.getText());
            config.maxPlayersShown = Integer.parseInt(maxPlayersField.getText());
            
            config.save();
            
            TotemCounterV2Mod.LOGGER.info("Config saved successfully!");
        } catch (NumberFormatException e) {
            TotemCounterV2Mod.LOGGER.error("Invalid number format in config");
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFD700);
        
        // Instructions at bottom
        int instructY = this.height - 60;
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§7For Position & Size: Click §e'EDIT MODE' §7button above", 
            this.width / 2, instructY, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§7Or press §eŞ §7key anytime in-game", 
            this.width / 2, instructY + 12, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, 
            "§8Made by §6m4ssive §8| github.com/m4ssivee", 
            this.width / 2, instructY + 28, 0x666666);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
