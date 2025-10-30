package com.thedrofdoctoring.synthetics.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.capabilities.PowerManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public class EnergyOverlay implements LayeredDraw.Layer {

    private static final ResourceLocation ENERGY_BAR_SPRITE = Synthetics.rl("hud/energy_overlay");

    private final Minecraft mc = Minecraft.getInstance();
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (this.mc.player != null && this.mc.gameMode != null && this.mc.gameMode.getPlayerMode() != GameType.SPECTATOR && this.mc.player.isAlive() && !this.mc.options.hideGui) {
            if(!SyntheticsClient.getInstance().getManager().displayEnergy) {
                return;
            }
            PowerManager manager = SyntheticsPlayer.get(mc.player).getPowerManager();
            if (manager.getMaxPower() == 0) {
                return;
            }
            int i = guiGraphics.guiWidth() / 2;
            int x = i - ClientConfig.energyOverlayMiddleX.get();
            int y = guiGraphics.guiHeight() - ClientConfig.energyOverlayMiddleY.get();

            guiGraphics.pose().pushPose();

            if (ClientConfig.energyOverlayRotation.get() != 0) {
                float centerX = i - x + 62 / 2f;
                float centerY = guiGraphics.guiHeight() - y + 22 / 2f;
                guiGraphics.pose().translate(centerX, centerY, 0);
                guiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees(ClientConfig.energyOverlayRotation.get()), 0, 0, 0);
                guiGraphics.pose().translate(-centerX, -centerY, 0);
            }

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);
            guiGraphics.blitSprite(ENERGY_BAR_SPRITE, x, y, 62, 12);
            float energyPercentage = (float) manager.getStoredPower() / manager.getMaxPower();
            guiGraphics.fillGradient(x + 4, y + 2, (x + 3 + Math.round(56 * energyPercentage)), y + 10, 0xFF64e3a1, 0xFF198450);


            guiGraphics.pose().popPose();


            guiGraphics.pose().popPose();
            RenderSystem.disableBlend();

        }
    }
}
