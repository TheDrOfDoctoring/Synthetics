package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.BlockPermeatorAbilityInstance;
import com.thedrofdoctoring.synthetics.blocks.entities.PermeableBlockEntity;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import com.thedrofdoctoring.synthetics.util.Helper;
import com.thedrofdoctoring.synthetics.world.data.PermeableBlocksData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BlockPermeatorAbility extends ActiveAbilityType<BlockPermeatorAbilityInstance> {
    public BlockPermeatorAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, BlockPermeatorAbilityInstance abilityData) {
        if(syntheticsPlayer.getEntity() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {

            Vec3 lookVector = player.getViewVector(1.0f);
            Vec3 eyePos = player.getEyePosition();
            if (player.getCamera() != player) {
                return false;
            }
            BlockHitResult hit = Helper.isBlockInLine(player.level(), new ClipBlockStateContext(eyePos, lookVector.scale(player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)).add(eyePos), state -> true));
            Direction direction = hit.getDirection().getOpposite();
            BlockPos pos = hit.getBlockPos().relative(direction, (int) abilityData.radius());

            Vector3f newPos = direction.step().mul((float) abilityData.length());
            AABB aabb = new AABB(pos)
                    .expandTowards(new Vec3(newPos))
                    .inflate(abilityData.radius() / 2);
            Stream<BlockPos> positions = BlockPos.betweenClosedStream(aabb);
            HolderSet<Block> blacklist = abilityData.blacklistedBlocks();
            PermeableBlocksData.PermeableBlockData positionData = new PermeableBlocksData.PermeableBlockData(positions
                    .map(BlockPos::immutable)
                    .filter(p -> PermeableBlockEntity.isValidPosition(level, p, blacklist))
                    .toList(), abilityData.getDuration() * 20);
            PermeableBlocksData data = PermeableBlocksData.getData(level.getServer(), level.dimension());
            if(data != null) {
                data.addData(player.getUUID(), positionData);
            }
            positionData.positions().forEach(pos1 -> PermeableBlockEntity.set(level, pos1, blacklist));
            player.level().playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1f, 0.25f);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if (ability.abilityData() instanceof BlockPermeatorAbilityInstance.Data activeData) {
            ActiveAbilityOptions options = activeData.options();
            description.add(Component.translatable("abilities.synthetics.description.cooldown", options.cooldown()).withStyle(ChatFormatting.BLUE));
            if (options.duration() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.duration", options.duration()).withStyle(ChatFormatting.BLUE));
            }
            if (options.powerCost() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.power_cost", options.powerCost()).withStyle(ChatFormatting.BLUE));
            }
            description.add(Component.translatable("abilities.synthetics.description.radius", activeData.radius()).withStyle(ChatFormatting.BLUE));
            description.add(Component.translatable("abilities.synthetics.description.length", activeData.length()).withStyle(ChatFormatting.BLUE));

        }
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof BlockPermeatorAbilityInstance.Data activeData) {
            return Optional.of(new BlockPermeatorAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static BlockPermeatorAbilityInstance.Data create(double length, ActiveAbilityOptions options, float radius, HolderSet<Block> blockBlacklist) {
        return new BlockPermeatorAbilityInstance.Data(length, options, radius, blockBlacklist);
    }
}
