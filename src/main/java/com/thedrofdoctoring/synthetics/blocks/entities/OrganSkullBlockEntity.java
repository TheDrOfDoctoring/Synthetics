package com.thedrofdoctoring.synthetics.blocks.entities;

import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class OrganSkullBlockEntity extends SkullBlockEntity {
    public OrganSkullBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    public @NotNull BlockEntityType<OrganSkullBlockEntity> getType() {
        return SyntheticsBlockEntities.ORGAN_SKULL.get();
    }
}
