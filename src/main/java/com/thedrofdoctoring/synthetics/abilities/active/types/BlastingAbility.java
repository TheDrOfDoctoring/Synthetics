package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.BlastingAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsSounds;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class BlastingAbility extends ActiveAbilityType<BlastingAbilityInstance> {
    public BlastingAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, BlastingAbilityInstance abilityData) {
        Player player = syntheticsPlayer.getEntity();
        Level level = player.level();
        HitResult hitResult = Helper.calculateHitResult(player);
        if (hitResult instanceof BlockHitResult blockhitresult && hitResult.getType() == HitResult.Type.BLOCK) {
            Direction direction  = blockhitresult.getDirection().getOpposite();
            BlockPos pos = blockhitresult.getBlockPos();
            Vector3f newPos = direction.step().mul((float) abilityData.length());
            AABB aabb = new AABB(pos.getCenter(), pos.getCenter())
                    .expandTowards(new Vec3(newPos));
            aabb = inflated(direction.step(), abilityData.radius(), aabb);
            BlockPos.betweenClosedStream(aabb)
                    .filter(position -> canBreak(level.getBlockState(position), abilityData.invalidBlocks()))
                    .forEach(position -> level.destroyBlock(position, true, player));
            player.level().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SyntheticsSounds.BLASTING, SoundSource.PLAYERS, 0.5f, 1.5f);
            return true;
        }
        return false;
    }

    private AABB inflated(Vector3f dir, double radius, AABB aabb) {
        double rad = radius / 2;
        return aabb.inflate(dir.x * rad, dir.y * rad, dir.z * rad);
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    private boolean canBreak(BlockState state, TagKey<Block> incorrect) {
        if(state.is(incorrect)) {
            return false;
        }
        if(state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return true;
        }
        return false;
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof BlastingAbilityInstance.Data activeData) {
            return Optional.of(new BlastingAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData() instanceof BlastingAbilityInstance.Data data) {
            description.add(Component.translatable("abilities.synthetics.description.length", data.factor()).withStyle(ChatFormatting.BLUE));
            description.add(Component.translatable("abilities.synthetics.description.radius", data.radius()).withStyle(ChatFormatting.BLUE));
        }
        ActiveAbilityType.activeAbilityDescription(ability, description);
    }

    public static BlastingAbilityInstance.Data create(double length, ActiveAbilityOptions options, double radius, Tier tier) {
        return new BlastingAbilityInstance.Data(length, options, radius, tier.getIncorrectBlocksForDrops());
    }
}
