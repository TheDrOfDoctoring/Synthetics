package com.thedrofdoctoring.synthetics.items;

import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class DroneItem extends Item {
    public DroneItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if(context.getClickedFace() == Direction.UP) {
            BlockPos pos = context.getClickedPos();
            DroneEntity drone = DroneEntity.create(context.getLevel());
            drone.setPos(pos.above().getCenter());
            context.getLevel().addFreshEntity(drone);
            context.getItemInHand().shrink(1);
        }
        return super.useOn(context);
    }
}
