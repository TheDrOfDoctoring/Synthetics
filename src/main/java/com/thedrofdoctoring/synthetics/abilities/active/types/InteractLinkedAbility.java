package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardActiveAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.linkables.LinkableBlock;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.entities.linkable.DummyCameraEntity;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLinkedInteractPacket;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class InteractLinkedAbility extends StandardActiveAbility {
    public InteractLinkedAbility(ResourceLocation id) {
        super(id);
    }
    // Consider making this configurable I think
    private static final double ACTIVATION_DISTANCE = 110d;

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        if(syntheticsPlayer.getEntity() instanceof ServerPlayer serverPlayer) {
            Vec3 lookVector = syntheticsPlayer.getEntity().getViewVector(1.0f);
            Vec3 eyePos = syntheticsPlayer.getEntity().getEyePosition();
            if(serverPlayer.getCamera() != serverPlayer) {
                Entity camera = serverPlayer.getCamera();
                lookVector = camera.getViewVector(0f);
                eyePos = camera.getEyePosition();
                if(camera instanceof DummyCameraEntity) {
                    eyePos = eyePos.add(lookVector.scale(1.5f));
                }
            }
            BlockHitResult hit = Helper.isBlockInLine(serverPlayer.level(), new ClipBlockStateContext(eyePos, lookVector.scale(ACTIVATION_DISTANCE).add(eyePos), state -> state.getBlock() instanceof LinkableBlock));
            if(hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hit.getBlockPos();
                BlockEntity be = serverPlayer.level().getBlockEntity(pos);
                if(be instanceof LinkableBlockEntity linkable && linkable.isLinked(serverPlayer)) {
                    linkable.onLinkedInteract(serverPlayer);
                    syntheticsPlayer.getEntity().playNotifySound(SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.MASTER,1f, 1.25f);
                    serverPlayer.connection.send(new ClientboundLinkedInteractPacket(linkable.getBlockPos()));
                    return true;
                }
            }
        }


        return false;
    }



    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData() instanceof AbilityActiveInstance.Data data) {
            description.add(Component.translatable("abilities.synthetics.description.cooldown", data.options().cooldown()).withStyle(ChatFormatting.BLUE));
            if(data.options().powerDrain() > 0) {
                // this is a bit of a hack, used for the power cost of a specific interaction, like the teleporter.
                description.add(Component.translatable("abilities.synthetics.description.power_cost", data.options().powerDrain()).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}
