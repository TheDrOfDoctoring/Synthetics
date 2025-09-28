package com.thedrofdoctoring.synthetics.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticAbilityActiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticActiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AbilityOverlay implements LayeredDraw.Layer {


    private static final ResourceLocation ABILITY_BAR_SPRITE = Synthetics.rl("hud/ability_bar");
    private static final ResourceLocation ABILITY_BAR_SELECTED_SPRITE = Synthetics.rl("hud/ability_selected");

    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if(this.mc.player != null && this.mc.gameMode != null && this.mc.gameMode.getPlayerMode() != GameType.SPECTATOR && this.mc.player.isAlive()  && !this.mc.options.hideGui) {
            AbilityManager manager = SyntheticsPlayer.get(mc.player).getAbilityManager();
            SyntheticAbilityActiveInstance[] abilities = manager.getActiveAbilities().toArray(new SyntheticAbilityActiveInstance[0]);
            if(abilities.length == 0) return;
            int i = guiGraphics.guiWidth() / 2;
            int x = ClientConfig.abilityCarouselMiddleX.get();
            int y = ClientConfig.abilityCarouselMiddleY.get();

            int selectedAbility = SyntheticsClient.getInstance().getManager().selectedAbility;

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
                this.renderAbility(guiGraphics, j1, k1, toRender[i1], manager);
            }
            RenderSystem.disableBlend();

        }
    }
    private void renderAbility(GuiGraphics guiGraphics, int x, int y, SyntheticAbilityActiveInstance instance, AbilityManager abilities) {
        if(instance != null) {
            ResourceLocation id = instance.getAbility().getAbilityID();
            ResourceLocation texture = id.withPath("textures/abilities/" + id.getPath() + ".png");
            int percentage = 17;
            if(abilities.isAbilityActive(instance.getAbility())) {
                percentage = (int) ((1 - abilities.getPercentageForAbilityTime(instance)) * 17);
            } else if(abilities.isAbilityOnCooldown(instance.getAbility())) {
                percentage = (int) ((1 + abilities.getPercentageForAbilityTime(instance)) * 17);
            }

            guiGraphics.setColor(1, 1, 1, 0.5f);
            guiGraphics.fillGradient(x, y + percentage, x + 16, y + 17, 0xFF878787, 0xFF5E5E5E);
            guiGraphics.blit(texture, x, y, 0, 0, 0, 16, 16, 16, 16);
            guiGraphics.setColor(1, 1, 1, 1);

        }

    }
}
