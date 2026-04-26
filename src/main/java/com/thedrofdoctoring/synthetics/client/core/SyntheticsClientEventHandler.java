package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.blaze3d.shaders.FogShape;
import com.thedrofdoctoring.synthetics.abilities.active.types.BlockHighlightAbility;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.client.renderers.world.EntityHighlightingRenderer;
import com.thedrofdoctoring.synthetics.client.renderers.world.HighlightedBlocksRenderer;
import com.thedrofdoctoring.synthetics.client.renderers.world.LinkableBlocksRenderer;
import com.thedrofdoctoring.synthetics.core.SyntheticsSounds;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundClimbPacket;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundLinkedInputPacket;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundLinkedUsePacket;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.Collection;
import java.util.Optional;

import static com.thedrofdoctoring.synthetics.util.Helper.getRotDirection;

public class SyntheticsClientEventHandler implements ResourceManagerReloadListener {

    public static final SyntheticsClientEventHandler INSTANCE = new SyntheticsClientEventHandler();

    private final Minecraft mc;
    private final EntityHighlightingRenderer highlightRenderer;
    private SyntheticsPlayer syntheticsPlayer;

    private static Input lastInput;

    private SyntheticsClientEventHandler() {
        this.mc = Minecraft.getInstance();
        this.highlightRenderer = new EntityHighlightingRenderer(mc);
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(INSTANCE);
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
                    if(instance.type().equals(SyntheticAbilities.UNDERWATER_VISION.get())) {
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
        this.highlightRenderer.onRenderLevelEvent(event);
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            LinkableBlocksRenderer.render(event.getPoseStack(), event.getCamera());
            HighlightedBlocksRenderer.render(event.getPoseStack(), event.getCamera());
        }
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        this.highlightRenderer.onRenderLiving(event);
    }

    @SubscribeEvent
    public void onComputeCamera(ViewportEvent.ComputeCameraAngles event) {
        this.highlightRenderer.onComputeCamera(event);
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGuiLayerEvent.Pre event) {
        if(event.getName() == VanillaGuiLayers.EXPERIENCE_BAR && SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).isNotViewingSelf) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Pre event) {
        if(mc.level == null || mc.player == null) return;
        BlockHighlightAbility.tick(mc.player);
        this.highlightRenderer.onClientTick(event);

    }

    public static void onBlockBreak(BlockPos pos) {
        BlockHighlightAbility.blockDestroyed(pos, Minecraft.getInstance().player);
    }


    // Renders a player sideways, when on a wall with the magnetic wall climbing augment.
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if(SyntheticsPlayerCache.get(event.getEntity()).invisible) {
            event.setCanceled(true);
            return;
        }
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
            mc.player.connection.send(new ServerboundLinkedUsePacket(event.isAttack()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInput(MovementInputUpdateEvent event) {
        handlePlayerInput(mc, event);
    }

    public static boolean handlePlayerInput(Minecraft mc, MovementInputUpdateEvent event) {
        Input input = event.getInput();

        if(mc.player == null) return false;
        lastInput = input;
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(mc.player);

        if(cache.lockedInPlace && !cache.isNotViewingSelf) {
            stopInput(input);
            return false;
        }

        if(cache.isNotViewingSelf) {
            ServerboundLinkedInputPacket packet = getServerboundLinkedInputPacket(event, input, mc.player, mc);
            mc.player.connection.send(packet);
            stopInput(input);
            return false;
        }

        return true;
    }

    private static @NotNull ServerboundLinkedInputPacket getServerboundLinkedInputPacket(MovementInputUpdateEvent event, Input input, @NotNull Player player, Minecraft mc) {
        float sidewaysMotion = (
                event.getInput().left ? 1f :
                        event.getInput().right ? -1f : 0 );
        float forwardMotion = (
                event.getInput().up ? 1f :
                        event.getInput().down ? -1f : 0 );
        return new ServerboundLinkedInputPacket(sidewaysMotion, forwardMotion, player.getXRot(), player.getYRot(), player.getYHeadRot(), input.jumping, input.shiftKeyDown, mc.options.keySprint.isDown());
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(mc.player == null) return;
        if(SyntheticsPlayerCache.get(mc.player).isNotViewingSelf) {
            event.setCanceled(true);
        }
    }

    private static void stopInput(Input input) {
        input.up = false;
        input.down = false;
        input.right = false;
        input.left = false;
        input.jumping = false;
        input.forwardImpulse = 0f;
        input.leftImpulse = 0f;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        this.highlightRenderer.onResourceManagerReload(resourceManager);
    }

    public static void handleRocketFlight(Player player, double factor) {
        if(player.onGround() || !player.isLocalPlayer()) return;
        if(lastInput.jumping) {
            double magnitude = 0.25 * (player.isCrouching() ? factor / 2 : factor);
            if(player.isInLiquid()) {
                magnitude *= 0.25f;
            }
            Vec3 delta = player.getDeltaMovement();
            player.setDeltaMovement(delta.x(), magnitude, delta.z());
            if(player.tickCount % 9 == 0) {
                player.playSound(SyntheticsSounds.ROCKET_FLIGHT.get(), 0.25f, 2.0f);
            }
        }
    }



}
