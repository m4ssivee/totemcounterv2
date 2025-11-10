package com.m4ssive.totemcounterv2.gui;

import com.m4ssive.totemcounterv2.TotemTracker;
import com.m4ssive.totemcounterv2.config.ModConfig;
import com.m4ssive.totemcounterv2.nativecode.NativeGUIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

public class TotemHud {
    private final TotemTracker tracker;
    private final ModConfig config;
    private static final ItemStack TOTEM_STACK = new ItemStack(Items.TOTEM_OF_UNDYING);
    
    private boolean editMode = false;
    
    private boolean isDragging = false;
    private boolean isResizing = false;
    private ResizeCorner activeCorner = ResizeCorner.NONE;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private int resizeStartX = 0;
    private int resizeStartY = 0;
    private float resizeStartScale = 1.0f;
    
    private int lastHudX, lastHudY, lastHudWidth, lastHudHeight;
    
    private static final int HANDLE_SIZE = 8;
    
    private final Map<UUID, Float> animationProgress = new HashMap<>();
    private final Map<UUID, Integer> previousCounts = new HashMap<>();
    private long lastUpdateTime = System.currentTimeMillis();
    
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

        if (!config.enableHud && !editMode) {
            return;
        }

        Map<UUID, Integer> allPops = tracker.getAllPops();
        
        List<PlayerInfo> playersToShow = new ArrayList<>();
        
        if (editMode && allPops.isEmpty()) {
            playersToShow.add(new PlayerInfo("Player1", 5, UUID.randomUUID()));
            playersToShow.add(new PlayerInfo("Player2", 3, UUID.randomUUID()));
            playersToShow.add(new PlayerInfo("Player3", 1, UUID.randomUUID()));
        } else if (!allPops.isEmpty()) {
            for (Map.Entry<UUID, Integer> entry : allPops.entrySet()) {
                PlayerEntity player = client.world.getPlayerByUuid(entry.getKey());
                if (player == null) continue;

                if (!config.showSelf && player.getUuid().equals(client.player.getUuid())) {
                    continue;
                }

                if (config.showOnlyNearby) {
                    double distance = client.player.squaredDistanceTo(player);
                    if (distance > config.nearbyDistance * config.nearbyDistance) {
                        continue;
                    }
                }

                playersToShow.add(new PlayerInfo(player.getGameProfile().getName(), entry.getValue(), player.getUuid()));
            }

            playersToShow.sort((a, b) -> Integer.compare(b.totemCount, a.totemCount));

            if (playersToShow.size() > config.maxPlayersShown) {
                playersToShow = playersToShow.subList(0, config.maxPlayersShown);
            }
        }

        if (playersToShow.isEmpty() && !editMode) {
            return;
        }

        renderHud(context, client, playersToShow);
        
