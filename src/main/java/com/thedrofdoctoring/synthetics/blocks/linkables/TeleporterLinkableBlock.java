package com.thedrofdoctoring.synthetics.blocks.linkables;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.TeleporterLinkableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class TeleporterLinkableBlock extends LinkableBlock {

    public static final MapCodec<TeleporterLinkableBlock> CODEC = simpleCodec(TeleporterLinkableBlock::new);

    public TeleporterLinkableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BiFunction<BlockPos, BlockState, ? extends LinkableBlockEntity> createLinkableBlockEntity() {
        return TeleporterLinkableBlockEntity::new;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
