package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.blaze3d.shaders.FogShape;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.client.renderers.world.LinkableBlocksRenderer;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundClimbPacket;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundLinkedInputPacket;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Quaternionf;

import java.util.Collection;
import java.util.Optional;

import static com.thedrofdoctoring.synthetics.util.Helper.getRotDirection;

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

    @SubscribeEvent
    public void onRenderLevel(RenderLevelStageEvent event) {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            LinkableBlocksRenderer.render(event.getPoseStack(), event.getCamera());
        }
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGuiLayerEvent.Pre event) {
        if(event.getName() == VanillaGuiLayers.EXPERIENCE_BAR && SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).isNotViewingSelf) {
            event.setCanceled(true);
        }
    }


    // Renders a player sideways, when on a wall with the magnetic wall climbing augment.
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Optional<BlockPos> posOpt = event.getEntity().getLastClimbablePos();
        // This condition is awful:
        //  On the local player, we check if the player is on a wall, and if they're colliding with the wall, or crouched stationary on it.
        //  On the remote player, we check if the rendered player is on a block border, and is not on the ground
        if (SyntheticsPlayerCache.get(event.getEntity()).onWall && posOpt.isPresent()
                        || (event.getEntity() instanceof RemotePlayer && Helper.onBlockBorder(event.getEntity(), event.getEntity().position()) && !event.getEntity().onGround())) {
            Vec3 playerPos = event.getEntity().position();
            float[] direction = getRotDirection(playerPos);
            event.getPoseStack().translate(direction[3], 0, direction[5]);
            Quaternionf rotation = new Quaternionf()
                    .rotationTo(0, 1, 0, direction[0], direction[1], direction[2]);
            event.getPoseStack().mulPose(rotation);
            event.getEntity().walkAnimation.setSpeed((float) event.getEntity().getDeltaMovement().y * 2);
        } else if(SyntheticsPlayerCache.get(event.getEntity()).onRoof) {
            Quaternionf rotation = new Quaternionf()
                    .rotationTo(0, 1, 0, 0, -1, 0);
            event.getPoseStack().translate(0, syntheticsPlayer.getEntity().getBbHeight() * 1.3, 0);
            event.getPoseStack().mulPose(rotation);
        }

    }

    public static void updateServerPlayerWallClimb(Player player) {
        if(player instanceof LocalPlayer local) {
            SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player);
            local.connection.send(new ServerboundClimbPacket(cache.onWall, cache.onRoof));
        }
    }


    @SubscribeEvent
    public void onPlayerInteract(InputEvent.InteractionKeyMappingTriggered event) {

        if(mc.player == null) return;

        if(SyntheticsPlayerCache.get(mc.player).isNotViewingSelf) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInput(MovementInputUpdateEvent event) {
        Input input = event.getInput();

        if(mc.player == null) return;

        if(SyntheticsPlayerCache.get(mc.player).isNotViewingSelf) {
            float sidewaysMotion = (
                    event.getInput().left ? 1f :
                            event.getInput().right ? -1f : 0 );
            float forwardMotion = (
                    event.getInput().up ? 1f :
                            event.getInput().down ? -1f : 0 );
            ServerboundLinkedInputPacket packet = new ServerboundLinkedInputPacket(sidewaysMotion, forwardMotion, mc.player.getXRot(), mc.player.getYRot(), mc.player.getYHeadRot(), input.jumping, input.shiftKeyDown, mc.options.keySprint.isDown());
            mc.player.connection.send(packet);
            stopInput(input);
        }

    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(mc.player == null) return;
        if(SyntheticsPlayerCache.get(mc.player).isNotViewingSelf) {
            event.setCanceled(true);
        }
    }

    public void stopInput(Input input) {
        input.up = false;
        input.down = false;
        input.right = false;
        input.left = false;
        input.jumping = false;
        input.forwardImpulse = 0f;
        input.leftImpulse = 0f;
    }

}
