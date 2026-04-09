package com.thedrofdoctoring.synthetics.capabilities.cache;

import com.thedrofdoctoring.synthetics.abilities.active.instances.BlockHighlightAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import it.unimi.dsi.fastutil.doubles.DoubleObjectImmutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BlockHighlightCache {

    private final HashSet<BlockPos> toHighlight;
    private final List<DoubleObjectImmutablePair<HolderSet<Block>>> whitelistedBlocks;
    public boolean shouldDraw = false;

    public BlockHighlightCache() {
        this.toHighlight = new HashSet<>();
        this.whitelistedBlocks = new ArrayList<>();
    }

    public void clear() {
        this.toHighlight.clear();
    }

    public void remove(BlockPos pos) {
        toHighlight.remove(pos);
    }

    public boolean isToHighlight(BlockPos pos) {
        return toHighlight.contains(pos);
    }

    public double maxRadius() {
        double max = 0;
        for(var pair : whitelistedBlocks) {
            max = Math.max(pair.leftDouble(), max);
        }
        return max;
    }

    public void addPositions(Collection<BlockPos> positions) {
        this.toHighlight.addAll(positions);
    }
    public void addPosition(BlockPos position) {
        this.toHighlight.add(new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    public Set<BlockPos> toHighlight() {
        return toHighlight;
    }

    public List<DoubleObjectImmutablePair<HolderSet<Block>>> allWhitelist() {
        return whitelistedBlocks;
    }

    public void rebuildWhitelist(SyntheticsPlayer player) {
        this.whitelistedBlocks.clear();

        var toAdd = player.getAbilityManager().getActiveAbilities()
                .stream()
                .filter(inst -> inst instanceof BlockHighlightAbilityInstance)
                .map(inst -> {
                    BlockHighlightAbilityInstance instance = (BlockHighlightAbilityInstance) inst;
                    return new DoubleObjectImmutablePair<>(instance.radius(), instance.toHighlight());
                })
                .toList();
        whitelistedBlocks.addAll(toAdd);
    }


    public boolean isInWhitelist(BlockState state, double distance) {
        for(var pair : whitelistedBlocks) {
            if(pair.leftDouble() >= distance && state.is(pair.right())) {
                return true;
            }
        }
        return false;
    }

}
