package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.blaze3d.shaders.FogShape;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FogType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Collection;

public class SyntheticsClientEventHandler {

    private final Minecraft mc;
    private SyntheticsPlayer syntheticsPlayer;

    private SyntheticsClientEventHandler() {
        this.mc = Minecraft.getInstance();
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(new SyntheticsClientEventHandler());
    }

    private SyntheticsPlayer getSyntheticsPlayer() {

        if(mc.player != null) {
            if(syntheticsPlayer != null) {
                return syntheticsPlayer;
            }
            this.syntheticsPlayer = SyntheticsPlayer.get(mc.player);
            return this.syntheticsPlayer;
        }
        return null;

    }

    @SubscribeEvent
    public void gameRenderEvent(ViewportEvent.RenderFog event) {
        SyntheticsPlayer player = getSyntheticsPlayer();
        if (player != null) {
            if (event.getType() == FogType.WATER) {
                Collection<AbilityPassiveInstance<?>> abilities = player.getAbilityManager().getPassiveAbilities();
                double viewDistance = event.getFarPlaneDistance();
                for(AbilityPassiveInstance<?> instance : abilities) {
                    if(instance.getAbility().equals(SyntheticAbilities.UNDERWATER_VISION.get())) {
                        viewDistance += instance.factor();
                    }
                }
                event.setCanceled(true);
                event.setFogShape(FogShape.SPHERE);
                event.setNearPlaneDistance(0.1f);
                event.setFarPlaneDistance((float) viewDistance);
            }
        }

    }
}
