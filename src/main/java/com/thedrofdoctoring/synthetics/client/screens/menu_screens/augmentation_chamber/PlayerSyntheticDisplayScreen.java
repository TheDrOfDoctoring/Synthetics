package com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.SyntheticsEntities;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPartType;
import com.thedrofdoctoring.synthetics.entities.OrganDisplayMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class PlayerSyntheticDisplayScreen {

    private static final int WIDTH = 67;
    private static final int HEIGHT = 99;

    private final AugmentationChamberScreen mainScreen;
    private final List<BodyPartDisplayScreen> partDisplays;
    private final int x;
    private final int y;
    private final AdvancementTabType position;
    private final ItemStack icon;
    private final @NotNull LivingEntity displayEntity;
    private final int index;
    @SuppressWarnings("DuplicateBranchesInSwitch")
    public PlayerSyntheticDisplayScreen(AugmentationChamberScreen mainScreen, BodyPartType.Layer layer, int index) {
        this.mainScreen = mainScreen;
        // 89, 8
        Minecraft mc = Minecraft.getInstance();
        this.x = 89;
        this.y = 8;
        partDisplays = new ArrayList<>();
        SyntheticsPlayer player = SyntheticsPlayer.get(Objects.requireNonNull(mc.player));
        for(BodyPartType type : SyntheticsClient.getInstance().getManager().partTypes) {
            if(type.bodyLayer() != layer) continue;
            BodyPart part = player.getPartManager().getPartForType(type);
            if(part != null) {
                partDisplays.add(new BodyPartDisplayScreen(this, mc, type.x(), type.y(), player, part));
            }
        }

        this.position = AdvancementTabType.RIGHT;
        this.index = index;

        switch(layer) {
            case EXTERIOR -> {
                this.icon = new ItemStack(Items.PLAYER_HEAD);
                this.displayEntity = mc.player;
            }
            case BONE -> {
                this.icon = new ItemStack(Items.SKELETON_SKULL);
                this.displayEntity = new Skeleton(EntityType.SKELETON, player.getEntity().level());
            }
            case ORGANS -> {
                this.icon = new ItemStack(SyntheticsBlocks.ORGAN_SKULL.asItem());
                this.displayEntity = new OrganDisplayMob(SyntheticsEntities.ORGAN_DISPLAY_MOB.get(), player.getEntity().level());

            }
            case null, default -> {
                this.icon = new ItemStack(Items.PLAYER_HEAD);
                this.displayEntity = mc.player;
            }
        }
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(mainScreen.guiLeft, mainScreen.guiTop, 0);
        renderEntity(guiGraphics, mouseX, mouseY, partialTick);
        pose.translate(x + (float) WIDTH / 2, y, 50) ;
        pose.scale(0.4f, 0.4f, 1);
        for(BodyPartDisplayScreen parts : partDisplays) {
            parts.render(guiGraphics);

        }
        pose.popPose();
    }
    public void renderHover(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(mainScreen.guiLeft, mainScreen.guiTop, 0);
        pose.translate(x + (float) WIDTH / 2, y, 50) ;
        pose.scale(0.4f, 0.4f, 1);
        if(mouseX >= 0 && mouseX < AugmentationChamberScreen.WIDTH && mouseY >= 0 && mouseY < AugmentationChamberScreen.HEIGHT) {
            double scaledMouseX = getScaledMouseX(mouseX);
            double scaledMouseY = getScaledMouseY(mouseY);
            for (BodyPartDisplayScreen part : partDisplays) {
                if (part.isMouseOver(scaledMouseX, scaledMouseY, 0, 0)) {
                    if(this.mainScreen.rightClicked) {
                        part.onHoldClick(1);
                    }
                    part.renderHover(guiGraphics, scaledMouseX, scaledMouseY);
                    break;
                }
            }
        }
        pose.popPose();
    }

    public void renderEntity(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 150);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateY(((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        float f4 = displayEntity.yBodyRot;
        float f5 = displayEntity.getYRot();
        float f6 = displayEntity.getXRot();
        float f7 = displayEntity.yHeadRotO;
        float f8 = displayEntity.yHeadRot;
        displayEntity.yBodyRot = 180.0F;
        displayEntity.setYRot(180.0F);
        displayEntity.setXRot(0.0F);
        displayEntity.yHeadRot = displayEntity.getYRot();
        displayEntity.yHeadRotO = displayEntity.getYRot();
        float factor = displayEntity.getBbHeight() / 1.8f;
        guiGraphics.pose().scale(1, 1 / factor, 1);
        InventoryScreen.renderEntityInInventory(guiGraphics, x + (float) WIDTH / 2, (y + HEIGHT) * factor, 50, new Vector3f(0, 0, 0), quaternionf, quaternionf1, displayEntity);
        displayEntity.yBodyRot = f4;
        displayEntity.setYRot(f5);
        displayEntity.setXRot(f6);
        displayEntity.yHeadRotO = f7;
        displayEntity.yHeadRot = f8;
        guiGraphics.pose().popPose();
    }

    public void drawTab(GuiGraphics guiGraphics, int x, int y, boolean selected) {
        this.position.draw(guiGraphics, x, y, selected, this.index);
    }
    public void drawIcon(GuiGraphics guiGraphics, int x, int y) {
        this.position.drawIcon(guiGraphics, x, y, this.index, this.icon);
    }
    public boolean isMouseOver(int guiLeft, int guiTop, double mouseX, double mouseY) {
        return this.position.isMouseOver(guiLeft, guiTop, this.index, mouseX, mouseY);
    }
    public boolean isMouseOverScreen(double mouseX, double mouseY) {
        return mouseX >= mainScreen.guiLeft + x && mouseX < mainScreen.guiLeft + x + WIDTH && mouseY > y + mainScreen.guiTop && mouseY < y + mainScreen.guiTop + HEIGHT;
    }
    public void mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double x = getScaledMouseX(mouseX);
        double y = getScaledMouseY(mouseY);
        for (BodyPartDisplayScreen part : partDisplays) {
            if (part.isMouseOver(x, y, 0, 0)) {
                part.scroll(scrollX, scrollY);
                break;
            }
        }
    }
    public void mouseReleased(double mouseX, double mouseY, int button) {
        double x = getScaledMouseX(mouseX);
        double y = getScaledMouseY(mouseY);
        for (BodyPartDisplayScreen part : partDisplays) {
            if (part.isMouseOver(x, y, 0, 0)) {
                if(button == 1) {
                    part.setSelectTick(0);
                }
                break;
            }
        }
    }
    private double getScaledMouseX(double mouseX) {
        return (mouseX - x - WIDTH/2d) / 0.4f;
    }

    private double getScaledMouseY(double mouseY) {
        return (mouseY - 7) / 0.4f;
    }
}
