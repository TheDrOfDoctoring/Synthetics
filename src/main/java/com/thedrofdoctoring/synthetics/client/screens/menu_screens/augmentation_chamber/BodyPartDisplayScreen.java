package com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.AppliedAugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundRemoveAugmentPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SuppressWarnings("unused")
public class BodyPartDisplayScreen {

    private final int x;
    private final int y;

    private final BodyPart part;
    private final double scale;

    private final List<AppliedAugmentInstance> installedAugments;
    private final Minecraft minecraft;

    private static final int WIDTH = 26;
    private static final int HEIGHT = 26;

    private final int size;
    private int selectedAugment = 0;
    private int selectTick;
    private static final int SELECT_DURATION = 45;

    private static final ResourceLocation BODY_PART_NODE = Synthetics.rl("augmentation/node");
    private static final ResourceLocation AUGMENT_NODE = Synthetics.rl("augmentation/node_wide_border");

    private static final ResourceLocation TITLE = Synthetics.rl("generic/title_red");
    private static final ResourceLocation RIGHT_CLICK_SPRITE = Synthetics.rl("icons/right_click");

    public BodyPartDisplayScreen(PlayerSyntheticDisplayScreen screen, Minecraft minecraft, int x, int y, SyntheticsPlayer player, BodyPart part, double scale) {

        this.part = part;
        this.minecraft = minecraft;
        this.size = Math.max(56 + minecraft.font.width(part.title()), 120);
        List<AppliedAugmentInstance> instances = player.getInstalledAugments();
        this.selectTick = 0;
        this.installedAugments = new ArrayList<>();
        for(AppliedAugmentInstance instance : instances) {
            if(instance.appliedPart().equals(this.part)) {
                installedAugments.add(instance);
            }
        }
        this.scale = scale;
        this.x = (int) (x / scale);
        this.y = (int) (y / scale);
    }

    public void setSelectTick(int amount) {
        this.selectTick = amount;
    }

    public boolean isMouseOver(double mouseX, double mouseY, int scrollX, int scrollY) {
        double minX = x + scrollX - 6 * scale;
        double maxX = x + scrollX + (WIDTH - 7) * scale;

        double minY = y + scrollY;
        double maxY = y + scrollY + HEIGHT * scale;

        return mouseX >= minX && mouseX < maxX && mouseY > minY && mouseY < maxY;
    }

    public BodyPart getPart() {
        return part;
    }

    public void renderHover(GuiGraphics graphics, double mouseX, double mouseY) {
        PoseStack pose = graphics.pose();
        int translation = 0;
        if(scale <= 0.95) {
            translation = (int) (1 / scale);
        }
        int x = this.x - translation;
        int y = this.y - translation;
        pose.pushPose();
        pose.translate(-6, 0, 300);
        pose.pushPose();
        if(!installedAugments.isEmpty()) {
            int renderDirection = 1;
            int yOffsetAugment = 27;
            int yOffsetNode = 25;
            int yOffsetTitle = 22;
            if(y > 190) {
                renderDirection = -1;
                yOffsetAugment = -23;
                yOffsetNode = -25;
                yOffsetTitle = -14;
            }
            int j = 0;
            int k = 0;

            for(int i = 0; i < installedAugments.size(); i++) {
                int width = 25;

                if (j + width > size) {
                    j = 0;
                    k += 1;
                }
                int xPos = j + x;
                int yPos = y + (k * 22 * renderDirection);
                Augment augment = installedAugments.get(i).augment();

                // Only selected augments have their title shown, and are drawn with full opacity.

                if(selectedAugment == i) {
                    pose.pushPose();
                    pose.translate(0, 0, 100);
                    float selectTick = (float) this.selectTick / SELECT_DURATION;
                    graphics.setColor(1, 1, 1, 1);
                    Component title = Component.translatable(augment.title().getString()).withStyle(ChatFormatting.AQUA);
                    graphics.drawString(this.minecraft.font, title, xPos + 18 - 30,  yPos + yOffsetAugment + 3 + yOffsetTitle, -1, true);
                    graphics.setColor(1, 1, 1, 0.75f);
                    graphics.blitSprite(AUGMENT_NODE, xPos, yPos + yOffsetNode, 26, 26);
                    graphics.blit(augment.texture(), xPos + 18 - 13,  yPos + yOffsetAugment + 3, 0, 0, 16, 16,16, 16);
                    graphics.fillGradient(xPos +  18 - 14, yPos + yOffsetAugment + 2, xPos + 18 + 3, (int) ((yPos + yOffsetAugment) + (21 * selectTick)), 0xFF878787, 0xFF5E5E5E);

                    pose.popPose();
                } else {
                    graphics.setColor(0.5f, 0.5f, 0.5f, 0.65f);
                    graphics.blitSprite(AUGMENT_NODE, xPos, yPos + yOffsetNode, 26, 26);
                    graphics.blit(augment.texture(), xPos + 18 - 13,  yPos + yOffsetAugment + 3, 0, 0, 16, 16,16, 16);
                }


                j = j + width - 2;

            }
            graphics.setColor(1, 1, 1, 1);

        }
        pose.popPose();

        graphics.drawString(minecraft.font, this.part.title(), x + 30, y + 9, -1, true);
        graphics.blitSprite(TITLE, x - 15, y + 3, size + 10, 20);
        graphics.blitSprite(BODY_PART_NODE,  x, y, 26, 26);
        graphics.blit(part.texture(), x + 5, y + 5, 0, 0, 16, 16, 16, 16);
        graphics.blitSprite(RIGHT_CLICK_SPRITE, x - 13, y + 4, 15, 16);
        pose.popPose();
    }
    public void render(GuiGraphics graphics) {

        PoseStack pose = graphics.pose();

        pose.pushPose();

        pose.translate(-6, 0, 200);
        graphics.pose().pushPose();
        int x = (int) (this.x / scale);
        int y = (int) (this.y / scale);
        graphics.pose().scale((float) scale, (float) scale, 1);
        graphics.blitSprite(BODY_PART_NODE, x, y, WIDTH, HEIGHT);

        RenderSystem.enableBlend();
        graphics.blit(part.texture(), x + 5, y + 5, 0, 0, 16, 16, 16, 16);
        graphics.pose().popPose();

        pose.popPose();

    }

    public void onHoldClick(int button) {
        if(this.selectedAugment < installedAugments.size() && installedAugments.get(selectedAugment) != null) {
            this.selectTick++;
            if(this.selectTick == SELECT_DURATION) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(ServerboundRemoveAugmentPacket.create(this.installedAugments.get(selectedAugment)));
                this.selectTick = 0;
                playSoundEffect(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f);
            }
        }
    }
    @SuppressWarnings("SameParameterValue")
    private void playSoundEffect(@NotNull SoundEvent event, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, pitch));
        }
    }


    public void scroll(double scrollX, double scrollY) {
        int increment;
        if(scrollY < 0) {
            increment = 1;
        } else {
            increment = -1;
        }
        this.selectTick = 0;
        this.selectedAugment = Math.clamp(this.selectedAugment + increment, 0, Math.max(0, this.installedAugments.size() - 1));

    }


}
