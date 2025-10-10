package com.thedrofdoctoring.synthetics.client.screens.menu_screens;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.menus.SyntheticForgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SyntheticForgeScreen extends AbstractContainerScreen<SyntheticForgeMenu> {

    public static final ResourceLocation BACKGROUND = Synthetics.rl("textures/gui/container/synthetic_forge.png");
    public static final ResourceLocation LAVA_ARROW = Synthetics.rl("textures/gui/sprites/forge/lava_arrow.png");
    public static final ResourceLocation LAVA_FLOW = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/lava_still.png");

    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public static final int LAVA_WIDTH = 8;
    public static final int LAVA_HEIGHT = 52;

    private int guiLeft;
    private int guiTop;
    public SyntheticForgeScreen(SyntheticForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;
        this.leftPos = guiLeft;
        this.topPos = guiTop;

    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.guiLeft, this.guiTop, 0, 0, WIDTH, HEIGHT);
        float lavaPercentage = (float) menu.getLavaAmount() / menu.getLavaMaxAmount();
        guiGraphics.blit(LAVA_FLOW, this.guiLeft + 8, (int) (this.guiTop + LAVA_HEIGHT + 17 - Math.ceil(LAVA_HEIGHT * lavaPercentage)), 0, 20, LAVA_WIDTH, (int) Math.ceil(LAVA_HEIGHT * lavaPercentage), 16, 320);
        float recipeCompletion = (float) menu.getRecipeTime() / menu.getTotalRecipeTime();
        guiGraphics.blit(LAVA_ARROW, this.guiLeft + 90, this.guiTop + 35, 0, 0, (int) (22 * recipeCompletion), 15, 22, 15);

    }
}
