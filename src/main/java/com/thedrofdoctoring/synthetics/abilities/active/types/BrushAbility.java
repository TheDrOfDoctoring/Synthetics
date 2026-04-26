package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BrushAbility extends StandardLastingAbility {
    public BrushAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        int factor = (int) instance.factor();
        if (syntheticsPlayer.getEntity().tickCount % factor == 0) {
            Player player = syntheticsPlayer.getEntity();
            Level level = player.level();
            HitResult hitResult = calculateHitResult(player);
            if (hitResult instanceof BlockHitResult blockhitresult && hitResult.getType() == HitResult.Type.BLOCK) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockpos = blockhitresult.getBlockPos();
                    BlockState blockstate = level.getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if(!(block instanceof BrushableBlock brushableBlock)) return false;
                    if (blockstate.shouldSpawnTerrainParticles() && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                        this.spawnDustParticles(level, blockhitresult, blockstate, player.getViewVector(0.0F));
                    }
                    SoundEvent soundevent = brushableBlock.getBrushSound();

                    level.playSound(player, blockpos, soundevent, SoundSource.BLOCKS);
                    if (!level.isClientSide()) {
                        BlockEntity be = level.getBlockEntity(blockpos);
                        if (be instanceof BrushableBlockEntity brushableblockentity) {
                            brushableblockentity.brushCount++;
                            brushableblockentity.brush(level.getGameTime(), player, blockhitresult.getDirection());
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {

    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {

    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {

    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    private HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(player, (entity) -> !entity.isSpectator() && entity.isPickable(), player.blockInteractionRange());
    }

    private void spawnDustParticles(Level level, BlockHitResult hitResult, BlockState state, Vec3 pos) {
        int i = 1;
        int j = level.getRandom().nextInt(7, 12);
        BlockParticleOption blockparticleoption = new BlockParticleOption(ParticleTypes.BLOCK, state);
        Direction direction = hitResult.getDirection();
        BrushItem.DustParticlesDelta brushitem$dustparticlesdelta = BrushItem.DustParticlesDelta.fromDirection(pos, direction);
        Vec3 vec3 = hitResult.getLocation();

        for(int k = 0; k < j; ++k) {
            level.addParticle(blockparticleoption, vec3.x - (double)(direction == Direction.WEST ? 1.0E-6F : 0.0F), vec3.y, vec3.z - (double)(direction == Direction.NORTH ? 1.0E-6F : 0.0F), brushitem$dustparticlesdelta.xd() * (double)i * (double)3.0F * level.getRandom().nextDouble(), 0.0F, brushitem$dustparticlesdelta.zd() * (double)i * (double)3.0F * level.getRandom().nextDouble());
        }

    }
}
