package com.thedrofdoctoring.synthetics.blocks.entities.linkables;

import com.thedrofdoctoring.synthetics.blocks.linkables.RedstoneLinkableBlock;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneLinkableBlockEntity extends LinkableBlockEntity {

    public RedstoneLinkableBlockEntity(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.REDSTONE_LINKABLE.get(), pos, blockState);
    }

    @Override
    public void onLinkedInteract(Player player, @Nullable ItemStack stack, @NotNull InteractionHand hand) {
        if(!player.level().isClientSide && hand == InteractionHand.MAIN_HAND
                && this.level != null && getBlockState().getBlock() instanceof RedstoneLinkableBlock block) {
            block.toggle(level, worldPosition, getBlockState());
        }
    }

    @Override
    public boolean canPlayerInteract(Player player) {
        return SyntheticsPlayer.get(player).getAbilityManager().hasAbility(Abilities.CYBERNETIC_REDSTONE_LINK);
    }
}