        if (editMode) {
            renderEditModeOverlay(context, client);
        }
    }

    private void renderHud(DrawContext context, MinecraftClient client, List<PlayerInfo> players) {
        TextRenderer textRenderer = client.textRenderer;
        
        updateAnimations(players);
        
        int hudX = config.hudX >= 0 ? config.hudX : client.getWindow().getScaledWidth() - 270;
        int hudY = config.hudY >= 0 ? config.hudY : 
            NativeGUIHelper.calculateOptimalYPosition(
                client.getWindow().getScaledWidth(), 
                client.getWindow().getScaledHeight(), 
                players.size()
            );
        
        context.getMatrices().push();
        context.getMatrices().scale(config.scale, config.scale, 1.0f);

        int scaledX = (int) (hudX / config.scale);
        int scaledY = (int) (hudY / config.scale);

        int iconSize = config.showIcon ? 16 : 0;
        int iconPadding = config.showIcon ? 4 : 0;

        int maxWidth = 0;
        
        if (players.isEmpty()) {
            String noPopsText = "No totem pops";
            maxWidth = textRenderer.getWidth(noPopsText) + iconSize + iconPadding;
        } else {
            for (PlayerInfo player : players) {
                String playerName = player.name + " ";
                String countText = String.valueOf(player.totemCount);
                int width = textRenderer.getWidth(playerName) + textRenderer.getWidth(countText) + iconSize + iconPadding + 5;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }

        int lineHeight = Math.max(textRenderer.fontHeight, iconSize);
        int itemSpacing = 2;
        
        int[] dimensions = NativeGUIHelper.calculateLayoutDimensions(
            players.isEmpty() ? 1 : players.size(),
            maxWidth,
            lineHeight,
            itemSpacing,
            config.padding
        );
        
        int totalWidth = dimensions[0];
        int totalHeight = dimensions[1];
        
        lastHudX = hudX;
        lastHudY = hudY;
        lastHudWidth = (int) (totalWidth * config.scale);
        lastHudHeight = (int) (totalHeight * config.scale);

        if (config.showBackground) {
            context.fill(scaledX, scaledY, scaledX + totalWidth, scaledY + totalHeight, config.backgroundColor);
        }

        if (config.showBorder) {
            context.fill(scaledX, scaledY, scaledX + totalWidth, scaledY + 1, config.borderColor);
            context.fill(scaledX, scaledY + totalHeight - 1, scaledX + totalWidth, scaledY + totalHeight, config.borderColor);
            context.fill(scaledX, scaledY, scaledX + 1, scaledY + totalHeight, config.borderColor);
            context.fill(scaledX + totalWidth - 1, scaledY, scaledX + totalWidth, scaledY + totalHeight, config.borderColor);
        }

        int yOffset = scaledY + config.padding;
        
        if (players.isEmpty()) {
            int xOffset = scaledX + config.padding;
            int textY = yOffset + (iconSize - textRenderer.fontHeight) / 2;
            context.drawText(textRenderer, "No totem pops", xOffset, textY, config.textColor, false);
        } else {
            for (PlayerInfo player : players) {
                int xOffset = scaledX + config.padding;
                
                if (config.showIcon) {
                    context.drawItem(TOTEM_STACK, xOffset, yOffset);
                    xOffset += iconSize + iconPadding;
                }
                
                int textY = yOffset + (iconSize - textRenderer.fontHeight) / 2;
                String playerName = player.name + " ";
                context.drawText(textRenderer, playerName, xOffset, textY, config.textColor, false);
                xOffset += textRenderer.getWidth(playerName);
                
                int countColor = getTotemCountColor(player.totemCount);
                String countText = "-" + player.totemCount;
                
                float animProgress = animationProgress.getOrDefault(player.uuid, 1.0f);
                
                float scale = config.countScale;
                if (animProgress < 1.0f) {
                    scale = NativeGUIHelper.calculateScaleAnimation(
                        config.countScale,
                        animProgress,
                        config.countScale * 1.3f,
                        config.countScale * 0.8f
                    );
                }

                if (Math.abs(scale - 1.0f) > 0.01f) {
                    context.getMatrices().push();
                    context.getMatrices().translate(xOffset, textY, 0);
                    context.getMatrices().scale(scale, scale, 1.0f);
                    int sx = 0;
                    int sy = 0;
                    
                    int finalColor = countColor;
                    
                    if (config.countShadow) {
                        context.drawTextWithShadow(textRenderer, countText, sx, sy, finalColor);
                    } else {
                        context.drawText(textRenderer, countText, sx, sy, finalColor, false);
                    }
                    context.getMatrices().pop();
                } else {
                    if (config.countShadow) {
                        context.drawTextWithShadow(textRenderer, countText, xOffset, textY, countColor);
                    } else {
                        context.drawText(textRenderer, countText, xOffset, textY, countColor, false);
                    }
                }
                
                yOffset += lineHeight + 2;
            }
        }

        context.getMatrices().pop();
    }
    
    private void renderEditModeOverlay(DrawContext context, MinecraftClient client) {
        TextRenderer textRenderer = client.textRenderer;
        
        if (lastHudWidth > 0 && lastHudHeight > 0) {
            drawCornerHandle(context, lastHudX, lastHudY, 0xFFFFFF00);
            drawCornerHandle(context, lastHudX + lastHudWidth - HANDLE_SIZE, lastHudY, 0xFFFFFF00);
            drawCornerHandle(context, lastHudX, lastHudY + lastHudHeight - HANDLE_SIZE, 0xFFFFFF00);
            drawCornerHandle(context, lastHudX + lastHudWidth - HANDLE_SIZE, lastHudY + lastHudHeight - HANDLE_SIZE, 0xFFFFFF00);
        }
        
        String status = isDragging ? "§eDragging..." : isResizing ? "§bResizing..." : "§aEdit Mode ON";
        context.drawTextWithShadow(textRenderer, status, 10, 10, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, "§7Drag to move | Drag corners to resize | Press §eK §7to exit", 10, 22, 0xAAAAAA);
    }
    
    private void drawCornerHandle(DrawContext context, int x, int y, int color) {
        context.fill(x, y, x + HANDLE_SIZE, y + HANDLE_SIZE, 0xFFFFFFFF);
        context.fill(x + 1, y + 1, x + HANDLE_SIZE - 1, y + HANDLE_SIZE - 1, color);
    }
    
    public boolean handleMouseClick(double mouseX, double mouseY, int button) {
        if (!editMode || button != 0) return false;
        
        ResizeCorner corner = getCornerAtPosition((int) mouseX, (int) mouseY);
        if (corner != ResizeCorner.NONE) {
            isResizing = true;
            activeCorner = corner;
            resizeStartX = (int) mouseX;
            resizeStartY = (int) mouseY;
            resizeStartScale = config.scale;
            return true;
        }
        
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
            int deltaFromStartX = (int) mouseX - resizeStartX;
            int deltaFromStartY = (int) mouseY - resizeStartY;
            
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
        if (mouseX >= lastHudX && mouseX <= lastHudX + HANDLE_SIZE &&
            mouseY >= lastHudY && mouseY <= lastHudY + HANDLE_SIZE) {
            return ResizeCorner.TOP_LEFT;
        }
        
        if (mouseX >= lastHudX + lastHudWidth - HANDLE_SIZE && mouseX <= lastHudX + lastHudWidth &&
            mouseY >= lastHudY && mouseY <= lastHudY + HANDLE_SIZE) {
            return ResizeCorner.TOP_RIGHT;
        }
        
        if (mouseX >= lastHudX && mouseX <= lastHudX + HANDLE_SIZE &&
            mouseY >= lastHudY + lastHudHeight - HANDLE_SIZE && mouseY <= lastHudY + lastHudHeight) {
            return ResizeCorner.BOTTOM_LEFT;
        }
        
        if (mouseX >= lastHudX + lastHudWidth - HANDLE_SIZE && mouseX <= lastHudX + lastHudWidth &&
            mouseY >= lastHudY + lastHudHeight - HANDLE_SIZE && mouseY <= lastHudY + lastHudHeight) {
            return ResizeCorner.BOTTOM_RIGHT;
        }
        
        return ResizeCorner.NONE;
    }
    
    public boolean isResizing() {
        return isResizing;
    }
    
    public boolean isDragging() {
        return isDragging;
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    private boolean isMouseOverHud(double mouseX, double mouseY) {
        return mouseX >= lastHudX && mouseX <= lastHudX + lastHudWidth &&
               mouseY >= lastHudY && mouseY <= lastHudY + lastHudHeight;
    }
    
    private int getTotemCountColor(int totemCount) {
        int minCount = 1;
        int maxCount = 30;
        
        int darkGreen = 0xFF00AA00;
        int yellow = 0xFFFFFF00;
        int red = 0xFFFF0000;
        
        if (totemCount <= maxCount / 2) {
            float ratio = (float) (totemCount - minCount) / (maxCount / 2 - minCount);
            return NativeGUIHelper.calculateGradientColor(
                totemCount, minCount, maxCount / 2, darkGreen, yellow
            );
        } else {
            float ratio = (float) (totemCount - maxCount / 2) / (maxCount - maxCount / 2);
            return NativeGUIHelper.calculateGradientColor(
                totemCount, maxCount / 2, maxCount, yellow, red
            );
        }
    }
    
    private void updateAnimations(List<PlayerInfo> players) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        
        float animationSpeed = 8.0f;
        
        Set<UUID> currentPlayerIds = new HashSet<>();
        for (PlayerInfo player : players) {
            currentPlayerIds.add(player.uuid);
            
            Integer previousCount = previousCounts.get(player.uuid);
            if (previousCount == null || previousCount != player.totemCount) {
                animationProgress.put(player.uuid, 0.0f);
                previousCounts.put(player.uuid, player.totemCount);
            } else {
                float progress = animationProgress.getOrDefault(player.uuid, 1.0f);
                if (progress < 1.0f) {
                    progress += deltaTime * animationSpeed;
                    progress = Math.min(1.0f, progress);
                    animationProgress.put(player.uuid, progress);
                }
            }
        }
        
        animationProgress.keySet().removeIf(uuid -> !currentPlayerIds.contains(uuid));
        previousCounts.keySet().removeIf(uuid -> !currentPlayerIds.contains(uuid));
    }

    private static class PlayerInfo {
        final String name;
        final int totemCount;
        final UUID uuid;

        PlayerInfo(String name, int totemCount) {
            this.name = name;
            this.totemCount = totemCount;
            this.uuid = UUID.randomUUID();
        }
        
        PlayerInfo(String name, int totemCount, UUID uuid) {
            this.name = name;
            this.totemCount = totemCount;
            this.uuid = uuid;
        }
    }
}
