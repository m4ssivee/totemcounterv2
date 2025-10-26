package com.m4ssive.totemcounterv2.gui;

import com.m4ssive.totemcounterv2.TotemTracker;
import com.m4ssive.totemcounterv2.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

/**
 * HUD overlay with full edit mode support
 * - Drag to move
 * - Resize from corners
 * - Config integration
 */
public class TotemHud {
    private final TotemTracker tracker;
    private final ModConfig config;
    private static final ItemStack TOTEM_STACK = new ItemStack(Items.TOTEM_OF_UNDYING);
    
    // Edit mode
    private boolean editMode = false;
    
    // Mouse interaction
    private boolean isDragging = false;
    private boolean isResizing = false;
    private ResizeCorner activeCorner = ResizeCorner.NONE;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private int resizeStartX = 0;
    private int resizeStartY = 0;
    private float resizeStartScale = 1.0f;
    
    // HUD bounds for interaction
    private int lastHudX, lastHudY, lastHudWidth, lastHudHeight;
    
    // Corner handle size
    private static final int HANDLE_SIZE = 8;
    
    private enum ResizeCorner {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public TotemHud(TotemTracker tracker, ModConfig config) {
        this.tracker = tracker;
        this.config = config;
    }
    
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (!editMode) {
            // Reset states when exiting edit mode
            isDragging = false;
            isResizing = false;
            activeCorner = ResizeCorner.NONE;
        }
    }

    public void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        // Get players to show
        Map<UUID, Integer> allPops = tracker.getAllPops();
        
        // In edit mode, show sample data if no players
        List<PlayerInfo> playersToShow = new ArrayList<>();
        
        if (editMode && allPops.isEmpty()) {
            // Sample data for editing
            playersToShow.add(new PlayerInfo("Player1", 5));
            playersToShow.add(new PlayerInfo("Player2", 3));
            playersToShow.add(new PlayerInfo("Player3", 1));
        } else if (!allPops.isEmpty()) {
            // Real data
            for (Map.Entry<UUID, Integer> entry : allPops.entrySet()) {
                PlayerEntity player = client.world.getPlayerByUuid(entry.getKey());
                if (player == null) continue;

                // Show self option
                if (!config.showSelf && player.getUuid().equals(client.player.getUuid())) {
                    continue;
                }

                // Distance check
                if (config.showOnlyNearby) {
                    double distance = client.player.squaredDistanceTo(player);
                    if (distance > config.nearbyDistance * config.nearbyDistance) {
                        continue;
                    }
                }

                playersToShow.add(new PlayerInfo(player.getGameProfile().getName(), entry.getValue()));
            }

            // Sort by count (descending)
            playersToShow.sort((a, b) -> Integer.compare(b.totemCount, a.totemCount));

            // Limit max players
            if (playersToShow.size() > config.maxPlayersShown) {
                playersToShow = playersToShow.subList(0, config.maxPlayersShown);
            }
        }

        if (playersToShow.isEmpty() && !editMode) {
            return;
        }

        // Render HUD
        renderHud(context, client, playersToShow);
        
