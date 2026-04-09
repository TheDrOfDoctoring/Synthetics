package com.thedrofdoctoring.synthetics.blocks.entities;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.AugmentationChamber;
import com.thedrofdoctoring.synthetics.blocks.TableBlock;
import com.thedrofdoctoring.synthetics.config.CommonConfig;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PermeableBlockEntity extends BlockEntity {
    public PermeableBlockEntity(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.PERMEABLE.get(), pos, blockState);
    }

    private static final String ORIGINAL_BLOCKSTATE = "original_blockstate";
    private static final String ORIGINAL_NBT_DATA   = "original_be_data";

    private BlockState originalBlockState;
    private CompoundTag originalBEData;

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains(ORIGINAL_NBT_DATA, CompoundTag.TAG_COMPOUND)) {
            this.originalBEData = tag.getCompound(ORIGINAL_NBT_DATA);
        }
        if(tag.contains(ORIGINAL_BLOCKSTATE, CompoundTag.TAG_COMPOUND)) {
            CompoundTag stateData = tag.getCompound(ORIGINAL_BLOCKSTATE);
            HolderGetter<Block> lookup = this.level == null ? BuiltInRegistries.BLOCK.asLookup() : this.level.holderLookup(Registries.BLOCK);
            this.originalBlockState = NbtUtils.readBlockState(lookup, stateData);
        } else if(this.level != null) {
            level.setBlock(this.worldPosition, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if(this.originalBEData != null) {
            tag.put(ORIGINAL_NBT_DATA, originalBEData);
        }
        if(this.originalBlockState != null) {
            tag.put(ORIGINAL_BLOCKSTATE, NbtUtils.writeBlockState(this.originalBlockState));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return super.getUpdateTag(registries);
    }

    public static void set(Level level, BlockPos pos, HolderSet<Block> inBlacklist) {
        BlockState state = level.getBlockState(pos);
        BlockEntity originalBE = level.getBlockEntity(pos);
        if(!isValidPosition(state, originalBE, inBlacklist)) {
            return;
        }

        level.setBlock(pos, SyntheticsBlocks.PERMEABLE_BLOCK.get().defaultBlockState(), 3);
        if(level.getBlockEntity(pos) instanceof PermeableBlockEntity permeableBE) {
            if(originalBE != null) {
                permeableBE.originalBEData = originalBE.saveWithFullMetadata(level.registryAccess());
            }
            permeableBE.originalBlockState = state;
        }

    }

    public void reset() {
        if(this.level == null || this.originalBlockState == null) {
            Synthetics.LOGGER.warn("Failed to reset Permeable Block to original state at {}", this.worldPosition);
            return;
        }

        this.level.setBlock(this.worldPosition, this.originalBlockState, 3);
        this.level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, this.getBlockPos(), Block.getId(this.originalBlockState));
        BlockEntity replacement = this.level.getBlockEntity(this.worldPosition);
        if(this.originalBEData != null && replacement != null) {
            replacement.loadWithComponents(this.originalBEData, this.level.registryAccess());
        } else if(replacement == null && originalBEData != null) {
            Synthetics.LOGGER.warn("Failed to reset Permeable Block to original state at {}", this.worldPosition);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean isValidPosition(BlockState state, BlockEntity be, HolderSet<Block> inBlacklist) {
        if(state.is(inBlacklist) || state.is(SyntheticsBlocks.PERMEABLE_BLOCK.get())) return false;
        if(!CommonConfig.permeableBlockBE.get() && be != null) return false;

        Block block = state.getBlock();
        if(block instanceof DoorBlock || block instanceof DoublePlantBlock || block instanceof TableBlock || block instanceof AugmentationChamber || block instanceof BedBlock) {
            return false;
        }

        return true;
    }

    public static boolean isValidPosition(Level level, BlockPos pos, HolderSet<Block> additionalBlacklist) {
        return isValidPosition(level.getBlockState(pos), level.getBlockEntity(pos), additionalBlacklist);
    }
}
