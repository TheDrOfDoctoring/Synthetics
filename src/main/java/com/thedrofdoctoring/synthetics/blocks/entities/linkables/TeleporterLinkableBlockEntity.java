package com.thedrofdoctoring.synthetics.blocks.entities.linkables;


import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TeleporterLinkableBlockEntity extends LinkableBlockEntity {


    public TeleporterLinkableBlockEntity(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.TELEPORTER_LINKABLE.get(), pos, blockState);
    }

    @Override
    public void onLinkedInteract(Player player, @Nullable ItemStack stack, @NotNull InteractionHand hand) {
        if(!player.level().isClientSide && hand == InteractionHand.MAIN_HAND && this.level != null) {
            Optional<AbilityActiveInstance<?>> instance = SyntheticsPlayer.get(player).getAbilityManager().activeInstances()
                    .filter(inst -> inst.type().equals(SyntheticAbilities.TELEPORT_LINKED.get()))
                    .findFirst();
            if (player instanceof ServerPlayer serverPlayer
                    && instance.isPresent()
                    && SyntheticsPlayer.get(player).getPowerManager().tryDrainPower(instance.orElseThrow().getPowerDrain())) {
                BlockPos pos = this.worldPosition.above();
                serverPlayer.removeVehicle();
                serverPlayer.teleportTo(pos.getX(), pos.getY() + 0.1, pos.getZ());
                this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.75f, 1.75f);
            }
        }
    }

    @Override
    public boolean canPlayerInteract(Player player) {
        SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);
        Optional<AbilityActiveInstance<?>> instance = syntheticsPlayer.getAbilityManager().activeInstances()
                .filter(inst -> inst.type().equals(SyntheticAbilities.TELEPORT_LINKED.get()))
                .findFirst();
        if(instance.isPresent()) {
            return syntheticsPlayer.getPowerManager().hasSufficientPower(instance.orElseThrow().getPowerDrain());
        }
        return false;
    }
}
