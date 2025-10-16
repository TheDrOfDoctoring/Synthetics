package com.thedrofdoctoring.synthetics.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticAbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public class AbilityOverlay implements LayeredDraw.Layer {


    private static final ResourceLocation ABILITY_BAR_SPRITE = Synthetics.rl("hud/ability_bar");
    private static final ResourceLocation ABILITY_BAR_SELECTED_SPRITE = Synthetics.rl("hud/ability_selected");

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if(this.mc.player != null && this.mc.gameMode != null && this.mc.gameMode.getPlayerMode() != GameType.SPECTATOR && this.mc.player.isAlive()  && !this.mc.options.hideGui) {
            AbilityManager manager = SyntheticsPlayer.get(mc.player).getAbilityManager();
            if(!SyntheticsClient.getInstance().getManager().displayAbilities) {
                return;
            }
            SyntheticAbilityActiveInstance[] abilities = manager.getActiveAbilities().toArray(new SyntheticAbilityActiveInstance[0]);
            if(abilities.length == 0) return;
            int i = guiGraphics.guiWidth() / 2;
            int x = ClientConfig.abilityCarouselMiddleX.get();
            int y = ClientConfig.abilityCarouselMiddleY.get();
            guiGraphics.pose().pushPose();
            int selectedAbility = SyntheticsClient.getInstance().getManager().selectedAbility;
            if(selectedAbility >= manager.getActiveAbilities().size()) {
                selectedAbility = 0;
                SyntheticsClient.getInstance().getManager().selectedAbility = 0;
            }
            if(ClientConfig.abilityCarouselRotation.get() != 0) {
                float centerX = i - x + 62 / 2f;
                float centerY = guiGraphics.guiHeight() - y + 22 / 2f;
                guiGraphics.pose().translate(centerX, centerY, 0);
                guiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees(ClientConfig.abilityCarouselRotation.get()), 0, 0, 0);
                guiGraphics.pose().translate(-centerX, -centerY, 0);
            }

            RenderSystem.enableBlend();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);

            guiGraphics.blitSprite(ABILITY_BAR_SPRITE, i - x, guiGraphics.guiHeight() - y, 62, 22);
            guiGraphics.blitSprite(ABILITY_BAR_SELECTED_SPRITE, i - x - 1 + 20, guiGraphics.guiHeight() - y - 1, 24, 23);

            guiGraphics.pose().popPose();

            SyntheticAbilityActiveInstance[] toRender = new SyntheticAbilityActiveInstance[3];

            toRender[1] = abilities[selectedAbility];
            if(abilities.length > selectedAbility + 1) {
                toRender[2] = abilities[selectedAbility + 1];
            } else {
                toRender[2] = abilities[0];
            }
            if(selectedAbility > 0) {
                toRender[0] = abilities[0];
            } else {
                toRender[0] = abilities[abilities.length - 1];
            }

            for (int i1 = 0; i1 < 3; i1++) {
                int j1 = i - x + i1 * 20 + 3;
                int k1 = guiGraphics.guiHeight() - y + 3;
                this.renderAbility(guiGraphics, j1, k1, toRender[i1], manager, i1 == 1);
            }
            guiGraphics.pose().popPose();
            RenderSystem.disableBlend();

        }
    }
    private void renderAbility(GuiGraphics guiGraphics, int x, int y, SyntheticAbilityActiveInstance instance, AbilityManager abilities, boolean selected) {
        if(instance != null) {
            ResourceLocation id = instance.getAbility().getAbilityID();
            ResourceLocation texture = id.withPath("textures/abilities/" + id.getPath() + ".png");
            int percentage = 17;
            if(!selected) {
                guiGraphics.setColor(1, 1, 1, 0.5f);
            } else {
                guiGraphics.setColor(1, 1, 1, 1);
            }

            guiGraphics.blit(texture, x, y, 0, 0, 0, 16, 16, 16, 16);
            if(abilities.isAbilityActive(instance.getAbility())) {
                percentage = (int) ((1 - abilities.getPercentageForAbilityTime(instance)) * 17);
                guiGraphics.fillGradient(x, y + percentage, x + 16, y + 17, 0x88011f4b, 0x8803396c);
            } else if(abilities.isAbilityOnCooldown(instance.getAbility())) {
                percentage = (int) ((1 + abilities.getPercentageForAbilityTime(instance)) * 17);
                guiGraphics.fillGradient(x, y + percentage, x + 16, y + 17, 0x88900000, 0x88ee2400);
            }

            guiGraphics.setColor(1, 1, 1, 1);

        }

    }
}
