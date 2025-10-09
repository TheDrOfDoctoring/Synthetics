package com.thedrofdoctoring.synthetics.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundResearchPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@SuppressWarnings({"unused", "FieldCanBeLocal"})
// Based on the Vampirism Skill Screen and Minecraft Advancement Screen, with some changes.
public class ResearchScreen extends Screen {

    public static final int SCREEN_WIDTH = 252;
    public static final int SCREEN_HEIGHT = 219;

    private final Map<ResearchNode, ResearchNodeScreen> rootNodes = new Object2ObjectOpenHashMap<>();
    private final List<ResearchNodeScreen> allNodes = new ArrayList<>();
    private boolean scrolling;
    private Vec3 mousePos;
    private Vec3 rightClickPos;

    private boolean clicked;
    private boolean rightClicked;

    private int guiLeft;
    private int guiTop;
    private double scrollX;
    private double scrollY;
    private double minX = Double.MIN_VALUE;
    private double minY = Double.MAX_VALUE;
    private double maxX = Double.MAX_VALUE;
    private double maxY = Double.MIN_VALUE;
    private double zoom = 0.5f;
    private final double maxZoom = 2;
    private final double minZoom = 0.25;
    private SyntheticsPlayer player;

    private static final ResourceLocation BACKGROUND = Synthetics.rl("textures/gui/research/background.png");
    private static final ResourceLocation WINDOW = Synthetics.rl("textures/gui/research/window.png");

    private double centerX;
    private double centerY;

    private float fade;
    public ResearchScreen() {
        super(Component.translatable("screens.synthetics.research_title"));
        if (Minecraft.getInstance().player != null) {
            this.player = SyntheticsPlayer.get(Minecraft.getInstance().player);
        }
        List<ResearchNode> nodes = SyntheticsClient.getInstance().getManager().rootNodes;
        for(ResearchNode node : nodes) {
            ResearchNodeScreen screen = new ResearchNodeScreen(null, node, this, node.x(), node.y(), player.getResearchManager());
            this.rootNodes.put(node, screen);
            addNode(screen);

        }


        this.minY = -(200+16);
        this.maxY = 20;

        this.minX = -SCREEN_WIDTH;
        this.maxX = SCREEN_WIDTH;
        this.centerX = 0;
        this.centerY = 0;

    }

    public void addNode(ResearchNodeScreen node) {
        this.allNodes.add(node);
        for(ResearchNodeScreen child : node.getChildren()) {
            addNode(child);
        }
    }


    @Override
    protected void init() {
        assert this.minecraft != null;
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;

    }


    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderInside(guiGraphics, guiLeft, guiTop, mouseX - guiLeft, mouseY - guiTop);
        this.renderWindow(guiGraphics, guiLeft, guiTop);

