package com.thedrofdoctoring.synthetics.body.abilities.passive.generators;

import com.thedrofdoctoring.synthetics.body.abilities.passive.IAbilityEventListener;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticAbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticPassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SolarGeneratorAbility extends SyntheticPassiveAbilityType implements IAbilityEventListener {

    public SolarGeneratorAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onTick(SyntheticAbilityPassiveInstance instance, int count,SyntheticsPlayer player) {
        int powerGain = (int) (instance.getAbilityFactor() * count);
        ResourceKey<Level> dimension = player.getEntity().level().dimension();
        if(dimension.equals(Level.NETHER)) {
            player.getPowerManager().addPower(powerGain * 3);

        } else {
            if(canBlockSeeSun(player.getEntity().level(),    player.getEntity().blockPosition())) {
                if(dimension.equals(Level.OVERWORLD)) {
                    player.getPowerManager().addPower(powerGain * 10);
                } else {
                    player.getPowerManager().addPower(powerGain * 5);
                }

            } else {
                player.getPowerManager().addPower(powerGain);
            }

        }

    }


    // https://github.com/TeamLapen/Vampirism/blob/4130581c10fd883bb37af964287ae0063dd1ff5f/src/main/java/de/teamlapen/vampirism/util/Helper.java#L78
    @SuppressWarnings("deprecation")
    public static boolean canBlockSeeSun(@NotNull LevelAccessor world, @NotNull BlockPos pos) {
        if (pos.getY() >= world.getSeaLevel()) {
            return world.canSeeSky(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
            if (!world.canSeeSky(blockpos)) {
                return false;
            } else {
                int liquidBlocks = 0;
                for (blockpos = blockpos.below(); blockpos.getY() > pos.getY(); blockpos = blockpos.below()) {
                    BlockState state = world.getBlockState(blockpos);
                    if (state.liquid()) {
                        liquidBlocks++;
                        if (liquidBlocks >= 5) {
                            return false;
                        }
                    } else if (state.canOcclude() && (state.isFaceSturdy(world, pos, Direction.DOWN) || state.isFaceSturdy(world, pos, Direction.UP))) { //solid block blocks the light (fence is solid too?)
                        return false;
                    } else if (state.getLightBlock(world, blockpos) > 0) { //if not solid, but propagates no light
                        return false;
                    }
                }
                return true;
            }
        }
    }
}
