package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.linkables.LinkableBlock;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.entities.DummyCameraEntity;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLinkedInteractPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static net.minecraft.world.level.BlockGetter.traverseBlocks;

public class InteractLinkedAbility extends ActiveAbilityType<AbilityActiveInstance.Data> {
    public InteractLinkedAbility(ResourceLocation id) {
        super(id);
    }
    // Maybe should be configurable, but fine for now
    private static final double ACTIVATION_DISTANCE = 110d;

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance.Data abilityData) {
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
            BlockHitResult hit = isBlockInLine(serverPlayer.level(), new ClipBlockStateContext(eyePos, lookVector.scale(ACTIVATION_DISTANCE).add(eyePos), state -> state.getBlock() instanceof LinkableBlock));
            if(hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hit.getBlockPos();
                BlockEntity be = serverPlayer.level().getBlockEntity(pos);
                if(be instanceof LinkableBlockEntity linkable) {
                    linkable.onLinkedInteract(serverPlayer);
                    syntheticsPlayer.getEntity().playNotifySound(SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.MASTER,1f, 1.25f);
                    serverPlayer.connection.send(new ClientboundLinkedInteractPacket(linkable.getBlockPos()));
                }
            }
        }


        return false;
    }

    private static BlockHitResult isBlockInLine(Level level, ClipBlockStateContext context) {
        return traverseBlocks(context.getFrom(), context.getTo(), context, (matchContext, pos) -> {
            BlockState blockstate = level.getBlockState(pos);
            Vec3 vec3 = matchContext.getFrom().subtract(matchContext.getTo());
            return matchContext.isTargetBlock().test(blockstate) ? new BlockHitResult(matchContext.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), pos, false) : null;
        }, (failContext) -> {
            Vec3 vec3 = failContext.getFrom().subtract(failContext.getTo());
            return BlockHitResult.miss(failContext.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), BlockPos.containing(failContext.getTo()));
        });
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData() instanceof AbilityActiveInstance.Data data) {
            description.add(Component.translatable("abilities.synthetics.description.cooldown", data.options().cooldown()).withStyle(ChatFormatting.BLUE));
        }
    }
}
