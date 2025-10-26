package com.thedrofdoctoring.synthetics.client.screens.research;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResearchTabScreen {

    private final ResearchScreen mainScreen;
    private final List<ResearchNodeScreen> allNodes;
    private final AdvancementTabType position;
    private final ItemStack icon;
    private final int index;
    private final Map<ResearchNode, ResearchNodeScreen> rootNodes = new Object2ObjectOpenHashMap<>();
    private int centerX;
    private int centerY;
    private float zoom = 0.5f;

    public static final int SCREEN_WIDTH = ResearchScreen.SCREEN_WIDTH;
    public static final int SCREEN_HEIGHT = ResearchScreen.SCREEN_HEIGHT;

    private float fade;

    public ResearchTabScreen(ResearchScreen mainScreen, ItemStack icon, int index, List<ResearchNode> nodes) {
        this.position = AdvancementTabType.LEFT;
        this.icon = icon;
        this.mainScreen = mainScreen;
        this.index = index;
        this.allNodes = new ArrayList<>();
        for(ResearchNode node : nodes) {
            ResearchNodeScreen screen = new ResearchNodeScreen(null, node, this, node.x(), node.y(), mainScreen.player().getResearchManager());
            this.rootNodes.put(node, screen);
            addNode(screen);
        }
    }
    public void addNode(ResearchNodeScreen node) {
        this.allNodes.add(node);
        for(ResearchNodeScreen child : node.getChildren()) {
            addNode(child);
        }
    }
    public void renderInside(@NotNull GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.enableScissor(x + 9, y + 18, x + SCREEN_WIDTH - 9, y + SCREEN_HEIGHT - 28);
        guiGraphics.pose().pushPose();
        PoseStack pose = guiGraphics.pose();
        pose.translate(x, y, 0);
        pose.translate(SCREEN_WIDTH/2d + centerX, 20 + centerY, 0);
        pose.scale(this.zoom, this.zoom, 1);
        for(ResearchNodeScreen node : rootNodes.values()) {
            node.draw(guiGraphics, x, y);
        }
        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();

    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int guiLeft = mainScreen.guiLeft();
        int guiTop = mainScreen.guiTop();
        this.renderInside(guiGraphics, guiLeft, guiTop);
        this.renderConnections(guiGraphics, guiLeft, guiTop);
        this.renderTooltips(guiGraphics, mouseX - guiLeft, mouseY - guiTop);

    }

    private void renderConnections(@NotNull GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.enableScissor(x + 9, y + 18, x + SCREEN_WIDTH - 9, y + SCREEN_HEIGHT - 28);

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.translate(SCREEN_WIDTH/2d + centerX, 20 + centerY, 0);
        pose.scale(this.zoom,this.zoom, 1);
        for(ResearchNodeScreen node : rootNodes.values()) {
            node.renderConnections(guiGraphics, 0,0, true);
            node.renderConnections(guiGraphics, 0, 0, false);
        }
        pose.popPose();
        guiGraphics.disableScissor();
    }

    public void renderTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(mainScreen.guiLeft(), mainScreen.guiTop(), 0);

        graphics.fill(8, 18, SCREEN_WIDTH - 8, SCREEN_HEIGHT + 18, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        if(mouseX >= 0 && mouseX < SCREEN_WIDTH && mouseY >= 0 && mouseY < SCREEN_HEIGHT) {
            double scaledMouseX = getScaledMouseX(mouseX);
            double scaledMouseY = getScaledMouseY(mouseY);
            for (ResearchNodeScreen nodeScreen : this.allNodes) {
                if (nodeScreen.isMouseOver(scaledMouseX, scaledMouseY, 0, 0)) {
                    flag = true;
                    pose.pushPose();
                    pose.translate(SCREEN_WIDTH/2d + centerX, 20 + centerY, 350);
                    pose.scale(this.zoom, this.zoom, 1);
                    nodeScreen.renderHover(graphics, scaledMouseX, scaledMouseY, this.fade, 0,0);
                    pose.popPose();
                    break;
                }
            }
        }


        pose.popPose();
        if (flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }
    private double getScaledMouseX(double mouseX) {
        return (mouseX -SCREEN_WIDTH/2d - centerX)/zoom;
    }

    private double getScaledMouseY(double mouseY) {
        return (mouseY - 20  - centerY) /zoom;
    }

    public boolean isMouseOver(int guiLeft, int guiTop, double mouseX, double mouseY) {
        return this.position.isMouseOver(guiLeft, guiTop, this.index, mouseX, mouseY);
    }

    @Nullable
    public ResearchNode getSelected(int mouseX, int mouseY) {
        for (ResearchNodeScreen nodeScreen : this.allNodes) {
            ResearchNode selected = nodeScreen.getSelectedNode(getScaledMouseX(mouseX), getScaledMouseY(mouseY),0,0);
            if (selected != null) {
                return selected;
            }
        }
        return null;
    }
    @Nullable
    public ResearchNodeScreen getSelectedScreen(int mouseX, int mouseY) {
        for (ResearchNodeScreen nodeScreen : this.allNodes) {
            ResearchNodeScreen selected = nodeScreen.getSelectedNodeScreen(getScaledMouseX(mouseX), getScaledMouseY(mouseY),0,0);
            if (selected != null) {
                return selected;
            }
        }
        return null;
    }

    public void drawTab(GuiGraphics guiGraphics, int x, int y, boolean selected) {
        this.position.draw(guiGraphics, x, y, selected, this.index);
    }
    public void drawIcon(GuiGraphics guiGraphics, int x, int y) {
        this.position.drawIcon(guiGraphics, x, y, this.index, this.icon);
    }


    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }
}
