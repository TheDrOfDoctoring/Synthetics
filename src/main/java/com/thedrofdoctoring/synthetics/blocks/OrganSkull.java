package com.thedrofdoctoring.synthetics.blocks;

import com.thedrofdoctoring.synthetics.blocks.entities.OrganSkullBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class OrganSkull extends SkullBlock {
    public OrganSkull(Type type, Properties properties) {
        super(type, properties);
    }

    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new OrganSkullBlockEntity(pos, state);
    }
}
