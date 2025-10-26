package com.thedrofdoctoring.synthetics.client.screens.research;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchTab;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundResearchPacket;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
// Based on the Vampirism Skill Screen and Minecraft Advancement Screen, with some changes.
public class ResearchScreen extends Screen {

    public static final int SCREEN_WIDTH = 252;
    public static final int SCREEN_HEIGHT = 219;

    private boolean scrolling;
    private Vec3 mousePos;
    private Vec3 rightClickPos;

    private boolean clicked;
    private boolean rightClicked;

    private int guiLeft;
    private int guiTop;
    private double scrollX;
    private double scrollY;
    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;
    private double zoom = 0.5f;
    private final double maxZoom = 2;
    private final double minZoom = 0.25;

    private SyntheticsPlayer player;

    private static final ResourceLocation BACKGROUND = Synthetics.rl("textures/gui/research/background.png");
    private static final ResourceLocation WINDOW = Synthetics.rl("textures/gui/research/window.png");

    private double centerX;
    private double centerY;

    private float fade;
    private final ArrayList<ResearchTabScreen> researchTabs = new ArrayList<>();
    private ResearchTabScreen selectedTab;
    private int selectedTabIndex;

    public ResearchScreen() {
        super(Component.translatable("screens.synthetics.research_title"));
        if (Minecraft.getInstance().player != null) {
            this.player = SyntheticsPlayer.get(Minecraft.getInstance().player);
        }
        Map<ResearchTab, List<ResearchNode>> nodes = SyntheticsClient.getInstance().getManager().rootNodes;
        int i = 0;
        for(Map.Entry<ResearchTab, List<ResearchNode>> entry : nodes.entrySet()) {
            this.researchTabs.add(new ResearchTabScreen(this, entry.getKey().displayIcon(), i, entry.getValue()));
            i++;
        }
        this.selectedTab = this.researchTabs.getFirst();


        this.minY = -(200+16);
        this.maxY = 20;

        this.minX = -SCREEN_WIDTH;
        this.maxX = SCREEN_WIDTH;
        this.centerX = 0;
        this.centerY = 0;

    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;
    }

    public SyntheticsPlayer player() {
        return player;
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderInside(guiGraphics, guiLeft, guiTop, mouseX - guiLeft, mouseY - guiTop);

        this.renderWindow(guiGraphics, guiLeft, guiTop);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        for(Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        this.selectedTab.render(guiGraphics, mouseX, mouseY);

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

    private void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.pose().pushPose();
        for(ResearchTabScreen screen : this.researchTabs) {
            screen.drawTab(guiGraphics, guiLeft, this.guiTop + 17, screen == selectedTab);
            screen.drawIcon(guiGraphics, guiLeft, this.guiTop + 17);
        }
        guiGraphics.pose().popPose();

    }

    private void renderInside(@NotNull GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        PoseStack pose = guiGraphics.pose();
        guiGraphics.enableScissor(x + 9, y + 18, x + SCREEN_WIDTH - 9, y + SCREEN_HEIGHT - 28);
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.translate(SCREEN_WIDTH/2d + centerX, 20 + centerY, 0);
        pose.scale((float)this.zoom,(float) this.zoom, 1);

        for (int i = -(int)((((double) SCREEN_WIDTH /2 + centerX)  /16/zoom)) -1; i <= (int)((((double) SCREEN_WIDTH /2 - centerX)  /16/zoom)); ++i) {
            for (int j = -(int) ((20 + centerY) / 16 / zoom) - 1; j <= (int) ((SCREEN_HEIGHT - centerY) / 16 / zoom); ++j) {
                guiGraphics.blit(BACKGROUND, 16 * i, 16 * j, 0.0F, 0.0F, 16, 16, 16, 16);
            }
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
        this.selectedTab.setCenterY((int) centerY);
        this.selectedTab.setCenterX((int) centerX);
        this.selectedTab.setZoom((float) zoom);
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

    public int guiLeft() {
        return guiLeft;
    }

    public int guiTop() {
        return guiTop;
    }



    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.clicked) {
                if (!this.scrolling || (this.mousePos != null && this.mousePos.distanceTo(new Vec3(mouseX, mouseY, 0)) < 5)) {
                    int i = 0;
                    for (ResearchTabScreen tab : this.researchTabs) {
                        if (tab != this.selectedTab && tab.isMouseOver(this.guiLeft, this.guiTop + 17, mouseX, mouseY)) {
                            this.selectedTabIndex = i;
                            this.selectedTab = tab;
                            break;
                        }
                        i++;
                    }
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
        ResearchNode selected = this.selectedTab.getSelected((int) (mouseX - guiLeft), (int) (mouseY - guiTop));
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
        ResearchNodeScreen selected = this.selectedTab.getSelectedScreen((int) (mouseX - guiLeft), (int) (mouseY - guiTop));
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