        for(Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        this.renderConnections(guiGraphics, guiLeft, guiTop);
        this.renderTooltips(guiGraphics, mouseX - guiLeft, mouseY - guiTop);


    }
    public void renderWindow(GuiGraphics guiGraphics, int x, int y) {
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        RenderSystem.enableBlend();

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(WINDOW, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        guiGraphics.drawString(this.font, getTitle(), x + 8, y + 6, 4210752, false);

        RenderSystem.disableBlend();
        stack.popPose();
    }
    public void renderTooltips(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(guiLeft, guiTop, 0);
        pose.translate(0.0F, 0.0F, -200.0F);


        graphics.fill(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        if(mouseX >= 0 && mouseX < SCREEN_WIDTH && mouseY >= 0 && mouseY < SCREEN_HEIGHT) {
            double scaledMouseX = getScaledMouseX(mouseX);
            double scaledMouseY = getScaledMouseY(mouseY);
            for (ResearchNodeScreen nodeScreen : this.allNodes) {
                if (nodeScreen.isMouseOver(scaledMouseX, scaledMouseY, 0, 0)) {
                    flag = true;
                    pose.pushPose();
                    pose.translate(SCREEN_WIDTH/2d + centerX, 20 + centerY, 0);
                    pose.scale((float) this.zoom, (float) this.zoom, 1);
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

    private void renderInside(@NotNull GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        PoseStack pose = guiGraphics.pose();
        guiGraphics.enableScissor(x, y, x + SCREEN_WIDTH, y + SCREEN_HEIGHT);
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.translate(SCREEN_WIDTH/2d + centerX, 20 + centerY, 0);
        pose.scale((float)this.zoom,(float) this.zoom, 1);

        for (int i = -(int)((((double) SCREEN_WIDTH /2 + centerX)  /16/zoom)) -1; i <= (int)((((double) SCREEN_WIDTH /2 - centerX)  /16/zoom)); ++i) {
            for (int j = -(int) ((20 + centerY) / 16 / zoom) - 1; j <= (int) ((SCREEN_HEIGHT - centerY) / 16 / zoom); ++j) {
                guiGraphics.blit(BACKGROUND, 16 * i, 16 * j, 0.0F, 0.0F, 16, 16, 16, 16);
            }
        }

        for(ResearchNodeScreen node : rootNodes.values()) {
            node.draw(guiGraphics, x, y);
        }


        pose.popPose();
        guiGraphics.disableScissor();
    }

    private void renderConnections(@NotNull GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.enableScissor(x + 9, y + 18, x + SCREEN_WIDTH - 9, y + SCREEN_HEIGHT - 28);

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.translate(SCREEN_WIDTH/2d + centerX, 20 + centerY, -250);
        pose.scale((float)this.zoom,(float) this.zoom, 1);
        for(ResearchNodeScreen node : rootNodes.values()) {
            node.renderConnections(guiGraphics, 0,0, true);
            node.renderConnections(guiGraphics, 0, 0, false);
        }
        pose.popPose();
        guiGraphics.disableScissor();

    }
    private double getScaledMouseX(double mouseX) {
        return (mouseX -SCREEN_WIDTH/2d - centerX)/zoom;
    }

    private double getScaledMouseY(double mouseY) {
        return (mouseY - 20  - centerY) /zoom;
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


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double xDragged, double yDragged) {
        this.scrolling = true;
        center(this.centerX + xDragged, this.centerY + yDragged);
        return super.mouseDragged(mouseX, mouseY, mouseButton, xDragged, yDragged);
    }
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        this.scrolling = true;
        double x = pMouseX - guiLeft;
        double y = pMouseY - guiTop;
        double mouseX = getScaledMouseX(x);
        double mouseY = getScaledMouseY(y);
        this.zoom = Mth.clamp(this.zoom + (float)pScrollX * 0.1f + (float)pScrollY * 0.1f, this.minZoom, this.maxZoom);

        center(this.centerX - (mouseX - getScaledMouseX(x)) * zoom, this.centerY - (mouseY - getScaledMouseY(y)) * zoom);
        return super.mouseScrolled(x, y, pScrollX, pScrollY);
    }

    public void center(double x, double y) {
        this.centerX = Mth.clamp(x, this.minX *zoom, this.maxX*zoom);
        this.centerY = Mth.clamp(y, -SCREEN_HEIGHT * 2 * zoom,  20*zoom);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrolling) {
            scrolling = false;
            this.rightClickPos = new Vec3(mouseX, mouseY, 0);
        }
        if (button == 0) {
            this.clicked = true;
            this.mousePos = new Vec3(mouseX, mouseY, 0);
        }
        if (button == 1) {
            this.rightClicked = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.clicked) {
                if (!this.scrolling || (this.mousePos != null && this.mousePos.distanceTo(new Vec3(mouseX, mouseY, 0)) < 5)) {
                    unlockNode(mouseX, mouseY);
                }
            }
            this.clicked = false;
        }
        if (button == 1) {
            if (this.rightClicked) {
                if (!this.scrolling || (this.rightClickPos != null && this.rightClickPos.distanceTo(new Vec3(mouseX, mouseY, 0)) < 5)) {
                    switchNodeDisplay(mouseX, mouseY);
                }
            }
            this.rightClicked = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    private void unlockNode(double mouseX, double mouseY) {
        ResearchNode selected = this.getSelected((int) (mouseX - guiLeft), (int) (mouseY - guiTop));
        if (selected != null) {
            if (canResearchNode(selected) && Minecraft.getInstance().getConnection() != null) {
                Minecraft.getInstance().getConnection().send(ServerboundResearchPacket.create(selected));
                playSoundEffect(SoundEvents.PLAYER_LEVELUP, 0.7F);
            } else {
                playSoundEffect(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5F);
            }
        }
    }

    private void switchNodeDisplay(double mouseX, double mouseY) {
        ResearchNodeScreen selected = this.getSelectedScreen((int) (mouseX - guiLeft), (int) (mouseY - guiTop));
        if (selected != null) {
            selected.switchDisplay();

        }
    }

    private boolean canResearchNode(@NotNull ResearchNode node) {
        if (this.player == null) return false;
        return this.player.getResearchManager().canResearch(node);
    }

    private void playSoundEffect(@NotNull SoundEvent event, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
        }
    }
}