        // Render edit mode overlay
        if (editMode) {
            renderEditModeOverlay(context, client);
        }
    }

    private void renderHud(DrawContext context, MinecraftClient client, List<PlayerInfo> players) {
        TextRenderer textRenderer = client.textRenderer;
        
        context.getMatrices().push();
        context.getMatrices().scale(config.scale, config.scale, 1.0f);

        int scaledX = (int) (config.hudX / config.scale);
        int scaledY = (int) (config.hudY / config.scale);

        // Icon size
        int iconSize = config.showIcon ? 16 : 0;
        int iconPadding = config.showIcon ? 4 : 0;

        // Calculate max width
        int maxWidth = 0;
        List<String> lines = new ArrayList<>();
        
        if (players.isEmpty()) {
            lines.add("No totem pops");
            maxWidth = textRenderer.getWidth("No totem pops");
        } else {
            for (PlayerInfo player : players) {
                String line = config.displayFormat
                    .replace("{player}", player.name)
                    .replace("{count}", String.valueOf(player.totemCount));
                lines.add(line);
                int width = textRenderer.getWidth(line) + iconSize + iconPadding;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }

        int lineHeight = Math.max(textRenderer.fontHeight, iconSize);
        int totalHeight = lines.size() * (lineHeight + 2) + config.padding * 2;
        int totalWidth = maxWidth + config.padding * 2;
        
        // Store bounds for mouse interaction
        lastHudX = config.hudX;
        lastHudY = config.hudY;
        lastHudWidth = (int) (totalWidth * config.scale);
        lastHudHeight = (int) (totalHeight * config.scale);

        // Background
        if (config.showBackground) {
            context.fill(scaledX, scaledY, scaledX + totalWidth, scaledY + totalHeight, config.backgroundColor);
        }

        // Border
        if (config.showBorder) {
            context.fill(scaledX, scaledY, scaledX + totalWidth, scaledY + 1, config.borderColor);
            context.fill(scaledX, scaledY + totalHeight - 1, scaledX + totalWidth, scaledY + totalHeight, config.borderColor);
            context.fill(scaledX, scaledY, scaledX + 1, scaledY + totalHeight, config.borderColor);
            context.fill(scaledX + totalWidth - 1, scaledY, scaledX + totalWidth, scaledY + totalHeight, config.borderColor);
        }

        // Draw texts and icons
        int yOffset = scaledY + config.padding;
        for (String line : lines) {
            int xOffset = scaledX + config.padding;
            
            // Draw totem icon
            if (config.showIcon && !players.isEmpty()) {
                context.drawItem(TOTEM_STACK, xOffset, yOffset);
                xOffset += iconSize + iconPadding;
            }
            
            // Draw text
            int textY = yOffset + (iconSize - textRenderer.fontHeight) / 2;
            context.drawText(textRenderer, line, xOffset, textY, config.textColor, false);
            yOffset += lineHeight + 2;
        }

        context.getMatrices().pop();
    }
    
    private void renderEditModeOverlay(DrawContext context, MinecraftClient client) {
        TextRenderer textRenderer = client.textRenderer;
        
        // Draw corner handles
        drawCornerHandle(context, lastHudX, lastHudY, 0xFFFFFF00); // Top-left
        drawCornerHandle(context, lastHudX + lastHudWidth - HANDLE_SIZE, lastHudY, 0xFFFFFF00); // Top-right
        drawCornerHandle(context, lastHudX, lastHudY + lastHudHeight - HANDLE_SIZE, 0xFFFFFF00); // Bottom-left
        drawCornerHandle(context, lastHudX + lastHudWidth - HANDLE_SIZE, lastHudY + lastHudHeight - HANDLE_SIZE, 0xFFFFFF00); // Bottom-right
        
        // Draw edit mode info
        String info = isDragging ? "§eDragging..." : isResizing ? "§bResizing... (Scale: " + String.format("%.1f", config.scale) + ")" : "§aEdit Mode ON";
        context.drawTextWithShadow(textRenderer, info, 5, 5, 0xFFFFFF);
        
        context.drawTextWithShadow(textRenderer, "§7Drag HUD to move", 5, 17, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "§7Drag corners to resize", 5, 27, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "§7Press §eE §7to exit", 5, 37, 0xAAAAAA);
    }
    
    private void drawCornerHandle(DrawContext context, int x, int y, int color) {
        // Outer border (white)
        context.fill(x, y, x + HANDLE_SIZE, y + HANDLE_SIZE, 0xFFFFFFFF);
        // Inner fill (yellow)
        context.fill(x + 1, y + 1, x + HANDLE_SIZE - 1, y + HANDLE_SIZE - 1, color);
    }
    
    public boolean handleMouseClick(double mouseX, double mouseY, int button) {
        if (!editMode || button != 0) return false;
        
        // Check corner handles first
        ResizeCorner corner = getCornerAtPosition((int) mouseX, (int) mouseY);
        if (corner != ResizeCorner.NONE) {
            isResizing = true;
            activeCorner = corner;
            resizeStartX = (int) mouseX;
            resizeStartY = (int) mouseY;
            resizeStartScale = config.scale;
            return true;
        }
        
        // Check main HUD area
        if (isMouseOverHud(mouseX, mouseY)) {
            isDragging = true;
            dragStartX = (int) mouseX - config.hudX;
            dragStartY = (int) mouseY - config.hudY;
            return true;
        }
        
        return false;
    }
    
    public boolean handleMouseRelease(double mouseX, double mouseY, int button) {
        if (!editMode || button != 0) return false;
        
        boolean wasInteracting = isDragging || isResizing;
        
        if (wasInteracting) {
            config.save();
        }
        
        isDragging = false;
        isResizing = false;
        activeCorner = ResizeCorner.NONE;
        
        return wasInteracting;
    }
    
    public boolean handleMouseDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!editMode || button != 0) return false;
        
        if (isDragging) {
            config.hudX = (int) mouseX - dragStartX;
            config.hudY = (int) mouseY - dragStartY;
            return true;
        }
        
        if (isResizing && activeCorner != ResizeCorner.NONE) {
            // Calculate scale based on distance from start
            int deltaFromStartX = (int) mouseX - resizeStartX;
            int deltaFromStartY = (int) mouseY - resizeStartY;
            
            // Use the larger delta for uniform scaling
            int delta = Math.max(Math.abs(deltaFromStartX), Math.abs(deltaFromStartY));
            if (deltaFromStartX < 0 || deltaFromStartY < 0) {
                delta = -delta;
            }
            
            float scaleDelta = delta / 100.0f;
            config.scale = Math.max(0.5f, Math.min(3.0f, resizeStartScale + scaleDelta));
            return true;
        }
        
        return false;
    }
    
    private ResizeCorner getCornerAtPosition(int mouseX, int mouseY) {
        // Top-left
        if (mouseX >= lastHudX && mouseX <= lastHudX + HANDLE_SIZE &&
            mouseY >= lastHudY && mouseY <= lastHudY + HANDLE_SIZE) {
            return ResizeCorner.TOP_LEFT;
        }
        
        // Top-right
        if (mouseX >= lastHudX + lastHudWidth - HANDLE_SIZE && mouseX <= lastHudX + lastHudWidth &&
            mouseY >= lastHudY && mouseY <= lastHudY + HANDLE_SIZE) {
            return ResizeCorner.TOP_RIGHT;
        }
        
        // Bottom-left
        if (mouseX >= lastHudX && mouseX <= lastHudX + HANDLE_SIZE &&
            mouseY >= lastHudY + lastHudHeight - HANDLE_SIZE && mouseY <= lastHudY + lastHudHeight) {
            return ResizeCorner.BOTTOM_LEFT;
        }
        
        // Bottom-right
        if (mouseX >= lastHudX + lastHudWidth - HANDLE_SIZE && mouseX <= lastHudX + lastHudWidth &&
            mouseY >= lastHudY + lastHudHeight - HANDLE_SIZE && mouseY <= lastHudY + lastHudHeight) {
            return ResizeCorner.BOTTOM_RIGHT;
        }
        
        return ResizeCorner.NONE;
    }
    
    public boolean isResizing() {
        return isResizing;
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    private boolean isMouseOverHud(double mouseX, double mouseY) {
        return mouseX >= lastHudX && mouseX <= lastHudX + lastHudWidth &&
               mouseY >= lastHudY && mouseY <= lastHudY + lastHudHeight;
    }

    private static class PlayerInfo {
        final String name;
        final int totemCount;

        PlayerInfo(String name, int totemCount) {
            this.name = name;
            this.totemCount = totemCount;
        }
    }
}
