package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ItemMagnetAbility extends StandardLastingAbility {
    public ItemMagnetAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {

        if(syntheticsPlayer.getEntity().tickCount % 2 == 0 && !syntheticsPlayer.getEntity().level().isClientSide) {
            Player player = syntheticsPlayer.getEntity();
            AABB aabb = new AABB(player.blockPosition()).inflate(instance.factor());
            List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, aabb);
            for(ItemEntity entity : items) {
                Vec3 vec = player.position().subtract(entity.position()).normalize().scale(0.25f);
                entity.setTarget(player.getUUID());
                if(entity.distanceTo(player) <= 1.5f) {
                    entity.setDeltaMovement(vec.scale(0.1f));
                    entity.playerTouch(player);
                } else {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(vec));
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
}
