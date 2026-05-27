package com.sanhiruzu.amphibia.client.screen;

import com.sanhiruzu.amphibia.menu.FrogChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class FrogChestScreen extends AbstractContainerScreen<FrogChestMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    // Fixed GUI height consumed by non-chest-row elements (title bar + player inv area).
    private static final int FIXED_HEIGHT = 114;

    private final int rows;
    private int visibleRows;
    private int scrollOffset = 0;
    private boolean needsScroll;

    public FrogChestScreen(FrogChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.rows = menu.getRowCount();
    }

    @Override
    protected void init() {
        this.visibleRows = Math.min(rows, Math.max(3, (this.height - FIXED_HEIGHT) / 18));
        this.imageHeight = FIXED_HEIGHT + visibleRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
        this.scrollOffset = Mth.clamp(scrollOffset, 0, Math.max(0, rows - visibleRows));
        this.needsScroll = rows > visibleRows;
        super.init();
    }

    // ─── Slot coordinate helpers ──────────────────────────────────────────────

    /**
     * In ChestMenu, slot.y follows these vanilla patterns (container-relative):
     *   chest slot row r   →  18 + r*18       (y%18 == 0, y < 18+rows*18)
     *   player inv row r   →  18 + rows*18 + 14 + r*18   (y%18 == 14)
     *   hotbar             →  18 + rows*18 + 72           (y%18 == 0, y >= 18+rows*18)
     */
    private boolean isChestSlotY(int slotY) {
        return slotY >= 18 && slotY < 18 + rows * 18 && slotY % 18 == 0;
    }

    private boolean isChestRowVisible(int slotY) {
        int row = (slotY - 18) / 18;
        return row >= scrollOffset && row < scrollOffset + visibleRows;
    }

    /**
     * Y offset (in container-relative GUI units) to add to slot.y for rendering/hit-testing.
     * Chest slots scroll up; player inventory follows visibleRows instead of actual rows.
     */
    private int yOffset(int slotY) {
        if (isChestSlotY(slotY)) return -scrollOffset * 18;
        if (slotY >= 18 + rows * 18)  return -(rows - visibleRows) * 18;
        return 0;
    }

    // ─── Rendering ────────────────────────────────────────────────────────────

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (needsScroll) drawScrollBar(guiGraphics);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, x, y, 0, 0, this.imageWidth, visibleRows * 18 + 17);
        guiGraphics.blit(CONTAINER_BACKGROUND, x, y + visibleRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    /**
     * Slot items are rendered inside a pre-translated pose (leftPos, topPos).
     * Apply an additional Y translation so each slot type appears at its correct
     * visual row position regardless of actual slot.y.
     */
    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (isChestSlotY(slot.y) && !isChestRowVisible(slot.y)) return;

        int dy = yOffset(slot.y);
        if (dy != 0) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, dy, 0);
            super.renderSlot(guiGraphics, slot);
            guiGraphics.pose().popPose();
        } else {
            super.renderSlot(guiGraphics, slot);
        }
    }

    /** The hover highlight is drawn in the same pre-translated pose context as renderSlot. */
    @Override
    protected void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
        if (isChestSlotY(slot.y) && !isChestRowVisible(slot.y)) return;

        int dy = yOffset(slot.y);
        if (dy != 0) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, dy, 0);
            super.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick);
            guiGraphics.pose().popPose();
        } else {
            super.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick);
        }
    }

    private void drawScrollBar(GuiGraphics guiGraphics) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int trackX = x + imageWidth + 3;
        int trackY = y + 17;
        int trackH = visibleRows * 18;
        int maxScroll = rows - visibleRows;
        int thumbH = Math.max(8, trackH * visibleRows / rows);
        int thumbY = trackY + (trackH - thumbH) * scrollOffset / maxScroll;

        guiGraphics.fill(trackX, trackY, trackX + 6, trackY + trackH, 0xFF555555);
        guiGraphics.fill(trackX + 1, thumbY, trackX + 5, thumbY + thumbH, 0xFFBBBBBB);
    }

    // ─── Hit testing ──────────────────────────────────────────────────────────

    /**
     * The vanilla private isHovering(Slot, ...) calls this protected overload with
     * (slot.x, slot.y, 16, 16, mouseX, mouseY).  We intercept slot-sized checks
     * (16×16) and adjust the reference y so it matches the visual rendering position.
     */
    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        if (needsScroll && width == 16 && height == 16) {
            if (isChestSlotY(y)) {
                if (!isChestRowVisible(y)) return false;
                return super.isHovering(x, y - scrollOffset * 18, width, height, mouseX, mouseY);
            }
            if (y >= 18 + rows * 18) {
                return super.isHovering(x, y - (rows - visibleRows) * 18, width, height, mouseX, mouseY);
            }
        }
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    // ─── Scroll input ─────────────────────────────────────────────────────────

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (needsScroll) {
            scrollOffset = Mth.clamp(scrollOffset - (int) Math.signum(scrollY), 0, rows - visibleRows);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
