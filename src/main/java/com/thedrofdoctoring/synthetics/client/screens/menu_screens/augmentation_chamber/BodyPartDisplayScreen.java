package com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.AugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
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
    private final List<SyntheticAugment> installedAugments;
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

    public BodyPartDisplayScreen(PlayerSyntheticDisplayScreen screen, Minecraft minecraft, int x, int y, SyntheticsPlayer player, BodyPart part) {
        this.x = x;
        this.y = y;
        this.part = part;
        this.minecraft = minecraft;
        this.size = Math.max(56 + minecraft.font.width(part.title()), 120);
        List<AugmentInstance> instances = player.getInstalledAugments();
        this.selectTick = 0;
        this.installedAugments = new ArrayList<>();
        for(AugmentInstance instance : instances) {
            if(instance.appliedPart().equals(this.part)) {
                installedAugments.add(instance.augment());
            }
        }
    }

    public void setSelectTick(int amount) {
        this.selectTick = amount;
    }

    public boolean isMouseOver(double mouseX, double mouseY, int scrollX, int scrollY) {
        return mouseX >= x + scrollX - 6 && mouseX < x + scrollX + (double) WIDTH - 7 && mouseY > scrollY + y && mouseY < scrollY + this.y + HEIGHT;
    }
    public void renderHover(GuiGraphics graphics, double mouseX, double mouseY) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(-6, 0, 300);
        pose.pushPose();
        if(!installedAugments.isEmpty()) {
            int renderDirection = 1;
            int yOffsetAugment = 10;
            int yOffsetNode = 12;
            int yOffsetTitle = -12;
            if(y > 190) {
                renderDirection = -1;
                yOffsetAugment = -23;
                yOffsetNode = -25;
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
                int yPos = y + (k * 8 * renderDirection);
                if(selectedAugment == i) {
                    pose.pushPose();
                    pose.translate(0, 0, 100);
                    float selectTick = (float) this.selectTick / SELECT_DURATION;
                    graphics.setColor(1, 1, 1, 1);
                    Component title = Component.translatable(installedAugments.get(i).title().getString()).withStyle(ChatFormatting.AQUA);
                    graphics.drawString(this.minecraft.font, title, xPos + 18 - 30,  yPos + yOffsetAugment + 3 + yOffsetTitle, -1, true);
                    graphics.setColor(1, 1, 1, 0.75f);
                    graphics.blitSprite(AUGMENT_NODE, xPos, yPos + yOffsetNode, 26, 26);
                    graphics.blit(installedAugments.get(i).texture(), xPos + 18 - 13,  yPos + yOffsetAugment + 3, 0, 0, 16, 16,16, 16);
                    graphics.fillGradient(xPos +  18 - 14, yPos + yOffsetAugment + 2, xPos + 18 + 3, (int) ((yPos + yOffsetAugment) + (21 * selectTick)), 0xFF878787, 0xFF5E5E5E);

                    pose.popPose();
                } else {

                    graphics.setColor(0.5f, 0.5f, 0.5f, 0.65f);
                    graphics.blitSprite(AUGMENT_NODE, xPos, yPos + yOffsetNode, 26, 26);
                    graphics.blit(installedAugments.get(i).texture(), xPos + 18 - 13,  yPos + yOffsetAugment + 3, 0, 0, 16, 16,16, 16);
                }


                j = j + width - 2;

            }
            graphics.setColor(1, 1, 1, 1);

        }
        pose.popPose();

        graphics.drawString(minecraft.font, this.part.title(), x + 30, y + 9, -1, true);
        graphics.blitSprite(TITLE, x - 15, y + 3, size + 10, 20);
        graphics.blitSprite(BODY_PART_NODE,  x, y, 26, 26);
        graphics.blitSprite(RIGHT_CLICK_SPRITE, x - 13, y + 4, 15, 16);
        graphics.blit(part.texture(), x + 5, y + 5, 0, 0, 16, 16, 16, 16);
        pose.popPose();
    }
    public void render(GuiGraphics graphics) {

        PoseStack pose = graphics.pose();

        pose.pushPose();

        pose.translate(-6, 0, 200);
        graphics.blitSprite(BODY_PART_NODE, x, y, WIDTH, HEIGHT);

        RenderSystem.enableBlend();
        graphics.blit(part.texture(), x + 5, y + 5, 0, 0, 16, 16, 16, 16);

        pose.popPose();

    }

    public void onHoldClick(int button) {
        if(this.selectedAugment < installedAugments.size() && installedAugments.get(selectedAugment) != null) {
            this.selectTick++;
            if(this.selectTick == SELECT_DURATION) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(new ServerboundRemoveAugmentPacket(this.installedAugments.get(selectedAugment)));
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
        this.selectedAugment = Math.clamp(this.selectedAugment + increment, 0, this.installedAugments.size() - 1);

    }


}
